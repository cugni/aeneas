/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.metricstorer;

import es.bsc.cassandrabm.loader.DBSetter;
import es.bsc.cassandrabm.loader.LightXMLCassandraSetter;
import es.bsc.cassandrabm.nodeagent.Metric;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 *
 * @author ccugnasc
 */
public class AeneasMetricsStorerTest {

    /**
     * Test of store method, of class AeneasMetricsStorer.
     */
    @Test
    public void testStore_MetricMock() throws Exception {
        DBSetter dbs = Mockito.mock(DBSetter.class);
        Configuration c = new BaseConfiguration();
        AeneasMetricsStorer a = new AeneasMetricsStorer(dbs);
        c.addProperty("testingnode", "localhost");
        c.addProperty("testname", "prova");
        a.setConf(c);
        a.configure();
        a.start();
        a.store(new Metric("a", 1));
        a.store(new Metric("b", 2));
        a.store(new Metric("c", 3));
        for (Metric m : Metric.Metric("d")
                .addMetric("alfa", 4)
                .addMetric("beta", 5).getList()) {
            a.store(m);
        }
        a.stop();
        verify(dbs).put(eq(1), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("a"));
        verify(dbs).put(eq(2), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("b"));
        verify(dbs).put(eq(3), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("c"));
        verify(dbs).put(eq(4), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("d.alfa"));
        verify(dbs).put(eq(5), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("d.beta"));
        verify(dbs).put(anyLong(), eq("clusternode"),
                eq("prova"), eq("localhost"), eq("start"));
       verify(dbs).put(anyLong(), eq("clusternode"),
                eq("prova"), eq("localhost"), eq("stop"));

    }

    /**
     * Test of store method, of class AeneasMetricsStorer.
     */
    @Test
    public void testStore_MetricLighXml() throws Exception {

        DBSetter dbs = new LightXMLCassandraSetter("metricspace.cm.xml", "MetricsReferenceModel.xml");

        Configuration c = new BaseConfiguration();
        AeneasMetricsStorer a = new AeneasMetricsStorer(dbs);
        c.addProperty("testingnode", "localhost");
        c.addProperty("testname", "prova");
        c.addProperty("aneas-recorder.clustername", "Test Cluster");
        c.addProperty("aneas-recorder.clusterlocation", "localhost:9160");
        a.setConf(c);
        a.configure( );

        a.store(new Metric("a", 1));
        a.store(new Metric("b", 2));
        a.store(new Metric("c", 3));
        for (Metric m : Metric.Metric("d")
                .addMetric("alfa", 4)
                .addMetric("beta", 5).getList()) {
            a.store(m);
        }

    }

    /**
     * Test of store method, of class AeneasMetricsStorer.
     */
    @Test
    public void testStore_Collection() throws Exception {
        DBSetter dbs = Mockito.mock(DBSetter.class);
        Configuration c = new BaseConfiguration();
        AeneasMetricsStorer a = new AeneasMetricsStorer(dbs);
        c.addProperty("testingnode", "localhost");
        c.addProperty("testname", "prova");
        a.setConf(c);
        a.configure();
        List<Metric> l = new ArrayList<Metric>();
        l.add(new Metric("a", 1));
        l.add(new Metric("b", 2));
        l.add(new Metric("c", 3));
        l.addAll(Metric.Metric("d")
                .addMetric("alfa", 4)
                .addMetric("beta", 5).getList());
        a.store(l);
        verify(dbs).put(eq(1), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("a"));
        verify(dbs).put(eq(2), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("b"));
        verify(dbs).put(eq(3), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("c"));
        verify(dbs).put(eq(4), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("d.alfa"));
        verify(dbs).put(eq(5), eq("clusternode"),
                eq("prova"), eq("localhost"), anyLong(), eq("d.beta"));

    }

    /**
     * Test of configure method, of class AeneasMetricsStorer.
     */
    @Test
    public void testConfigure() throws Exception {
        DBSetter dbs = Mockito.mock(DBSetter.class);
        Configuration c = new BaseConfiguration();
        AeneasMetricsStorer a = new AeneasMetricsStorer(dbs);
        c.addProperty("testingnode", "localhost");
        c.addProperty("testname", "prova");
        a.setConf(c);
        a.configure();

    }
}