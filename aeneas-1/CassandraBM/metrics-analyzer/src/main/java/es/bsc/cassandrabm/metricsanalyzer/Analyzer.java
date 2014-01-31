package es.bsc.cassandrabm.metricsanalyzer;

import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;
import es.bsc.cassandrabm.commons.CUtils;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVStrategy;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Hello world!
 *
 */
public class Analyzer {

    private static final Logger log = Logger.getLogger(Analyzer.class.getName());
    private static long tw = (1 << Math.round(Math.log(CUtils.getInt("timewindow", 5) * 1000) / Math.log(2))) - 1;
    private static RowSortedTable<ComposeKey<Date, String>, String, Object> table = TreeBasedTable.create();

    public static void main(String[] args) {
        String context;
        String test;
        String node;
        Composite ke;
        switch (args.length) {
            case 3:
                context = args[0];
                test = args[1];
                node = args[2];
               ke= new Composite(context, test, node);
                break;
            case 2:
                context = args[0];
                test = args[1];
                node = ""; //for compatibily with old models
                 ke= new Composite(context, test);
                break;
            case 1:
                String[] tmp = args[0].split(":");
                context = tmp[0];

                test = tmp[1];
                node = tmp[2];
                 ke= new Composite(context, test, node);
                break;
            default:
                throw new IllegalArgumentException("Wrong number of arguments");

        }
        log.log(Level.INFO, "Testing {0} {1}", new Object[]{node, test});

        Cluster cluster = HFactory.getOrCreateCluster("CBMetrics", CUtils.getString("clusterlocation", "127.0.0.1:9160"));

        Keyspace ksp = HFactory.createKeyspace("metricspace", cluster);
        DynamicComposite from = new DynamicComposite();

        from.addComponent(0, 0L, AbstractComposite.ComponentEquality.EQUAL);
        DynamicComposite to = new DynamicComposite();
        to.addComponent(0, Long.MAX_VALUE, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);

        //     SimpleDateFormat sd = new SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa");

        while (true) {
            ColumnSlice<DynamicComposite, DynamicComposite> get =
                    HFactory.createSliceQuery(ksp, CompositeSerializer.get(), DynamicCompositeSerializer.get(),
                    DynamicCompositeSerializer.get()).setColumnFamily("metrics").setKey(ke)
                    .setRange(from, null, false, 200).execute().get();
            if (get.getColumns().isEmpty()) {
                break;
            }

            DynamicComposite name = null;
            for (HColumn<DynamicComposite, DynamicComposite> res : get.getColumns()) {
                name = res.getName();
                Date d = new Date(name.get(0, LongSerializer.get()) & (~(long) tw));
                ComposeKey<Date, String> key = new ComposeKey<Date, String>(d, test + "_" + node);
                StringBuilder trow = new StringBuilder();
                for (int i = 1; i < name.size(); i++) {
                    trow.append(name.get(i).toString()).append("-");
                }
                String colname = trow.subSequence(0, trow.length() - 1).toString();
                String val = res.getValue().get(0).toString();
                table.put(key, colname, val);

            }
            from = name;
            from.setEquality(AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);
        }

        CSVPrinter cp = new CSVPrinter(System.out);
        cp.setStrategy(CSVStrategy.EXCEL_STRATEGY);
        List<String> cols = new ArrayList<String>();
        String columns = CUtils.getString("columns");
        if (columns == null) {
            cols.addAll(table.columnKeySet());
        } else {
            cols.addAll(Arrays.asList(columns.split(",")));
        }
        Collections.sort(cols);
        if (CUtils.getBoolean("printheader", true)) {
            cp.print("date");
            cp.print("test");
            cp.println(cols.toArray(new String[0]));
        }
        String filter = CUtils.getString("filter");
        String filter_col = null;
        String filter_regex = null;
        if (filter != null) {
            filter_col = checkNotNull(filter.split("=")[0], "malformed filter definition");
            filter_regex = checkNotNull(filter.split("=")[1], "malformed filter definition");

        }
        for (ComposeKey<Date, String> k : table.rowKeySet()) {
            ArrayList<String> line = new ArrayList();
            line.add(k.k1.toGMTString());
            line.add(k.k2);
            boolean skip_line = false;
            for (String c : cols) {
                Object get = table.get(k, c);
                if (filter != null && filter_col.equals(c) && get.toString().matches(filter_regex)) {
                    skip_line = true;
                    break;
                }
                if (get == null) {
                    get = "NOTRECORDED";
                }
                line.add(get.toString());
            }
            if (!skip_line) {
                cp.println(line.toArray(new String[line.size()]));
            }
        }

    }

    private static class ComposeKey<K extends Comparable, V extends Comparable> implements Comparable<ComposeKey> {

        final K k1;
        final V k2;

        ComposeKey(K k1, V k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.k1 != null ? this.k1.hashCode() : 0);
            hash = 37 * hash + (this.k2 != null ? this.k2.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ComposeKey<K, V> other = (ComposeKey<K, V>) obj;
            if (this.k1 != other.k1 && (this.k1 == null || !this.k1.equals(other.k1))) {
                return false;
            }
            if (this.k2 != other.k2 && (this.k2 == null || !this.k2.equals(other.k2))) {
                return false;
            }
            return true;
        }

        @Override
        public int compareTo(ComposeKey o) {
            int k1dif = this.k1.compareTo(o.k1);
            if (k1dif != 0) {
                return k1dif;
            } else {
                return this.k2.compareTo(o.k2);
            }
        }
    }
}
