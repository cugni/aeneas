/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.controller;

import com.google.common.base.CaseFormat;
import es.bsc.aeneas.cassandrametricsrecorder.CassandraMetricsReporter;
import es.bsc.aeneas.codegenerator.query.AbstractQueryImpl;
import es.bsc.aeneas.codegenerator.query.QueryInterface;
import es.bsc.aeneas.commons.CUtils;
import es.bsc.aeneas.core.workloader.controller.TestImpl.FailedTestException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesare Configurations avaible: -distribution: constant sequential
 * zipfian uniform -concurrency -clustername -clusterlocation -rangeuniform
 * -continueonerror default true
 *
 */
public class Client {

    private static final Logger log = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws Exception {
        Distribution type = Distribution.valueOf(CUtils.getString("distribution", "uniform").toUpperCase());
        QueryInterface queryImpl = null;
        String implName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,
                CUtils.getString("model", "test"));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd-hh-mm:ss");
        String testname=CUtils.getString("testname");
        if(testname==null){
            testname=implName+Calendar.getInstance().getTime().toString().replace(" ","_").replace(":", "-");
                   
        }else{
            testname=testname.replace(":", "_");//colons give some problem with cassandra-cli
        }
        CassandraMetricsReporter cmr = CassandraMetricsReporter.create("workloader",
                testname,//
                CUtils.getString("reportingserverlocation", "localhost:9160"),
                CUtils.getString("reportingserverclustername", "CBMetrics"));
        
        cmr.start(CUtils.getInt("metricssampling",10), TimeUnit.SECONDS);
        Integer ntest = CUtils.getInt("ntest", 20000);
        Integer concurrency = CUtils.getInt("concurrency", 20);

        ServiceLoader<AbstractQueryImpl> servs = ServiceLoader.load(AbstractQueryImpl.class);
        if (!servs.iterator().hasNext()) {
            throw new IllegalArgumentException("QueryInterface service provider class not found.");
        }
        for (QueryInterface qi : servs) {
            if (qi.getClass().getSimpleName().equals(implName)) {
                queryImpl = qi;
                break;
            }
        }
        if (queryImpl == null) {
            throw new IllegalArgumentException("Implementation class not found.");
        }

        System.out.printf("Starting testing on model %s for %d times at %s\n", implName, ntest, sf.format(Calendar.getInstance().getTime()));
        TestImpl test = new TestImpl(queryImpl, type, ntest, concurrency,
                CUtils.getBoolean("continueonerror", false),
                CUtils.getBoolean("continueontimeout", false));
        //First sampling

        try {
            test.run();
        } catch (FailedTestException ex) {
            log.log(Level.WARNING, "Testing failed: {0}", ex.getMessage());
            System.out.println("Testing failed: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
        // A last sampling at test completed
        cmr.run();
        cmr.shutdown();
        System.out.printf("Testing on model %s for %d times completed at %s\n Compleded %d, failed %d, timeout %d",
                implName, ntest, sf.format(Calendar.getInstance().getTime()),
                test.completedQueries.count(), test.failedQueries.count(),
                test.timeoutQueries.count());
    }

    public enum Distribution {

        CONSTANT,
        SEQUENTIAL,
        UNIFORM,
        ZIPFIAN
    }
}
