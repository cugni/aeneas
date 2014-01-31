/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.util.concurrent.*;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import es.bsc.cassandrabm.codegenerator.query.QueryInterface;
import es.bsc.cassandrabm.codegenerator.query.annotations.TestInterface;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.workloader.controller.Client.Distribution;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public class TestImpl {

    private final static Logger log = Logger.getLogger(TestImpl.class.getName());
    private final String methodfilter = CUtils.getString("methodfilter");
    private final ImmutableList<MethodHolder> methods;
    final Counter completedQueries = Metrics.newCounter(TestImpl.class, "completed-query");
    final Counter failedQueries = Metrics.newCounter(TestImpl.class, "failed-query");
    final Counter timeoutQueries = Metrics.newCounter(TestImpl.class, "timeout-query");
//    final Timer testTimer = Metrics.newTimer(TestImpl.class, "time-for-test");
    final int times;
    final int concurrency;
    final QueryInterface impl;
    final boolean continueonerror;
    final boolean continueontimeout;
    final CheckError error = new CheckError();
    final int tot_queries;
    final CountDownLatch remaining;
    final AtomicInteger per100 = new AtomicInteger(0);
    final BlockingQueue<MethodHolder> queue;
    final ListeningExecutorService service;

    //  private final Timer keygeneratioTimer = Metrics.newTimer(TestImpl.class, "key-generation-time");
    TestImpl(QueryInterface impl, Distribution distribution,
            int times, int concurrency,
            boolean continueonerror,
            boolean continueontimeout) {
        this.impl = impl;
        this.times = times;
        this.concurrency = concurrency;
        queue = new ArrayBlockingQueue<MethodHolder>(concurrency);
        this.continueonerror = continueonerror;
        this.continueontimeout = continueontimeout;
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(concurrency));

        Class toscan = null;
        for (Class inter : impl.getClass().getInterfaces()) {
            if (!inter.equals(QueryInterface.class)
                    && inter.isAnnotationPresent(TestInterface.class)) {
                toscan = inter;
                break;
            }
        }
        Builder<MethodHolder> builder = ImmutableList.builder();
        //TimerContext time = keygeneratioTimer.time();
        try {
            for (Method met : checkNotNull(toscan, "wrong interface interitance").getMethods()) {
                if (methodfilter == null || met.getName().matches(methodfilter)) {
                    builder.add(new MethodHolder(met, distribution));
                }

            }
        } finally {
            //   time.stop();
        }
        methods = builder.build();
        tot_queries = new Double(times * methods.size()).intValue();
        remaining = new CountDownLatch(tot_queries);
    }

    /**
     * Executes the methods raising an FailedTestException in case of exception
     * in the called methods.
     *
     * @throws
     * es.bsc.cassandrabm.workloader.controller.TestImpl.FailedTestException
     */
    public void run() throws FailedTestException {

        Metrics.newGauge(TestImpl.class, "pending-method-invocation", new Gauge<Long>() {
            @Override
            public Long value() {
                return remaining.getCount(); //exposing the the remaing tasks metric
            }
        });

        Metrics.newGauge(TestImpl.class, "testing-completition", new Gauge<Integer>() {
            @Override
            public Integer value() {
                return per100.get(); //exposing the the remaing tasks metric
            }
        });
//        TimerContext time = testTimer.time();
        try {
            for (double i = 0; i < times; i++) {
                for (final MethodHolder mh : methods) {
                    //Every 10 seconds it checks if the execution had exceptions. 
                    while (!queue.offer(mh, 10, TimeUnit.SECONDS)) {
                        error.check();
                    }
                    error.check();
                    ListenableFuture<Map<String, Object>> submit = service.submit(new Callable<Map<String, Object>>() {
                        @Override
                        public Map<String, Object> call() throws Exception {
                            return queue.take().createInvokeTask(impl).call();

                        }
                    });
                    Futures.addCallback(submit, new FutureCallback<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            remaining.countDown();
                            completedQueries.inc();
                            int com = ((int) ((completedQueries.count()
                                    / (double) tot_queries) * 100)) % 100;
                            //TODO not really thread safe
                            if (com > per100.get()) {
                                per100.set(com);
                                log.log(Level.INFO, "Completed {0} on {1} queries ({2}%)",
                                        new Object[]{completedQueries.count(), tot_queries, per100.get()});
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            failedQueries.inc();
                            if (t.getCause() != null && t.getCause().getCause() != null
                                    && t.getCause().getCause() instanceof me.prettyprint.hector.api.exceptions.HTimedOutException) {
                                log.log(Level.SEVERE,
                                        "Time out  exception on method " + mh.toString(), t);
                                timeoutQueries.inc();
                                log.log(Level.SEVERE,
                                        "Timeout in one method caller"
                                        + " thread for method " + mh.toString(), t);
                                if (!continueontimeout) {
                                    error.error(t);
                                }
                            } else {
                                log.log(Level.SEVERE,
                                        "Exception in one method caller"
                                        + " thread for method " + mh.toString(), t);
                                if (!continueonerror) {
                                    error.error(t);
                                }
                            }
                            remaining.countDown();
                        }
                    });
                }
            }
            while (!remaining.await(10, TimeUnit.SECONDS)) {
                error.check();
                log.log(Level.INFO, "Waiting last queue termiantion: completed {0},remaining {1} ",
                        new Object[]{completedQueries.count(), remaining.getCount()});
            }
            error.check();
        } catch (InterruptedException ex) {
            throw new FailedTestException(
                    "Testing interrupted", error.t != null ? error.t : ex);
        } finally {
//            time.stop();
            service.shutdown();
            log.log(Level.INFO, "Test completed");
        }

    }

    private class CheckError {

        boolean flag = false;
        private Throwable t = null;

        public void error() {
            flag = true;
        }

        public void check() throws FailedTestException {
            if (flag) {
                log.log(Level.SEVERE,
                        "An error occured: test interrupted with {0} completed queries",
                        completedQueries.count());
                throw new FailedTestException("Exception occured in one of the threads", error.t);
            }
        }

        public boolean noError() {
            return !flag;
        }

        private void error(Throwable t) {
            flag = true;
            this.t = t;
            service.shutdownNow();
        }
    }

    public class FailedTestException extends Exception {

        public FailedTestException(String message) {
            super(message);
        }

        public FailedTestException(String message, Throwable cause) {
            super(message, cause);
        }

        public FailedTestException(Throwable cause) {
            super(cause);
        }
    }
}
