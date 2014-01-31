/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.cassandrametricsrecorder;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import com.yammer.metrics.reporting.AbstractPollingReporter;
import es.bsc.cassandrabm.cassandrametricsrecorder.MetricspaceHolder.MetricContext;
import es.bsc.cassandrabm.loader.LightXMLCassandraSetter;
import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;

import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 *
 * @author cesare
 */
public class CassandraMetricsReporter extends AbstractPollingReporter implements MetricProcessor<CassandraMetricsReporter.Context> {

    private static final Logger LOG = Logger.getLogger(CassandraMetricsReporter.class.getName());
    private static AtomicReference<CassandraMetricsReporter> instance = new AtomicReference<CassandraMetricsReporter>();

    /**
     * *
     * Ugly but fast
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public static CassandraMetricsReporter getInstance() {
        return instance.get();
    }

    public static CassandraMetricsReporter create(String context, String testname, MetricspaceHolder ms) {
        String location = null;
        try {
            location = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible to determine the localhost name."
                    + "Set it manually through the testingnode parameter", ex);
        }
        CassandraMetricsReporter cmr = new CassandraMetricsReporter(context, testname, location, ms);
        instance.set(cmr);
        return cmr;

    }

    public static CassandraMetricsReporter create(String context, String testname,
            String serverlocation, String clustername) throws UnreachableClusterException {
        LightXMLCassandraSetter l = new LightXMLCassandraSetter("metricspace.cm.xml", "MetricsReferenceModel.xml");
        l.open(clustername, serverlocation);
        l.configure();
        MetricspaceHolder ms = new MetricspaceHolder(l);
        return CassandraMetricsReporter.create(context, testname, ms);
    }
    public final String testname;
    private final MetricContext mct;
    private final AtomicLong rec_time = new AtomicLong(System.currentTimeMillis());
    private ConcurrentSkipListSet<AsyncMetric> stm = new ConcurrentSkipListSet<AsyncMetric>();
    final Context context = new Context() {
        @Override
        public MetricContext getUpdater() {
            return mct;
        }
    };

    private CassandraMetricsReporter(String context, String testname, String location, MetricspaceHolder ms) {
        super(Metrics.defaultRegistry(), "Cassandra-metrics-reporter");
        //: doesn't work good with cassandra-cli
        this.testname = testname.replaceAll(":", "_");
        mct = ms.getMetricContext(context, testname, location);
    }
    //TODO better dependency injection

    @Override
    public void start(long period, TimeUnit unit) {
        super.start(period, unit);
        mct.start();
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        super.shutdown(timeout, unit);
        mct.stop();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        mct.stop();
    }

    /**
     * *
     * this method adds manually a value which will be stored when the threads
     * run. This method should not block the execution
     *
     * @param propertyName the name of the property (e.g. respo-time)
     * @param value
     */
    public void addAsyncMetric(String propertyName, Object value) {
        stm.add(new AsyncMetric(propertyName, value));
    }

    @Override
    public void run() {
        final Set<Entry<MetricName, Metric>> metrics = getMetricsRegistry().allMetrics().entrySet();
        try {
            rec_time.set(System.currentTimeMillis());
            for (Entry<MetricName, Metric> entry : metrics) {
                final Metric metric = entry.getValue();

                metric.processWith(this, entry.getKey(), context);

            }
            processAsyncMetric(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processAsyncMetric(Context context) {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());
        //TODO write it better
        for (AsyncMetric m : stm) {
            storeMetric.addMetric(m.name, m.value);
            stm.remove(m);
        }

    }

    @Override
    public void processMeter(MetricName name, Metered meter, Context context) throws Exception {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());
        storeMetric.addGroup(name.getName())
                .addMetric("count", meter.count())
                .addMetric("oneMinuteRate", meter.oneMinuteRate())
                .addMetric("meanRate", meter.meanRate())
                .addMetric("fiveMinuteRate", meter.fiveMinuteRate())
                .addMetric("fifteenMinuteRate", meter.fifteenMinuteRate());
    }

    @Override
    public void processCounter(MetricName name, Counter counter, Context context) throws Exception {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());

        storeMetric.addMetric(name.getName(), counter.count());

    }

    @Override
    public void processHistogram(MetricName name, Histogram histogram, Context context) throws Exception {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());
        storeMetric.addGroup(name.getName())
                .addMetric("min", histogram.min())
                .addMetric("max", histogram.max())
                .addMetric("mean", histogram.mean())
                .addMetric("median", histogram.mean())
                .addMetric("stdDev", histogram.stdDev());
        //If you want you can add here the percentile statistics.. 
    }

    @Override
    public void processTimer(MetricName name, Timer timer, Context context) throws Exception {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());
        storeMetric.addGroup(name.getName())
                .addMetric("min", timer.min())
                .addMetric("max", timer.max())
                .addMetric("mean", timer.mean())
                .addMetric("median", timer.mean())
                .addMetric("stdDev", timer.stdDev());

    }

    @Override
    public void processGauge(MetricName name, Gauge<?> gauge, Context context) throws Exception {
        MetricContext.MetricHolder storeMetric = context.getUpdater().storeMetric(rec_time.get());
        long time = rec_time.get();
        storeMetric.addMetric(name.getName(), gauge.value());
    }

    private static class AsyncMetric implements Comparable<AsyncMetric> {

        final String name;
        final Object value;
        final long time;

        AsyncMetric(String name, Object value) {
            time = System.currentTimeMillis();
            this.name = name;
            this.value = value;
        }

        @Override
        public int compareTo(AsyncMetric o) {
            return (int) ((int) time - o.time);
        }
    }

    public interface Context {

        MetricContext getUpdater();
    }
}
