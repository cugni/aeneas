/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent;

import es.bsc.aeneas.core.nodeagent.MetricsReader;
import es.bsc.aeneas.core.nodeagent.Recorder;
import es.bsc.aeneas.core.nodeagent.SimpleNodeAgent;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author ccugnasc
 */
public class SimpleNodeAgentTest {

    @Test
    public void testGetConfigurations() throws Exception {
        Recorder r = Mockito.mock(Recorder.class);
        MetricsReader mr1 = Mockito.mock(MetricsReader.class);
        MetricsReader mr2 = Mockito.mock(MetricsReader.class);
        Configuration c = new BaseConfiguration();
        SimpleNodeAgent s = new SimpleNodeAgent();
        s.conf = c;
        s.configure();
        s.getMetricsReaders().addAll(Arrays.asList(mr1, mr2));
        s.getRecorders().addAll(Arrays.asList(r));
        c.addProperty("testname", "prova");
        s.startSampling();


        verify(r).configure();
        verify(mr1).configure();
        verify(mr2).configure();


    }

    @Test
    public void testReportException() throws Exception {
        Configuration c = new BaseConfiguration();
        SimpleNodeAgent sa = new SimpleNodeAgent();
        sa.conf = c;
        sa.configure();
        sa.reportException(new Exception("test"));
        assertTrue(sa.getErrorsStackTrace().contains("test"));


    }

    /**
     * Test of runOneIteration method, of class SimpleNodeAgent.
     */
    @Test
    public void testRunOneIteration() throws Exception {
        Recorder r = Mockito.mock(Recorder.class);
        MetricsReader mr1 = Mockito.mock(MetricsReader.class);
        MetricsReader mr2 = Mockito.mock(MetricsReader.class);
        Configuration c = new BaseConfiguration();
        SimpleNodeAgent s = new SimpleNodeAgent();
        s.conf = c;
        s.configure();
        s.getMetricsReaders().addAll(Arrays.asList(mr1, mr2));
        s.getRecorders().addAll(Arrays.asList(r));
        c.addProperty("testname", "ciao");
        s.startSampling();
        s.runOneIteration();
        //since there are more threads, better to wait them
        Thread.sleep(10000);
        verify(mr1, times(2)).call();
        verify(mr2, times(2)).call();
        verify(r, times(4)).store(Mockito.any(List.class));


    }

    @Test
    public void testStartSempling() throws Exception {
        Recorder r = Mockito.mock(Recorder.class);
        MetricsReader mr1 = Mockito.mock(MetricsReader.class);
        MetricsReader mr2 = Mockito.mock(MetricsReader.class);
        Configuration c = new BaseConfiguration();
        c.addProperty("scheduler.interval", 1);
        SimpleNodeAgent s = new SimpleNodeAgent();
        s.conf = c;
        s.getMetricsReaders().addAll(Arrays.asList(mr1, mr2));
        s.getRecorders().addAll(Arrays.asList(r));
        s.configure();
        verify(mr1, Mockito.never()).configure();
        verify(mr2, Mockito.never()).configure();
        s.start();
        c.addProperty("testname", "prova");
        s.startSampling();
        Thread.sleep(2000);

        verify(r, Mockito.atLeastOnce()).store(Mockito.any(List.class));
        verify(mr1).configure();
        verify(mr2).configure();
        verify(mr1, Mockito.atLeastOnce()).call();
        verify(mr2, Mockito.atLeastOnce()).call();
        assertTrue(s.isSampling());
    }
}