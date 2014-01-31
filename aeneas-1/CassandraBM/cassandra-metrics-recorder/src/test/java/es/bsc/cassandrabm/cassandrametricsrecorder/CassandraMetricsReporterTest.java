/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.cassandrametricsrecorder;

import es.bsc.cassandrabm.loader.DBSetter;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 *
 * @author ccugnasc
 */
public class CassandraMetricsReporterTest {

    public CassandraMetricsReporterTest() {
    }

    /**
     * Test of getInstance method, of class CassandraMetricsReporter.
     */
    @Test
    public void testAsyncOne() {

        DBSetter db = mock(DBSetter.class);
        MetricspaceHolder ms = new MetricspaceHolder(db);
        CassandraMetricsReporter c =
                CassandraMetricsReporter.create("context", "test", ms);
        c.addAsyncMetric("prova", 1);
        verify(db, never()).put(any(), any());
        c.run();
        c.run();
        //  put(value,context,testname,location,System.currentTimeMillis(),propertyname);
        verify(db, only()).put(same(1), same("context"), same("test"), any(), any(), same("prova"));

    }

    public void testAsyncMultiple() {
        DBSetter db = mock(DBSetter.class);
        MetricspaceHolder ms = new MetricspaceHolder(db);
        CassandraMetricsReporter c =
                CassandraMetricsReporter.create("context", "test", ms);


        c.addAsyncMetric("prova1", 1);
        c.addAsyncMetric("prova2", 2);
        c.addAsyncMetric("prova3", 3);
        c.addAsyncMetric("prova4", 4);
        c.run();
//        verify(db, times(4)).put(any(), same("context"), same("test"), any(), any(), any());
        verify(db, only()).put(same(1), same("context"), same("test"), any(), any(), same("prova1"));
        verify(db, only()).put(same(2), same("context"), same("test"), any(), any(), same("prova2"));
        verify(db, only()).put(same(3), same("context"), same("test"), any(), any(), same("prova3"));
        verify(db, only()).put(same(4), same("context"), same("test"), any(), any(), same("prova4"));





    }
//    /**
//     * Test of start method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testStart() {
//        System.out.println("start");
//        long period = 0L;
//        TimeUnit unit = null;
//        CassandraMetricsReporter instance = null;
//        instance.start(period, unit);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of shutdown method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testShutdown_long_TimeUnit() throws Exception {
//        System.out.println("shutdown");
//        long timeout = 0L;
//        TimeUnit unit = null;
//        CassandraMetricsReporter instance = null;
//        instance.shutdown(timeout, unit);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of shutdown method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testShutdown_0args() {
//        System.out.println("shutdown");
//        CassandraMetricsReporter instance = null;
//        instance.shutdown();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addAsyncMetric method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testAddAsyncMetric() {
//        System.out.println("addAsyncMetric");
//        String propertyName = "";
//        Object value = null;
//        CassandraMetricsReporter instance = null;
//        instance.addAsyncMetric(propertyName, value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of run method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testRun() {
//        System.out.println("run");
//        CassandraMetricsReporter instance = null;
//        instance.run();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processAsyncMetric method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessAsyncMetric() {
//        System.out.println("processAsyncMetric");
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processAsyncMetric(context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processMeter method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessMeter() throws Exception {
//        System.out.println("processMeter");
//        MetricName name = null;
//        Metered meter = null;
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processMeter(name, meter, context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processCounter method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessCounter() throws Exception {
//        System.out.println("processCounter");
//        MetricName name = null;
//        Counter counter = null;
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processCounter(name, counter, context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processHistogram method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessHistogram() throws Exception {
//        System.out.println("processHistogram");
//        MetricName name = null;
//        Histogram histogram = null;
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processHistogram(name, histogram, context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processTimer method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessTimer() throws Exception {
//        System.out.println("processTimer");
//        MetricName name = null;
//        Timer timer = null;
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processTimer(name, timer, context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of processGauge method, of class CassandraMetricsReporter.
//     */
//    @Test
//    public void testProcessGauge() throws Exception {
//        System.out.println("processGauge");
//        MetricName name = null;
//        Gauge<?> gauge = null;
//        CassandraMetricsReporter.Context context = null;
//        CassandraMetricsReporter instance = null;
//        instance.processGauge(name, gauge, context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}