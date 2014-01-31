package es.bsc.aeneas.cassandra.nodeagent.metricstores;

import static com.google.common.base.Preconditions.checkNotNull;
import es.bsc.aeneas.cassandra.metricsrecorder.MetricspaceHolder;
import es.bsc.aeneas.cassandra.metricsrecorder.MetricspaceHolder.MetricContext;
 
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import es.bsc.aeneas.core.nodeagent.Metric;
import es.bsc.aeneas.core.nodeagent.Recorder;
import es.bsc.aeneas.core.rosetta.Rosetta;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;

/**
 *
 *
 *
 * This class connects to the reporting cluster and to the node to measure. At
 * each invocation of the method "call" it extract the information from the
 * testingnode and store them to the serverlocation.
 *
 * @author cesare
 * @param serverlocation The Address of the entry node of the cluster where to
 * store the metrics. (e.g. 192.168.1.10:9160)
 * 
* @param clustername The cluster name of the reporting server (e.g. "Test
 * Cluster")
 * @param testingnode The address and port of the JMX interface of the server to
 * be measured (e.g. localhost:7199)
 * @param columns A list of the Column Families to measure. The syntax is
 * "keyspace1:columnfamily1;keyspace2:columnfamily2"
 * @param deviceregexp A regexp to match to the device name you want to measure
 * (e.g sda[0-9])
 * @throws Exception It throws and Exception if it is not possible to connects
 * with one of the Cassandra clusters.
 *
 */
public class AeneasMetricsStorer implements Recorder {

    private final static Logger log = Logger.getLogger(AeneasMetricsStorer.class.getName());
    @Inject
    private   MetricspaceHolder msh;
    private final ReadWriteLock l = new ReentrantReadWriteLock();
    @Inject
    private MetricContext mcnt;
    @Inject
    private  Rosetta rosetta;

    /**
     * This class connects to the reporting cluster and to the node to measure.
     * At each invocation of the method "call" it extract the information from
     * the testingnode and store them to the serverlocation.
     *
     * @param db
     * @param serverlocation The Address of the entry node of the cluster where
     * to store the metrics. (e.g. 192.168.1.10:9160)
     *
     * @param clustername The cluster name of the reporting server (e.g. "Test
     * Cluster")
     *
     * @param testingnode The address and port of the JMX interface of the
     * server to be measured (e.g. localhost:7199)
     *
     * @param columns A list of the Column Families to measure. The syntax is
     * "keyspace1:columnfamily1;keyspace2:columnfamily2"
     * @param deviceregexp A regexp to match to the device name you want to
     * meause (e.g sda[0-9])
     * @throws Exception It throws and Exception if it is not possible to
     * connect with one of the Cassandra clusters.
     *
     */
  
    

    /**
     * At every call of this method it retrieve metrics from the testingnode and
     * store them in the reporting server. All metrics are recording using the
     * same time
     */
    @Override
    public void store(Metric m) {
        l.readLock().lock();
        try {
            MetricContext.MetricHolder save = mcnt.storeMetric(System.currentTimeMillis());
            if (m.isGrouped()) {
                save.addGroup(m.getGroup()).addMetric(m.getName(), m.getValue());
            } else {
                save.addMetric(m.getName(), m.getValue());
            }
        } finally {
            l.readLock().unlock();
        }
    }

    @Override
    public void store(Collection<Metric> lm) {
        l.readLock().lock();
        try {
            MetricContext.MetricHolder save = mcnt.storeMetric(System.currentTimeMillis());
            for (Metric m : lm) {
                if (m.isGrouped()) {
                    save.addGroup(m.getGroup()).addMetric(m.getName(), m.getValue());
                } else {
                    save.addMetric(m.getName(), m.getValue());
                }
            }
        } finally {
            l.readLock().unlock();
        }

    }

    @Override
    public void configure( ) {
        Lock writeLock = l.writeLock();
        writeLock.lock();
        try {
            String testingnode = checkNotNull(conf.getString("testingnode"),
                    "required argument testingnode");
            String testname = checkNotNull(conf.getString("testname"),
                    "required argument testname");
            String context = conf.getString("contextname", "clusternode");
            mcnt = msh.getMetricContext(context, testname, testingnode);
            try {
                rosetta.init();                       
                
            } catch (UnreachableClusterException ex) {
                Logger.getLogger(AeneasMetricsStorer.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalArgumentException("Unreachable Cluster", ex);
            }
            
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void start() {
        l.writeLock().lock();
        try {
            mcnt.start();
        } finally {
            l.writeLock().unlock();
        }
    }

    @Override
    public void stop() {
        l.writeLock().lock();
        try {
            mcnt.stop();
        } finally {
            l.writeLock().unlock();
        }

    }
     @Inject
    public  Configuration conf;

    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

}
