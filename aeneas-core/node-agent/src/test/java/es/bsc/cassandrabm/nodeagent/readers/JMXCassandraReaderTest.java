/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.readers;

import es.bsc.aeneas.core.nodeagent.readers.JMXCassandraReader;
import es.bsc.aeneas.core.nodeagent.Metric;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class JMXCassandraReaderTest {

    private static final Logger LOG = Logger.getLogger(JMXCassandraReaderTest.class.getName());
    private final String[] normal_rows = {"OperationMode", "StreamThroughputMbPerSec", "ExceptionCount", "Load",
        "FreePhysicalMemorySize", "SystemCpuLoad", "SystemLoadAverage", "ProcessCpuTime", "ProcessCpuLoad",
        "CollectionCount", "CollectionTime", "LastGcDuration",
        //"Capacity",
        "Hits","RecentHitRate",
        //"KeyCache",
        //        "ColumnFamilyInProgress", "BytesCompacted", "BytesTotalInProgress", "CompactionType", // Optionals
        "HeapUsed", "HeapMax", "HeapCommitted", "HeapInit", "NotHeapUsed", "NotHeapMax", "NotHeapCommitted", "NotHeapInit", "RecentReadOperations", "ReadStageCompletedTasks", "ReadStageActiveCount", "ReadStagePendingTasks", "MutationStageCompletedTasks", "MutationStageActiveCount", "MutationStagePendingTasks"};
    private final String[] metrics_x_cfs = {"ReadOperations",
        "WriteOperations",
        "RecentReadLatencyMillis",
        "RecentWriteLatencyMillis",
        "ReadLatencyMillis",
        "WriteLatencyMillis",
        "TotalReadLatencyMicros",
        "TotalWriteLatencyMicros",
        "LiveDiskSpaceUsed",
        "LiveSSTableCount",
        "PendingTasks",
        "RecentBloomFilterFalsePositiveRatio",
        "BloomFilterFalsePositiveRatio",
        "MemtableColumnCount",
        "MeanRowSize",
        "MaxRowSize",
        "MinRowSize",
        "TotalDiskSpaceUsed",
        "LiveDiskSpaceUsed"};

    @Test
    public void testConfigure() throws Exception {
        JMXCassandraReader j = new JMXCassandraReader();
        Configuration c = new BaseConfiguration();
        c.addProperty("columns", "metricspace:metrics");
        c.addProperty("garbagecollector.name", "ConcurrentMarkSweep");
        c.addProperty("testingnode", "localhost");
        c.addProperty("jmxport", "7199");
        j.setConf(c);
        j.configure();

    }

    /**
     * Test of call method, of class JMXCassandraReader.
     */
    @Test
    public void testCall() throws Exception {
        JMXCassandraReader j = new JMXCassandraReader();
        Configuration c = new BaseConfiguration();
        c.addProperty("columns", "metricspace:metrics");
        c.addProperty("garbagecollector.name", "ConcurrentMarkSweep");
        c.addProperty("testingnode", "localhost");
        c.addProperty("jmxport", "7199");
        j.setConf(c);
        j.configure();
        List<Metric> call = j.call();
        HashMap<String, Metric> s = new HashMap<String, Metric>(5);
        for (Metric m : call) {
            s.put(m.getName(), m);
        }
        for (String cs : metrics_x_cfs) {
            assertTrue(cs + " not found", s.containsKey(cs));
            assertEquals(cs + " group different", s.get(cs).getGroup(), "metricspace.metrics");
        }
        for (String es : normal_rows) {
            assertTrue("not found " + es, s.containsKey(es));
        }
    }
}