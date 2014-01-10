/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import es.bsc.aeneas.cassandrametricsrecorder.CassandraMetricsReporter;
import es.bsc.aeneas.codegenerator.query.QueryInterface;
import es.bsc.aeneas.codegenerator.query.QueryNotImplementedException;
import es.bsc.aeneas.codegenerator.query.annotations.Input;
import es.bsc.aeneas.codegenerator.query.annotations.InputVars;
import es.bsc.aeneas.commons.CUtils;
import es.bsc.aeneas.workloader.controller.Client.Distribution;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class takes in the construct a
 *
 * @param method and a
 * @param distribution type and creates the proper distribution objects. When it
 * is invoked the
 * {@link MethodHolder#createInvokeTask(es.bsc.aeneas.codegenerator.query.QueryInterface)}
 * method, this class use the random imput values created and ivoke the holded
 * method on the argument object
 *
 *
 *
 * @author cesare
 */
public class MethodHolder {

    private static final Logger log = Logger.getLogger(MethodHolder.class.getName());
    private final static String callBeforeRun = CUtils.getString("callbeforerun");
    private final static String callAfterRun = CUtils.getString("callafterrun");

    public static void execProcess(String command) throws Exception {
        CommandLine c = new CommandLine("/bin/sh");
        c.addArguments(new String[]{"-c", command}, false);
        new DefaultExecutor().execute(c);

    }
    private final Method m;
    private final ImmutableList<MethodInput> inputs;
    private final Timer responses;

    MethodHolder(Method method, Distribution distribution) {
        responses = Metrics.newTimer(MethodHolder.class,
                "responses-" + method.getName(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
        this.m = method;
        Builder<MethodInput> builder = ImmutableList.builder();
        InputVars inVars = checkNotNull(method.getAnnotation(InputVars.class), "Can't find input annotation");
        for (Input iv : inVars.value()) {
            builder.add(new MethodInput(iv, distribution)); //this is better to do in multithread
        }
        inputs = builder.build();


    }

    @Override
    public String toString() {
        return m.toString();
    }

    public Callable<Map<String, Object>> createInvokeTask(QueryInterface imple) {
        return new CallableHolder(imple);

    }

    private class CallableHolder implements Callable<Map<String, Object>> {

        private final QueryInterface imple;

        CallableHolder(QueryInterface imple) {
            this.imple = imple;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            CassandraMetricsReporter cmr=CassandraMetricsReporter.getInstance();
            ArrayList inputsValue = new ArrayList();
            for (MethodInput mi : inputs) {
                mi.setInput(inputsValue);
            }
            callBeforeRun();
            long singleRun=System.currentTimeMillis();
            TimerContext context = responses.time();
            try {
                log.log(Level.FINEST, "Invoking the method {0}", m.getName());
                Object invoke = m.invoke(imple, inputsValue.toArray());
                return (Map<String, Object>) invoke;
            } catch (InvocationTargetException qne) {
                if (qne.getCause() != null && qne.getCause() instanceof QueryNotImplementedException) {
                    return new HashMap<String, Object>(0);
                } else {
                    throw new RuntimeException("Exception in the method " + m.getName(), qne);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Generic Exception in the method " + m.getName(), ex);
            } finally {                
                context.stop();
                if(cmr!=null){
                    cmr.addAsyncMetric(m.getName()+".singleRun", System.currentTimeMillis()-singleRun);
                }
                callAfterRun();
            }

        }

        void callBeforeRun() {
            try {
                if (callBeforeRun == null) {
                    return;
                }
                execProcess(callBeforeRun);
                log.log(Level.INFO, "Executed call before method : [{0}]", callBeforeRun);
            } catch (Exception ex) {
                log.log(Level.WARNING, "Calling of the call before run" + callBeforeRun + " as failed: {1}" + ex.getMessage(), ex);

            }
        }

        void callAfterRun() {

            try {
                if (callAfterRun == null) {
                    return;
                }
                execProcess(callAfterRun);
                log.log(Level.INFO, "Executed call after method : [{0}]", callAfterRun);
            } catch (Exception ex) {
                log.log(Level.WARNING, "Calling of the call before run" + callAfterRun + " as failed: {1}" + ex.getMessage(), ex);

            }
        }
    }
}
