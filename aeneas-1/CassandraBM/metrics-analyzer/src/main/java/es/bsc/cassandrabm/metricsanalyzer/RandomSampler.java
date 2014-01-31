package es.bsc.cassandrabm.metricsanalyzer;

import es.bsc.cassandrabm.commons.CUtils;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.commons.csv.CSVPrinter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class RandomSampler {

    private static final Logger log = Logger.getLogger(RandomSampler.class.getName());

    public static void main(String[] args) {
        assert args.length == 2 : "missing arguments";
        String node = args[0];
        String test = args[1];
        log.log(Level.INFO, "Testing {0} {1}", new Object[]{node, test});
        CSVPrinter cp = new CSVPrinter(System.out);
        Cluster cluster = HFactory.getOrCreateCluster("CBMetrics", "localhost:9160");
        List<Double> keys = new ArrayList<Double>();
        for (Double i = 991.0; i <= 1866.0; i += 1.0) {
            keys.add(i);
        }
        final Random r = CUtils.random();
        Collections.sort(keys, new Comparator<Double>() {
            public int compare(Double o1, Double o2) {
                return r.nextInt();
            }
        });
        keys=keys.subList(0, keys.size()/100);
        Keyspace ksp = HFactory.createKeyspace("RP2NormLevelPerfect", cluster);
        ColumnFamilyTemplate<Double, Composite> template = 
                new ThriftColumnFamilyTemplate<Double, Composite>(ksp,
                "pointsindex",
                DoubleSerializer.get(),
                CompositeSerializer.get());
        ColumnFamilyResult<Double, Composite> res = template
                .queryColumns(keys);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        for (Composite name : res.getColumnNames()) {
            Date d = new Date(name.get(0, LongSerializer.get()));

            ArrayList<String> v = new ArrayList();
            v.add(sd.format(d));
            for (int i = 1; i < name.size(); i++) {
                v.add(name.get(i).toString());
            }
            DynamicComposite value =
                    DynamicCompositeSerializer.get()
                    .fromByteBuffer(res.getColumn(name).getValue());
            for (int i = 0; i < value.size(); i++) {
                v.add(value.get(i).toString());
            }
            cp.println(v.toArray(new String[v.size()]));


        }

    }
}
