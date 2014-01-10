package es.bsc.aeneas.loader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import com.yammer.metrics.core.Timer;
import es.bsc.aeneas.commons.CUtils;
import es.bsc.aeneas.loader.exceptions.InvalidGetRequest;
import es.bsc.aeneas.loader.exceptions.InvalidPutRequest;
import es.bsc.aeneas.loader.exceptions.NotSupportedQuery;
import es.bsc.aeneas.loader.exceptions.UnreachableClusterException;
import es.bsc.aeneas.cassandra.mapping.Ks;
import es.bsc.aeneas.model.gen.RootType;
import es.bsc.aeneas.model.util.GenUtils;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import org.apache.commons.configuration.Configuration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class XMLCassandraSetter extends AbstractCassandraDB {

    public final boolean batched = CUtils.getBoolean("batched", true);
    public final boolean nothreads = CUtils.getBoolean("nothreads", false);
    private final boolean dropMetricspace = CUtils.getBoolean("dropMetricspace", false);
    private final int QUEUE_LENGTH = CUtils.getInt("queuelength", 100);
    private final int MAX_BATCH_SIZE = CUtils.getInt("maxbatchsize", 50);
    private final HConsistencyLevel readconsistency = HConsistencyLevel.valueOf(CUtils.getString("readconsistency", HConsistencyLevel.QUORUM.name()));
    private final HConsistencyLevel writeconsistency = HConsistencyLevel.valueOf(CUtils.getString("writeconsistency",
            HConsistencyLevel.ANY.name()));
    private final boolean autoDiscoverHosts = CUtils.getBoolean("autoDiscoverHosts", true);
    private Histogram mutatorResponseTime;
    private Histogram mutatorElements;
    private static Timer get = Metrics.newTimer(XMLCassandraSetter.class, "gets-time", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static Timer putsAll = Metrics.newTimer(XMLCassandraSetter.class, "puts-time-all", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static Counter threadsleeping = Metrics.newCounter(XMLCassandraSetter.class, "thread-in-sleeping");
    private final ArrayBlockingQueue<PathMatch.PutRequest> queue = new ArrayBlockingQueue<PathMatch.PutRequest>(QUEUE_LENGTH, true);
    private Ks ks;
    private static final Logger log = Logger.getLogger(XMLCassandraSetter.class.getName());
    private PathMatchMap pathTempl = null;
    private RootType root;
    private volatile Keyspace keyspace;
    private volatile boolean close = false;
    private CountDownLatch deaemonscount;
    private final Histogram mutatorResponseTimeBiased;

    public XMLCassandraSetter(String cassandraModelName) {
        this(GenUtils.getCassandraModelFile(cassandraModelName));
    }

    public XMLCassandraSetter(URL cassandraModel) {

//        checkArgument(cassandraModel.exists(),
//                "Cassandra model file not found: {0}", cassandraModel.getAbsolutePath());
        ks = GenUtils.getCassandraModel(cassandraModel);
        root = GenUtils.getReferenceModel();
        Metrics.newGauge(XMLCassandraSetter.class, "Queue-size", new Gauge<Integer>() {
            @Override
            public Integer value() {
                return queue.size();
            }
        });
        Metrics.newGauge(XMLCassandraSetter.class, "Queue-remaing-size", new Gauge<Integer>() {
            @Override
            public Integer value() {
                return queue.remainingCapacity();
            }
        });
        if (batched) {
            mutatorResponseTime = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-response-time");
            mutatorResponseTimeBiased = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-response-time-biased", true);
            mutatorElements = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-elements");
        } else {
            mutatorElements = null;
            mutatorElements = null;
            mutatorResponseTimeBiased = null;
        }
    }

    @Override
    public Object testGet(Object value, Object... path) throws InvalidGetRequest, NotSupportedQuery {
        TimerContext time = get.time();
        try {
            Set<PathMatch> templateMatch = pathTempl.getTemplateMatch(path);
            if (templateMatch.isEmpty()) {
                throw new NotSupportedQuery("No path match found");
            }
            Object res = null;
            for (PathMatch pm : templateMatch) {
                res = pm.testGet(value, path);

            }
            if (res != null) {
                return res;
            }
            log.log(Level.WARNING, "Impossible read the value {0} with the path {1}",
                    new Object[]{value, Arrays.toString(path)});
            throw new NotSupportedQuery("Impossible to retrieve the data");
        } finally {
            time.stop();
        }

    }

    /**
     * If the keyspace "metricspace" does not exist, it create it. Otherwise if 
     * the parameter dropMetricspace is set to true, it drops and recreates the
     * keyspace. 
     * 
     */
    @Override
    public void configure() {
        if (checkNotNull(cluster, "Is required to call the open method before the configure one")
                .describeKeyspace(ks.name) == null) {
            //Add the schema to the cluster.
            //"true" as the second param means that Hector will block until all nodes see the change.
            cluster.addKeyspace(getKeyspaceDefinition(), true);
        } else if (dropMetricspace) {
            cluster.dropKeyspace(ks.name, true);
            cluster.addKeyspace(getKeyspaceDefinition(), true);

        }


    }
    private KeyspaceDefinition kd = null;

    @Override
    public KeyspaceDefinition getKeyspaceDefinition() {
        if (kd != null) {
            return kd;
        }
        kd = ks.createKeyspaceDefinition();

        return kd;
    }

    @Override
    public void put(Object value, Object... path) throws InvalidPutRequest {
        checkNotNull(keyspace, "Invoke open() before use this method");
        //log.log(Level.INFO, "[grepme]Put on value {0} with {1} path", new Object[]{value, Arrays.toString(path)});
        Set<PathMatch> templateMatch =
                checkNotNull(pathTempl, "Invoke open() before use this method").getTemplateMatch(path);
        if (templateMatch.isEmpty()) {
            log.log(Level.FINE, "path without match {}", Arrays.toString(path));
            return;
        }
        for (PathMatch pm : templateMatch) {
            try {
                if (nothreads) {
                    pm.pinsert(value, path);
                } else {
                    queue.put(pm.getPutRequest(value, path));
                }
            } catch (InterruptedException ex) {
                log.log(Level.SEVERE, "InterruptedException on the queue", ex);
                throw new IllegalArgumentException(ex);
            }
        }
    }

    public Keyspace getKeyspace() {
        return checkNotNull(keyspace,
                "Invoke DBSource.open() before call this method");
    }

    @Override
    public void close() {
        super.close();
        //     queue.clear();
        close = true;
        if (nothreads) {
            return;
        }
        try {
            deaemonscount.await();
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, "Exception while waiting the daemons termination", ex);
            throw new IllegalArgumentException(ex);
        }

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void open(String clusterName, String location) throws UnreachableClusterException {
        super.open(clusterName, location);

        ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
        Map<String, HConsistencyLevel> clread = new HashMap<String, HConsistencyLevel>(4);
        Map<String, HConsistencyLevel> clwrite = new HashMap<String, HConsistencyLevel>(4);
        for (ColumnFamilyDefinition cfd : getKeyspaceDefinition().getCfDefs()) {
            clread.put(cfd.getName(), readconsistency);
            clwrite.put(cfd.getName(), writeconsistency);
        }
        configurableConsistencyLevel.setReadCfConsistencyLevels(clread);
        configurableConsistencyLevel.setWriteCfConsistencyLevels(clwrite);

        CassandraHostConfigurator conf = new CassandraHostConfigurator();
        conf.setAutoDiscoverHosts(autoDiscoverHosts);
        keyspace = HFactory.createKeyspace(getKeyspaceDefinition().getName(),
                cluster, configurableConsistencyLevel);

        pathTempl = new PathMatchMap(ks, keyspace, root);
        if (!nothreads) {
            Loader loader = Loader.getInstance();
            List<Callable<Boolean>> daemons = new ArrayList<Callable<Boolean>>(Loader.NUMBER_OF_THREADS);
            for (int i = 0; i
                    < Loader.NUMBER_OF_THREADS; i++) {
                daemons.add(new PutDaemon());
            }
            deaemonscount = loader.submitConsumerDaemons(daemons);
            log.log(Level.INFO, "Queue size: {0}", QUEUE_LENGTH);
        } else {
            log.log(Level.INFO, "Single thread implementation");
        }

    }

    PathMatchMap getPathMatchMap() {
        return pathTempl;
    }

    public boolean isClose() {
        return close;
    }

    private class PutDaemon implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {

            while (!close || queue.size() > 0) {


                PathMatch.PutRequest take;
                /*
                 * This is done to avoid that two (or more) thread are trying to
                 * empty the queue for the last round (after have close the
                 * source)
                 *
                 */
                threadsleeping.inc();
                take = queue.poll(10, TimeUnit.SECONDS);
                threadsleeping.dec();
                if (take == null) {
                    continue;
                }

                TimerContext time = putsAll.time();

                try {
                    if (batched) {

                        Multimap<Class<? extends Serializer>, PathMatch.PutRequest> puts = HashMultimap.create(4, QUEUE_LENGTH);
                        puts.put(take.getKeySerializer().getClass(), take);
                        PathMatch.PutRequest tmp = queue.poll();
                        int size = 1;//TODO find a better policy
                        while (tmp != null && size < MAX_BATCH_SIZE) {
                            puts.put(tmp.getKeySerializer().getClass(), tmp);
                            size++;
                            tmp = queue.poll();
                        }
                        if (tmp != null) {
                            puts.put(tmp.getKeySerializer().getClass(), tmp);
                        }
                        mutatorElements.update(size);
                        for (Class<? extends Serializer> sclass : puts.keySet()) {
                            Serializer s = Serializers.getSerializerBySerializerClass(sclass);
                            Mutator mut = HFactory.createMutator(checkNotNull(keyspace, "Keyspace null"),
                                    checkNotNull(s, "Serializer"));
                            for (PathMatch.PutRequest pm : puts.get(sclass)) {
                                pm.insert(mut);
                            }
                            long executionTimeNano = mut.execute().getExecutionTimeNano();
                            mutatorResponseTime.update(executionTimeNano);
                            mutatorResponseTimeBiased.update(executionTimeNano);
                        }
                    } else {
                        take.insert();
                    }
                } finally {
                    time.stop();


                }
            }
            log.log(Level.INFO, "PutDaemon {0} teminated", this);
            return true;

        }
    }
}
