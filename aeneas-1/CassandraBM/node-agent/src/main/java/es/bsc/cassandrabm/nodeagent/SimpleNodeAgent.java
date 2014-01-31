package es.bsc.cassandrabm.nodeagent;

import com.google.common.util.concurrent.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.daemon.Daemon;

/**
 * This class implements the apache {@link Daemon} interface to handle the
 * Nodeagent life cycle.
 *
 * @author cesare
 */
public class SimpleNodeAgent
        extends NodeAgentService {

    private static final Logger log = Logger.getLogger("NodeAgent");
    final static List<Throwable> errors = new CopyOnWriteArrayList<Throwable>();
    private final ListeningExecutorService service = MoreExecutors
            .listeningDecorator(Executors.newFixedThreadPool(2));
    private List<Recorder> recorders = new ArrayList<Recorder>(2);
    private List<MetricsReader> metricsReaders = new ArrayList<MetricsReader>(2);
    private final AtomicBoolean running = new AtomicBoolean(false);
    final DynamicScheduler dyn = new DynamicScheduler();

    @Override
    public List<Recorder> getRecorders() {
        return recorders;
    }

    @Override
    public void setRecorders(List<Recorder> recorders) {
        this.recorders = recorders;
    }

    @Override
    public List<MetricsReader> getMetricsReaders() {
        return metricsReaders;
    }

    @Override
    public void setMetricsReaders(List<MetricsReader> mreaders) {
        this.metricsReaders = mreaders;
    }

    /**
     * This method returns as a string all the stack trace of the exception
     * occurred in the {@link SimpleNodeAgent}.
     *
     * @return
     */
    @Override
    public String getErrorsStackTrace() {
        if (!SimpleNodeAgent.errors.isEmpty()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            for (Throwable t : SimpleNodeAgent.errors) {
                t.printStackTrace(pw);
                sw.append("\n");
            }
            return sw.toString();

        } else {
            return "";
        }
    }

    @Override
    public void configure() throws Exception {

        if (conf.containsKey("scheduler.interval")) {
            dyn.setInterval(conf.getLong("scheduler.interval"));
        }
        if (conf.containsKey("scheduler.timeunit")) {
            dyn.setTimeunit(
                    TimeUnit.valueOf(
                    conf.getString("scheduler.timeunit").toUpperCase()));
        }



    }
    //TODO That's not really the best for sync stuff.. but it works

    /**
     * Call always before configure before start sampling
     *
     * @param testname
     * @throws Exception
     */
    @Override
    public synchronized void startSampling() throws Exception {
        if (running.get()) {
            throw new IllegalStateException("Sampling already running");
        }
        try {
            configure();
            running.set(true);
            for (Recorder r : recorders) {
                r.configure();
                r.start();
            }
            for (MetricsReader r : metricsReaders) {
                r.configure();
            }

            log.log(Level.INFO, "Cleaning the stack of errors because the sampling has started correctly");
            log.log(Level.INFO, "Old exception stack : {0}", getErrorsStackTrace());
            errors.clear();
            this.runOneIteration();
        } catch (Exception e) {
            running.set(false);
            throw e;
        }

    }

    @Override
    public synchronized void stopSampling() throws IllegalStateException, Exception {
        if (!running.compareAndSet(true, false)) {
            throw new IllegalStateException("Sampling already stopped");
        }
        this.runOneIteration();
        for (Recorder r : recorders) {

            r.stop();
        }
    }

    @Override
    public boolean isSampling() {
        return running.get();
    }

    @Override
    public void reportException(Throwable t) {
        errors.add(t);
    }

    /**
     * It runs an iteration: it submit the task of record and store the metric
     * to a other thread of a fixed pool and returns immediately.
     *
     * @throws Exception
     */
    @Override
    public void runOneIteration() throws Exception {
        if (!running.get()) {
            return;
        }
        log.info("Running one interation");
        for (MetricsReader mr : metricsReaders) {
            ListenableFuture<List<Metric>> submit = service.submit(mr);
            Futures.addCallback(submit, new FutureCallback<List<Metric>>() {
                @Override
                public void onSuccess(List<Metric> result) {
                    log.info("data read");
                    for (Recorder r : recorders) {
                        r.store(result);
                        log.log(Level.INFO, "data stored in {0}", r.getClass().getName());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    SimpleNodeAgent.errors.add(t);

                    log.log(Level.WARNING, "Error saving the metrics in cassandra {0}", t);
                }
            });
        }

    }

    /**
     * Return the instance of the dyn.
     *
     * @return
     */
    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        return dyn;

    }
}
