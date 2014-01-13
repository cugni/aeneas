/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader.examples;

import es.bsc.aeneas.core.loader.Loader;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

import java.util.Arrays;

/**
 *
 * @author cesare
 */
public class DynamicBugFInd {

    public static void main(String[] args) {
        Loader l = Loader.getInstance();
        Cluster cluster = HFactory.getOrCreateCluster(l.clustername, l.clusterlocation);
        String keyspaceName = "testspace";
        Keyspace ksp = HFactory.createKeyspace(keyspaceName, cluster);
        ColumnFamilyTemplate<Integer, byte[]> t =
                new ThriftColumnFamilyTemplate<Integer, byte[]>(ksp,
                "frame",
                IntegerSerializer.get(),
                BytesArraySerializer.get());
        byte[] step = DynamicCompositeSerializer.get().toBytes(new DynamicComposite("step"));
        System.out.println("serialized value " + Arrays.toString(step));
        HColumn<byte[], byte[]> s = t.querySingleColumn(0, step, BytesArraySerializer.get());
        QueryResult<HColumn<byte[], byte[]>> ex = HFactory.createColumnQuery(ksp,IntegerSerializer.get(), BytesArraySerializer.get(), BytesArraySerializer.get())
                                                               
                                                               .setColumnFamily("frame")
                                                               .setKey(0)
                                                               .setName(step)
                                                               .execute();
      System.out.println("column name secondo modo" + Arrays.toString(   ex.get().getName()));
     
        ColumnFamilyResult<Integer, byte[]> queryColumns = t.queryColumns(0);
        if (s != null) {
            System.out.println("column name" + Arrays.toString(s.getName()));
            System.out.println("column value" + Arrays.toString(s.getValue()));
        } else {
            System.out.println("query nulla");
        }
        for (byte[] b : queryColumns.getColumnNames()) {
            System.out.println(Arrays.toString(b));
        }
    }
}
