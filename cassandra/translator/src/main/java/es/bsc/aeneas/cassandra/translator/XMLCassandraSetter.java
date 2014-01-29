package es.bsc.aeneas.cassandra.translator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import com.yammer.metrics.core.Timer;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.cassandra.mapping.CF;
import es.bsc.aeneas.cassandra.mapping.Col;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.Mapping;
import es.bsc.aeneas.core.rosetta.Result;
import me.prettyprint.hector.api.beans.HColumn;

public class XMLCassandraSetter extends AbstractCassandraDB {

    private final int QUEUE_LENGTH = conf.getInt("queuelength", 100);
    private final int MAX_BATCH_SIZE = conf.getInt("maxbatchsize", 50);
    private Histogram mutatorResponseTime;
    private Histogram mutatorElements;
    private static Timer get = Metrics.newTimer(XMLCassandraSetter.class, "gets-time", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static Timer putsAll = Metrics.newTimer(XMLCassandraSetter.class, "puts-time-all", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static Counter threadsleeping = Metrics.newCounter(XMLCassandraSetter.class, "thread-in-sleeping");
    private final ArrayBlockingQueue<PutRequest> queue =
            new ArrayBlockingQueue<PutRequest>(QUEUE_LENGTH, true);
    private static final Logger log = Logger.getLogger(XMLCassandraSetter.class.getName());
    private volatile boolean close = false;
    private final Histogram mutatorResponseTimeBiased;

    public XMLCassandraSetter(URL cassandraModel) {
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

        mutatorResponseTime = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-response-time");
        mutatorResponseTimeBiased = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-response-time-biased", true);
        mutatorElements = Metrics.newHistogram(XMLCassandraSetter.class, "mutator-elements");

    }

    @Override
    public void close() {
        super.close();
        //     queue.clear();
        close = true;


    }

    @Override
    public void init(ClusterType cd) throws UnreachableClusterException {
        super.init(cd);
        log.log(Level.INFO, "Queue size: {0}", QUEUE_LENGTH);
    }

    public boolean isClose() {
        return close;
    }

    @Override
    public Callable<Result> query(CrudType ct, String matchid, ImmutableList<Mapping> match) {
        PutRequest rh = new PutRequest(cassa.getMatch(matchid), match);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class PutDaemon implements Callable<Result> {

        private final Col column;

        PutDaemon(Col column) {
            this.column = column;

        }

        @Override
        public Result call() throws Exception {

            while (!close || queue.size() > 0) {


                PutRequest take;
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

                    Multimap<CF, PutRequest> puts = HashMultimap.create(4, QUEUE_LENGTH);
                    puts.put(take.columnClass.cf, take);
                    PutRequest tmp = queue.poll();
                    int size = 1;//TODO find a better policy
                    while (tmp != null && size < MAX_BATCH_SIZE) {
                        puts.put(tmp.columnClass.cf, tmp);
                        size++;
                        tmp = queue.poll();
                    }
                    if (tmp != null) {
                        puts.put(tmp.columnClass.cf, tmp);
                    }
                    mutatorElements.update(size);
                    for (CF cf : puts.keySet()) {


                        Mutator mut = HFactory.createMutator(
                                cf.ks.ksp,
                                cf.keySerializer);
                        for (PutRequest pm : puts.get(cf)) {
                            pm.insert(mut);
                        }
                        long executionTimeNano = mut.execute().getExecutionTimeNano();
                        mutatorResponseTime.update(executionTimeNano);
                        mutatorResponseTimeBiased.update(executionTimeNano);
                    }

                } finally {
                    time.stop();

                }
            }
            log.log(Level.INFO, "PutDaemon {0} teminated", this);
            Result r = new Result();
            r.setSuccess(true);
            return r;

        }
    }

    public class PutRequest {

        final Col columnClass;
        final ImmutableList<Mapping> match;

        public PutRequest(Col columnClass, ImmutableList<Mapping> mappings) {
            this.columnClass = columnClass;
            this.match = mappings;
        }

        /*
         * This method is used for batched insertion
         */
        public void insert(Mutator mut) {
            try {
                Object key = columnClass.getKey(match);
                Object cn = columnClass.getColumnName(match);
                Object value = columnClass.getValue(match);
                HColumn col = HFactory.createColumn(cn,
                        value,
                        (Serializer) columnClass.cf.columnNameSerializer,
                        (Serializer) columnClass.getValueSerializer());
                mut.addInsertion(key, columnClass.cf.name, col);
            } catch (Exception e) {
                throw new RuntimeException("Exception inserting a value "
                        + " with the path " + columnClass.printPath(), e);
            }

        }
    }
}
