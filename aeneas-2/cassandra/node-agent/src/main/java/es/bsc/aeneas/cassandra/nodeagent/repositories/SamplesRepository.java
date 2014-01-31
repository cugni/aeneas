/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.nodeagent.repositories;

import es.bsc.aeneas.core.nodeagent.model.Samples;
import es.bsc.aeneas.core.nodeagent.repositories.SamplesRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ccugnasc
 */
@Repository
public class SamplesRepository implements SamplesRepo {

    private final Cluster cluster;
    private final Keyspace ksp;
    private final ColumnFamilyTemplate<String, DynamicComposite> metricsTemplate;
    private final int MAX_RESULTS_SIZE;
    private final int MAX_NODES;
    private static final Logger LOG = Logger.getLogger(SamplesRepository.class.getName());

    @Inject
    SamplesRepository(Configuration conf) {
        MAX_RESULTS_SIZE = conf.getInt("querycontroller.maxresultssize", 10000);
        MAX_NODES = conf.getInt("querycontroller.maxnodes", 200);
        String clusterlocation = conf.getString("aneas-recorder.clusterlocation");
        String clustername = conf.getString("aneas-recorder.clustername");
        LOG.log(Level.INFO, "Connectiong to the cluster {0} at address {1}", new Object[]{clustername, clusterlocation});
        cluster = HFactory.getOrCreateCluster(clustername, clusterlocation);
        ksp = HFactory.createKeyspace("metricspace", cluster);
        metricsTemplate =
                new ThriftColumnFamilyTemplate<String, DynamicComposite>(ksp,
                "lists", StringSerializer.get(), new DynamicCompositeSerializer());

    }

    /**
     * This method retrieves all the samples for the provide metric, test and
     * node.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @return an object Samples containing the result
     */
    @Override
    public Samples getSamples(String test, String context, String metric, String node) {

        Composite from = new Composite();
        from.addComponent(0, metric, AbstractComposite.ComponentEquality.EQUAL);

        Composite to = new Composite();
        to.addComponent(0, metric, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);


        QueryResult< ColumnSlice< Composite, DynamicComposite>> q =
                HFactory.createSliceQuery(ksp, CompositeSerializer.get(),
                CompositeSerializer.get(), DynamicCompositeSerializer.get())
                .setColumnFamily("metrics")
                // [context,testname,location]
                .setKey(new Composite(context, test, metric, node))
                // [metricname,time]
                .setRange(from, to, false, MAX_RESULTS_SIZE).execute();
        List<HColumn<Composite, DynamicComposite>> columns = q.get().getColumns();
        List<Long> times = new ArrayList<Long>(columns.size());
        List objs = new ArrayList(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            HColumn<Composite, DynamicComposite> c = columns.get(i);

            times.add(c.getName().get(0, LongSerializer.get()));
            objs.add(c.getValue().get(0));
        }
        return new Samples(test, context, metric, node, times, objs);
    }

    /**
     * This method retrieves at most #results samples for the provide metric,
     * test and node where the samples are older then tfrom.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @param tfrom
     * @param results
     * @return an object Samples containing the result
     */
    @Override
    public Samples getSamples(String test, String context, String metric, String node, long tfrom, Integer results) {
        if (results == null) {
            results = MAX_RESULTS_SIZE;
        }
        Composite from = new Composite(metric, tfrom);
        Composite to = new Composite();
        to.addComponent(0, metric, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);


        QueryResult< ColumnSlice< Composite, DynamicComposite>> q =
                HFactory.createSliceQuery(ksp, CompositeSerializer.get(),
                CompositeSerializer.get(), DynamicCompositeSerializer.get())
                .setColumnFamily("metrics")
                // [context,testname,location]
                .setKey(new Composite(context, test, metric, node))
                // [metricname,time]
                .setRange(from, to, false, results).execute();
        List<HColumn<Composite, DynamicComposite>> columns = q.get().getColumns();
        List<Long> times = new ArrayList<Long>(columns.size());
        List objs = new ArrayList(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            HColumn<Composite, DynamicComposite> c = columns.get(i);

            times.add(c.getName().get(0, LongSerializer.get()));
            objs.add(c.getValue().get(0));
        }
        return new Samples(test, context, metric, node, times, objs);


    }

    /**
     * The method returns the last sample for the provided parameters.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @return
     */
    @Override
    public Samples getLastSamples(String test, String context, String metric, String node) {
        return getSamples(test, context, metric, node, false, null);
    }

    private Samples getSamples(String test, String context, String metric, String node, boolean first, Long tfrom) {


        Composite from, to;
        if (tfrom == null) {
            from = new Composite();
            from.addComponent(0, metric, AbstractComposite.ComponentEquality.EQUAL);

            to = new Composite();
            to.addComponent(0, metric, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);
        } else {
            from = new Composite(metric, tfrom);
            to = new Composite();
            to.addComponent(0, metric, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);
        }



        QueryResult< ColumnSlice< Composite, DynamicComposite>> q =
                HFactory.createSliceQuery(ksp, CompositeSerializer.get(),
                CompositeSerializer.get(), DynamicCompositeSerializer.get())
                .setColumnFamily("metrics")
                // [context,testname,location]
                .setKey(new Composite(context, test, metric, node))
                // [metricname,time]
                .setRange(from, to, !first, 1).execute();
        List<HColumn<Composite, DynamicComposite>> columns = q.get().getColumns();
        List<Long> times = new ArrayList<Long>(1);
        List objs = new ArrayList(1);
        for (int i = 0; i < columns.size(); i++) {
            HColumn<Composite, DynamicComposite> c = columns.get(i);

            times.add(c.getName().get(0, LongSerializer.get()));
            objs.add(c.getValue().get(0));
        }
        return new Samples(test, context, metric, node, times, objs);

    }

    /**
     * The method returns the fist sample for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @return
     */
    @Override
    public Samples getFistSamples(String test, String context, String metric, String node) {
        return getSamples(test, context, metric, node, true, null);
    }

    /**
     * The method returns the fist sample after
     * <code>from</code>, for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @param from
     * @return
     */
    @Override
    public Samples getFistSamples(String test, String context, String metric, String node, Long from) {
        return getSamples(test, context, metric, node, true, from);
    }

    /**
     * The method returns the last sample before
     * <code>from</code>, for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @param from
     * @return
     */
    @Override
    public Samples getLastSamples(String test, String context, String metric, String node, Long from) {
        return getSamples(test, context, metric, node, false, from);
    }
}
