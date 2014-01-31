package es.bsc.cassandrabm.bsc01workloaderimpl;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import es.bsc.cassandrabm.codegenerator.query.AbstractQueryImpl;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.model.marshalling.IntegerClusterer;
import es.bsc.cassandrabm.model.marshalling.PointSerializer;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.util.ArrayList;
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
public class AFsemiclustered
        extends AbstractQueryImpl
        implements QueryInterfaceImpl, TestingInterface {

    final int FRAMES_TO_GET;
    final int ATOMS_TO_GET;
    final int ATOMS_NUMBER;
    final boolean save;
    final int FRAMES_NUMBER;
    final int ELEMENTS_FOR_CLUSTER;
    final int ROWS_WINDOW;
   

    public AFsemiclustered() {
        super("AFsemiclustered");
        ELEMENTS_FOR_CLUSTER = CUtils.getInt("elementforcluster", 32);
        FRAMES_TO_GET = CUtils.getInt("framestoget", 100);
        ATOMS_TO_GET = CUtils.getInt("atomstoget", 838);
        ATOMS_NUMBER = CUtils.getInt("atomsnumber", 8380);
        save = CUtils.getBoolean("saveresults", Boolean.TRUE);
        FRAMES_NUMBER = CUtils.getInt("framesnumber", 1000);
        ROWS_WINDOW = CUtils.getInt("rowswindow", 320);
       
    }

    protected AFsemiclustered(String keyspace, int elementforcluster) {
        super(keyspace);
        ELEMENTS_FOR_CLUSTER = CUtils.getInt("elementforcluster", elementforcluster);

        FRAMES_TO_GET = CUtils.getInt("framestoget", 100);
        ATOMS_TO_GET = CUtils.getInt("framestoget", 838);
        ATOMS_NUMBER = CUtils.getInt("atomsnumber", 8380);
        save = CUtils.getBoolean("saveresults", Boolean.TRUE);
        FRAMES_NUMBER = CUtils.getInt("framesnumber", 1000);
        ROWS_WINDOW = CUtils.getInt("rowswindow", 320);
      
    }

    public AFsemiclustered(int framestoget,
            int atomsnumber,
            boolean saveresults,
            int framesnumber,
            int rowswindow) {
        this("AFsemiclustered", 32, framestoget, atomsnumber,
                saveresults, framesnumber, rowswindow);

    }

    /**
     *
     * @param keyspace
     * @param elementforcluster
     * @param framestoget
     * @param atomsnumber
     * @param saveresults
     * @param framesnumber
     * @param rowswindow
     * @param columnswindow
     */
    protected AFsemiclustered(String keyspace,
            int elementforcluster,
            int framestoget,
            int atomsnumber,
            boolean saveresults,
            int framesnumber,
            int rowswindow) {
        super(keyspace);

        ELEMENTS_FOR_CLUSTER = elementforcluster;

        FRAMES_TO_GET = framestoget;
        ATOMS_TO_GET = framestoget;
        ATOMS_NUMBER = atomsnumber;
        save = saveresults;
        FRAMES_NUMBER = framesnumber;
        ROWS_WINDOW = rowswindow;
       
    }

    private static List<Composite> combineKeys(List<Integer> ks1, List<Integer> ks2) {
        List<Composite> list = Lists.newArrayListWithCapacity(ks1.size() * ks2.size());
        for (Integer k1 : ks1) {
            for (Integer k2 : ks2) {
                list.add(new Composite(k1, k2));
            }
        }
        return list;
    }

   
    @Override
    public Map getFrameSlice(Integer frameFrom) {
        if (frameFrom + FRAMES_TO_GET > FRAMES_NUMBER) {
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
        if (atomFrom + ATOMS_TO_GET > ATOMS_NUMBER) {
            /*
             * This is to be sure each query 
             * retrieves the same number of elements 
             */
            atomFrom = ATOMS_NUMBER - ATOMS_TO_GET;
        }
        Integer atomTo = atomFrom + ATOMS_TO_GET;
        return getPoints(0, FRAMES_NUMBER, atomFrom, atomTo, "get-frames-subqueries");
    }

    @Override
    public Map getPoints(Integer frameFrom, Integer frameTo, Integer atomFrom, Integer atomTo, String guagesname) {
        final AtomicInteger tmp_nsub = new AtomicInteger(0);
        Metrics.newGauge(AidXFid.class, guagesname, new Gauge<Integer>() {
            @Override
            public Integer value() {
                return tmp_nsub.get();
            }
        });
        Table<Object, Object, PointType> points = HashBasedTable.create();
        HashMap map = new HashMap(2);
        IntegerSerializer is = IntegerSerializer.get();
        CompositeSerializer cs = CompositeSerializer.get();
        IntegerClusterer atom_clus = new IntegerClusterer();
        atom_clus.setGrouping(0, ELEMENTS_FOR_CLUSTER - 1, 1);
        /*they memorize the maximum frame and atoms id retrieved 
         * (for each cluster)
         */

        List<Integer> atoms_clus_id = atom_clus.getGroupsInterval(atomFrom, atomTo);
        List<Composite> keys = combineKeys(
                CUtils.getDiscreteRange(frameFrom, frameTo), atoms_clus_id);
        int key_from = 0;
        while (key_from <= keys.size()) {
            tmp_nsub.incrementAndGet();
            int key_to = keys.size() < (key_from + ROWS_WINDOW) ? keys.size() : key_from + ROWS_WINDOW;
            Rows<Composite, Integer, PointType> queryResult1 =
                    HFactory.createMultigetSliceQuery(keyspace, cs, is,
                    PointSerializer.get())
                    .setColumnFamily("points").setKeys(
                    keys.subList(key_from, key_to))
                    .setRange(atomFrom, atomTo-1, false, ELEMENTS_FOR_CLUSTER + 1).execute().get();
            if (save) {
                for (Row<Composite, Integer, PointType> rows : queryResult1) {
                    for (HColumn<Integer, PointType> row :
                            rows.getColumnSlice().getColumns()) {
                        checkArgument(points.put(
                                //frame max number                            
                                rows.getKey().get(0, is),
                                //atom max number
                                row.getName(),
                                row.getValue()) == null, "Element already inserted");

                    }
                }
            }
            key_from += ROWS_WINDOW;
            key_from++;
        }
        map.put("points", points);
        return map;
    }
}
