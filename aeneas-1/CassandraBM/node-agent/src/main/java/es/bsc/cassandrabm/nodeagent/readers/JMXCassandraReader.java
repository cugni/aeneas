/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.readers;

import com.google.common.collect.ImmutableSet;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import com.sun.management.OperatingSystemMXBean;
import es.bsc.cassandrabm.nodeagent.Metric;
import es.bsc.cassandrabm.nodeagent.MetricsReader;
import org.apache.cassandra.concurrent.JMXConfigurableThreadPoolExecutorMBean;
import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.db.compaction.CompactionManager;
import org.apache.cassandra.db.compaction.CompactionManagerMBean;
import org.apache.cassandra.service.CacheServiceMBean;
import org.apache.cassandra.service.StorageServiceMBean;
import org.apache.commons.configuration.Configuration;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.inject.Inject;

//TODO be aware.. this limit you to use a SUN java virtual machine

/**
 *
 * @author ccugnasc
 */
public class JMXCassandraReader implements MetricsReader {

    private final static Logger log = Logger.getLogger(JMXCassandraReader.class.getName());
    private static final String FMT_URL = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
    private static final String CACHES_OBJECT_NAME = "org.apache.cassandra.db:type=Caches";
    private static final String MS_OBJECT_NAME = "org.apache.cassandra.request:type=MutationStage";
    private static final String RS_OBJECT_NAME = "org.apache.cassandra.request:type=ReadStage";
    private static final String SS_OBJECT_NAME = "org.apache.cassandra.db:type=StorageService";
    private static final String CF_OBJECT_NAME = "org.apache.cassandra.db:type=ColumnFamilies,keyspace=%s,columnfamily=%s";
    private Set<cfbean> cfs;
    private StorageServiceMBean storage;
    private JMXConfigurableThreadPoolExecutorMBean readStage;
    private JMXConfigurableThreadPoolExecutorMBean mutationStage;
    private MemoryMXBean memory;
    private OperatingSystemMXBean unixSystem;
    private GarbageCollectorMXBean gc;
    private CompactionManagerMBean compa;
    private CacheServiceMBean cache;
    @Inject
    private Configuration conf;
    @Override
    public void configure() throws Exception {
        String gbName = checkNotNull(conf.getString("garbagecollector.name"), "Missing configuration parameter garbagecollector");
        String columns = checkNotNull(conf.getString("columns"), "Missing a necessary init argument: columns");
        String testingnode = conf.getString("testingnode");
        int jmxport = conf.getInt("cassandra.jmxport", 7199);
        JMXServiceURL jmxUrl = new JMXServiceURL(String.format(FMT_URL, checkNotNull(testingnode), jmxport));
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        MBeanServerConnection mbeanServerConn = checkNotNull(jmxc.getMBeanServerConnection());
        readStage = JMX.newMBeanProxy(mbeanServerConn, new ObjectName(RS_OBJECT_NAME), JMXConfigurableThreadPoolExecutorMBean.class);
        mutationStage = JMX.newMBeanProxy(mbeanServerConn, new ObjectName(MS_OBJECT_NAME), JMXConfigurableThreadPoolExecutorMBean.class);
        storage = JMX.newMBeanProxy(mbeanServerConn, new ObjectName(SS_OBJECT_NAME), StorageServiceMBean.class);
        memory = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        unixSystem = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn,
                ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        gc = checkNotNull(ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn, "java.lang:type=GarbageCollector,name=" + gbName,
                GarbageCollectorMXBean.class), "can't load the GarbageCollectorMXBean");
        compa = JMX.newMBeanProxy(mbeanServerConn, new ObjectName(CompactionManager.MBEAN_OBJECT_NAME), CompactionManagerMBean.class);
        cache=JMX.newMBeanProxy(mbeanServerConn,new ObjectName(CACHES_OBJECT_NAME),CacheServiceMBean.class);
        ImmutableSet.Builder<cfbean> b = ImmutableSet.builder();
        for (String cf : columns.split(";")) {
            String[] split = cf.split(":");
            checkArgument(split.length == 2);
            b.add(new cfbean(split[0], split[1], mbeanServerConn));
        }
        cfs = b.build();


    }

    @Override
    public List<Metric> call() throws Exception {
        List<Metric> l = new ArrayList<Metric>(50);
        l.add(new Metric("OperationMode", storage.getOperationMode()));
        l.add(new Metric("StreamThroughputMbPerSec", storage.getStreamThroughputMbPerSec()));
        l.add(new Metric("ExceptionCount", storage.getExceptionCount()));

        double load = storage.getLoad();
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setGroupingUsed(false);
        l.add(new Metric("Load", formatter.format(load)));
        // Compactions
        List<Map<String, String>> compactions = compa.getCompactions();
        for (Map<String, String> compaction : compactions) {
            try {
                String bytesTotalInProgress = compaction.get("TotalBytes");
                String bc = compaction.get("BytesComplete");
                if (bc != null && bytesTotalInProgress != null) {
                    l.add(new Metric("ColumnFamilyInProgress", compaction.get("ColumnFamily")));
                    long bytesCompacted = Long.parseLong(bc);
                    l.add(new Metric("BytesCompacted", bytesCompacted));
                    l.add(new Metric("BytesTotalInProgress", Long.parseLong(bytesTotalInProgress)));
                    l.add(new Metric("CompactionType", compaction.get("TaskType")));
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Error on compaction", e);
            }
        }
        MemoryUsage heapMemoryUsage = memory.getHeapMemoryUsage();
        l.add(new Metric("HeapUsed", heapMemoryUsage.getUsed()));
        l.add(new Metric("HeapMax", heapMemoryUsage.getMax()));
        l.add(new Metric("HeapCommitted", heapMemoryUsage.getCommitted()));
        l.add(new Metric("HeapInit", heapMemoryUsage.getInit()));

        //not heap memory
        MemoryUsage notHeapMemoryUsage = memory.getNonHeapMemoryUsage();
        l.add(new Metric("NotHeapUsed", notHeapMemoryUsage.getUsed()));
        l.add(new Metric("NotHeapMax", notHeapMemoryUsage.getMax()));
        l.add(new Metric("NotHeapCommitted", notHeapMemoryUsage.getCommitted()));
        l.add(new Metric("NotHeapInit", notHeapMemoryUsage.getInit()));

        //TODO be aware.. this limit you to use a SUN java virtual machine

        //Cpu usage
        l.addAll(Metric.Metric("system")
                .addMetric("FreePhysicalMemorySize", unixSystem.getFreePhysicalMemorySize())
                // System cpu load are available only on with sun >java 1.7 
                .addMetric("SystemCpuLoad", unixSystem.getSystemCpuLoad())
                .addMetric("SystemLoadAverage", unixSystem.getSystemLoadAverage())
                .addMetric("ProcessCpuTime", unixSystem.getProcessCpuTime())
                .addMetric("ProcessCpuLoad", unixSystem.getProcessCpuLoad())
                .getList());
        //Cpu usage
        GcInfo lastGcInfo = gc.getLastGcInfo();
        long lastDuration = lastGcInfo == null ? -1 : lastGcInfo.getDuration();
        l.addAll(Metric.Metric("GarbageCollector")
                .addMetric("CollectionCount", gc.getCollectionCount())
                .addMetric("CollectionTime", gc.getCollectionTime())
                .addMetric("LastGcDuration", lastDuration)
                .getList());


        //Cache memory
        l.addAll(Metric.Metric("KeyCache")
//                .addMetric("Capacity", cache.getKeyCacheCapacityInBytes())
                .addMetric("Hits", cache.getKeyCacheHits())
                .addMetric("RecentHitRate", cache.getKeyCacheRecentHitRate())
//                .addMetric("Size", cache.getKeyCacheSize())
                .getList());
          l.addAll(Metric.Metric("RowCache")
//                .addMetric("Capacity", cache.getRowCacheCapacityInBytes())
                .addMetric("Hits", cache.getRowCacheHits())
                .addMetric("RecentHitRate", cache.getRowCacheRecentHitRate())
//                .addMetric("Size", cache.getRowCacheSize())
                  .getList());


        // Storage Proxy
        for (cfbean cf : cfs) {

            long histogram[] = cf.columnfamily.getRecentSSTablesPerReadHistogram();
            long totalReads = 0;
            for (int i = 0; i < histogram.length; i++) {
                //   save.addMetricCF("SSTablesPerRead", i, histogram[i]);
                totalReads += histogram[i];
            }

            l.addAll(Metric.Metric(cf.ks + "." + cf.cf, l)
                    .addMetric("ReadOperations", cf.columnfamily.getReadCount()). //
                    addMetric("WriteOperations", cf.columnfamily.getWriteCount()) //
                    .addMetric("RecentReadLatencyMillis", cf.columnfamily.getRecentReadLatencyMicros() / 1000) //
                    .addMetric("RecentWriteLatencyMillis", cf.columnfamily.getRecentWriteLatencyMicros() / 1000) //
                    .addMetric("ReadLatencyMillis", cf.columnfamily.getRecentReadLatencyMicros() / 1000)//
                    .addMetric("WriteLatencyMillis", cf.columnfamily.getRecentWriteLatencyMicros() / 1000)//
                    .addMetric("TotalReadLatencyMicros", cf.columnfamily.getTotalReadLatencyMicros())
                    .addMetric("TotalWriteLatencyMicros", cf.columnfamily.getTotalWriteLatencyMicros())
                    .addMetric("LiveDiskSpaceUsed", cf.columnfamily.getLiveDiskSpaceUsed())//
                    .addMetric("LiveSSTableCount", cf.columnfamily.getLiveSSTableCount())//
                    .addMetric("PendingTasks", cf.columnfamily.getPendingTasks())//
                    .addMetric("RecentBloomFilterFalsePositiveRatio", cf.columnfamily.getRecentBloomFilterFalseRatio())//
                    .addMetric("BloomFilterFalsePositiveRatio", cf.columnfamily.getBloomFilterFalseRatio())//
                    .addMetric("MemtableColumnCount", cf.columnfamily.getMemtableColumnsCount())//
                    .addMetric("MeanRowSize", cf.columnfamily.getMeanRowSize())//
                    .addMetric("MaxRowSize", cf.columnfamily.getMaxRowSize())//
                    .addMetric("MinRowSize", cf.columnfamily.getMinRowSize())//
                    .addMetric("TotalDiskSpaceUsed", cf.columnfamily.getTotalDiskSpaceUsed())//
                    .addMetric("LiveDiskSpaceUsed", cf.columnfamily.getLiveDiskSpaceUsed())
                    .addMetric("RecentReadOperations", totalReads).getList());

        }
        //read stage
        l.add(new Metric("ReadStageCompletedTasks",
                readStage.getCompletedTasks()));
        l.add(new Metric("ReadStageActiveCount",
                readStage.getActiveCount()));
        l.add(new Metric("ReadStagePendingTasks",
                readStage.getPendingTasks()));
        l.add(new Metric("MutationStageCompletedTasks",
                mutationStage.getCompletedTasks()));
        l.add(new Metric("MutationStageActiveCount",
                mutationStage.getActiveCount()));
        l.add(new Metric("MutationStagePendingTasks",
                mutationStage.getPendingTasks()));

        return l;
    }

    /**
     * This class is an helper to store data in Cassandra according the
     * metricpace model.
     */
    private class cfbean {

        final ColumnFamilyStoreMBean columnfamily;
        final String ks;
        final String cf;

        cfbean(String ks, String cf, MBeanServerConnection mbeanServerConn) throws Exception {
            this.ks = checkNotNull(ks);
            this.cf = checkNotNull(cf);
            columnfamily = JMX.newMBeanProxy(mbeanServerConn, new ObjectName(String.format(CF_OBJECT_NAME, ks, cf)),
                    ColumnFamilyStoreMBean.class);
        }
    }
     @Override
    public void setConf(Configuration c) {
        conf = c;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
