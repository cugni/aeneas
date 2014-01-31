package es.bsc.cassandrabm.bsc01workloaderimpl;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import es.bsc.cassandrabm.cassandrametricsrecorder.CassandraMetricsReporter;
import es.bsc.cassandrabm.codegenerator.query.AbstractQueryImpl;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.model.marshalling.PointSerializer;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.prettyprint.cassandra.serializers.*;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Query Implementation of KeyComposite created on 17-giu-2012
 *
 */
public class FidXAid
        extends AbstractQueryImpl
        implements QueryInterfaceImpl, TestingInterface {

    final int FRAMES_TO_GET;
    final int ATOMS_TO_GET;
    final int ATOMS_NUMBER;
    final boolean save;
    final int FRAMES_NUMBER;
    final int ROWS_WINDOW;
    final int COLUMNS_WINDOW;
//    final boolean runxatom;
//    final boolean runxframe;

    public FidXAid() {
        super("FidXAid");
        FRAMES_TO_GET = CUtils.getInt("framestoget", 100);
        ATOMS_TO_GET = CUtils.getInt("atomstoget", 838);
        ATOMS_NUMBER = CUtils.getInt("atomsnumber", 99637);
        save = CUtils.getBoolean("saveresults", Boolean.TRUE);
        FRAMES_NUMBER = CUtils.getInt("framesnumber", 1001);
        ROWS_WINDOW = CUtils.getInt("rowswindow", 5000);
        COLUMNS_WINDOW = CUtils.getInt("columnswindow", 60);
//        runxatom = CUtils.getBoolProperty("runxatom", true);
//        runxframe = CUtils.getBoolProperty("runxframe", true);

    }

    public FidXAid(int framestoget,
            int atomsnumber,
            boolean saveresults,
            int framesnumber,
            int rowswindow,
            int columnswindow) {
        super("FidXAid");
        FRAMES_TO_GET = framestoget;
        ATOMS_TO_GET = framestoget;
        ATOMS_NUMBER = atomsnumber;
        save = saveresults;
        FRAMES_NUMBER = framesnumber;
        ROWS_WINDOW = rowswindow;
        COLUMNS_WINDOW = columnswindow;
//        runxatom = CUtils.getBoolProperty("runxatom", true);
//        runxframe = CUtils.getBoolProperty("runxframe", true);
    }

    @Override
    public Map getPoints(Integer frameFrom, Integer frameTo, Integer atomFrom, Integer atomTo, String guagesname) {
        final AtomicInteger tmp_nsub = new AtomicInteger(0);
        CassandraMetricsReporter cmr = CassandraMetricsReporter.getInstance();

        Metrics.newGauge(AidXFid.class, guagesname, new Gauge<Integer>() {
            @Override
            public Integer value() {
                return tmp_nsub.get();
            }
        });
        Table<Object, Object, PointType> points = HashBasedTable.create();
        HashMap map = new HashMap(2);
        IntegerSerializer is = IntegerSerializer.get();
        for (int i = frameFrom; i < frameTo; i += ROWS_WINDOW) {
            //I generate all the row keys. 
            List<Integer> frs = CUtils.getDiscreteRange(i, frameTo, ROWS_WINDOW);
            Integer atomS = atomFrom;
            while (true) {
                tmp_nsub.incrementAndGet();
                long sub_time = System.nanoTime();

                Rows<Integer, Integer, PointType> queryResult1 =
                        HFactory.createMultigetSliceQuery(keyspace, is,
                        is,
                        PointSerializer.get())
                        .setColumnFamily("points").setKeys(frs)
                        .setRange(atomS, atomTo - 1, false, COLUMNS_WINDOW).execute().get();
                if (cmr != null) {
                    cmr.addAsyncMetric(guagesname + ".single", System.nanoTime() - sub_time);
                }
                if (save) {
                    for (Row<Integer, Integer, PointType> rows : queryResult1) {
                        for (HColumn<Integer, PointType> row : rows.getColumnSlice().getColumns()) {
                            checkArgument(null == points.put(rows.getKey(), row.getName(),
                                    row.getValue()),
                                    "Element already present ");
                        }
                    }
                }
                if ((atomTo - 1 - atomS) < COLUMNS_WINDOW) {
                    break;
                } else {
                    atomS += COLUMNS_WINDOW;
                }
            }
        }


        map.put("points", points);
        return map;
    }

    @Override
    public Map getFrameSlice(Integer frameFrom) {
//        if (!runxframe) {
//            return new HashMap();
//        }
        /*
         * Costant declared here to permit multiple testing
         */
        if (frameFrom + FRAMES_TO_GET >= FRAMES_NUMBER) {
            /*
             * This is to be sure each query 
             * retrieves the same number of elements 
             */
            frameFrom = FRAMES_NUMBER - FRAMES_TO_GET;
        }
        Integer frameTo = frameFrom + FRAMES_TO_GET;

        return getPoints(frameFrom, frameTo, 0, ATOMS_NUMBER, "get-frames-subqueries");
    }

    @Override
    public Map getAtomSlice(Integer atomFrom) {
//        if (!runxatom) {
//            return new HashMap();
//        }
        /*
         * Costant declared here to permit multiple testing
         */
        if (atomFrom + ATOMS_TO_GET >= ATOMS_NUMBER) {
            /*
             * This is to be sure each query 
             * retrieves the same number of elements 
             */
            atomFrom = ATOMS_NUMBER - ATOMS_TO_GET;
        }

        Integer atomTo = atomFrom + ATOMS_TO_GET;
        return getPoints(0, FRAMES_NUMBER, atomFrom, atomTo, "get-atoms-subqueries");


    }
}
