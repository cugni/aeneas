package es.bsc.cassandrabm.workloader.query;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import es.bsc.cassandrabm.codegenerator.query.AbstractQueryImpl;
import es.bsc.cassandrabm.codegenerator.query.MetricsList;
import es.bsc.cassandrabm.codegenerator.query.QueryNotImplementedException;
import es.bsc.cassandrabm.model.marshalling.BoxSerializer;
import es.bsc.cassandrabm.model.marshalling.BoxType;
import es.bsc.cassandrabm.model.marshalling.PointSerializer;
import es.bsc.cassandrabm.model.marshalling.PointType;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.*;


/**
 * Query Implementation of Test created on 17-giu-2012
 *
 */
public class Test
        extends AbstractQueryImpl
        implements QueryInterfaceImpl {

    public Test() {
        super("testspace");
    }

    /**
     *
     */
    @Override
    public Map getMetadata() {
        Integer natoms;
        Integer prec;
        HashMap map = new HashMap(2);
        //mtempalte statement
        ColumnFamilyTemplate<Integer, String> template1 =
                new ThriftColumnFamilyTemplate<Integer, String>((keyspace),
                "framemetadata", new IntegerSerializer(), new StringSerializer());
        ColumnFamilyResult<Integer, String> result1 =
                template1.queryColumns( -1, Arrays.asList("natoms","prec"));
        natoms = new IntegerSerializer().fromBytes(result1.getByteArray("natoms")).intValue();
        prec = new IntegerSerializer().fromBytes(result1.getByteArray("prec")).intValue();
        map.put("prec", prec);
        map.put("natoms", natoms);
        return map;
    }

    /**
     *
     */
    @Override
    public Map getFrameInfo(Integer frame) {
        Integer step;
        BoxType box;
        HashMap map = new HashMap(2);      
        //mtempalte statement
        ColumnFamilyTemplate<Integer, String> template1 =
                new ThriftColumnFamilyTemplate<Integer, String>((keyspace),
                "framemetadata", new IntegerSerializer(), new StringSerializer());
        ColumnFamilyResult<Integer, String> result1 = template1.queryColumns((frame), 
                Arrays.asList("step","box"));
        step = new IntegerSerializer().fromBytes(result1.getByteArray("step")).intValue();
        box = new BoxSerializer().fromBytes(result1.getByteArray("box"));
        map.put("box", box);
        map.put("step", step);
        return map;
    }

    /**
     *
     */
    @Override
    public Map getFramePoints(Integer frame, Integer pointFrom, Integer pointTo) {
        List<PointType> points = new ArrayList();
        HashMap map = new HashMap();
        //Slice query statement
        ColumnSlice<Integer, PointType> queryResult1 =
                HFactory.createSliceQuery(keyspace, new IntegerSerializer(),
                new IntegerSerializer(),
                new PointSerializer()).setColumnFamily("frame").setKey((frame)) //
                .setRange((pointFrom), (pointTo), false, (1000)).execute().get();
        for (HColumn<Integer, PointType> row : queryResult1.getColumns()) {
            points.add(row.getValue());
        }
        map.put("points", points);
        return map;
    }

    /**
     *
     */
    @Override
    public Map getFrameRangePoints(Integer frameFrom, Integer frameTo, Integer pointFrom, Integer pointTo) {
        Table<Object, Object, PointType> points = HashBasedTable.create();
        HashMap map = new HashMap();
        //range Slice statement
        Rows<Integer, Integer, PointType> queryResult1 =
                HFactory.createMultigetSliceQuery(keyspace, new IntegerSerializer(),
                new IntegerSerializer(), new PointSerializer()).setColumnFamily("frame") //
                .setKeys((frameFrom),(frameTo)). //
                setRange(pointFrom, pointTo, false, (1000)).execute().get();
        for (Row<Integer, Integer, PointType> rows : queryResult1) {
            Integer key = rows.getKey().intValue();
            for (HColumn<Integer, PointType> row : rows.getColumnSlice().getColumns()) {
                points.put(key, row.getName(), row.getValue());
            }
        }
        map.put("points", points);
        return map;
    }

    /**
     *
     */
    @Override
    public Map getFramePointsByTime(Long timeFrom, Long timeTo, Integer pointFrom, Integer pointTo) {
        List<Integer> frames = new MetricsList("getFramePointsByTime", "frames");
        Table<Object, Object, PointType> points = HashBasedTable.create();
        HashMap map = new HashMap();
        //range Slice statement
        OrderedRows<Long, Integer, byte[]> queryResult1 = HFactory.createRangeSlicesQuery(keyspace, new LongSerializer(),
                new IntegerSerializer(), new BytesArraySerializer())
                .setColumnFamily("time").setKeys(timeFrom, timeTo) //
                .setRange(null,null, false, (1000)).execute().get();
        for (Row<Long, Integer, byte[]> rows : queryResult1) {
            Long key = rows.getKey();
            for (HColumn<Integer, byte[]> row : rows.getColumnSlice().getColumns()) {
                frames.add(row.getName().intValue());
            }
        }
        //multi Get Slice statement
        Rows<Integer, Integer, PointType> queryResult2 = 
                HFactory.createMultigetSliceQuery(keyspace, new IntegerSerializer(),
                new IntegerSerializer(), new PointSerializer()).
                setColumnFamily("frame").setKeys(frames)
                .setRange((pointFrom), 
                (pointTo), false, 1000).execute().get();
        for (Row<Integer, Integer, PointType> rows : queryResult2) {
            Integer key = rows.getKey();
            for (HColumn<Integer, PointType> row : rows.getColumnSlice().getColumns()) {
                points.put(key, row.getName(), row.getValue());
            }
        }
        map.put("points", points);
        return map;
    }

    /**
     *
     */
    @Override
    public Map getAtoms(Integer number,Integer frame,Double delta) {
        Table<Object, Object, Integer> point = HashBasedTable.create();
        throw new QueryNotImplementedException("Unsupported query");
    }
}