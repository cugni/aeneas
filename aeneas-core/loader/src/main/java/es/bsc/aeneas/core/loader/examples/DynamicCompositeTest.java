/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader.examples;

import es.bsc.aeneas.cassandra.translator.Loader;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cesare
 */
public class DynamicCompositeTest {

    public static void main(String[] args) throws InterruptedException {
        Loader l = Loader.getInstance();
        Cluster cluster = HFactory.getOrCreateCluster(l.clustername, l.clusterlocation);
        String keyspaceName = "testspace";
        if (cluster.describeKeyspace(keyspaceName) != null) {
            cluster.dropKeyspace(keyspaceName);
        }
        DynamicComposite composite = new DynamicComposite();
        Map<Class<? extends Serializer>, String> serializerToComparatorMapping = new HashMap<Class<? extends Serializer>, String> (
                composite.getSerializerToComparatorMapping());
               serializerToComparatorMapping.put(DoubleSerializer.class, "DoubleType");
               composite.setSerializerToComparatorMapping(serializerToComparatorMapping);
        Map<Byte, String> aliasesToComparatorMapping = new HashMap<Byte,String>(composite.getAliasesToComparatorMapping());
        aliasesToComparatorMapping.put((byte) 'd', "DoubleType");
        composite.setAliasesToComparatorMapping(aliasesToComparatorMapping);
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspaceName,
                "prova",
                ComparatorType.DYNAMICCOMPOSITETYPE);
        cfDef.setKeyValidationClass(ComparatorType.UTF8TYPE.getTypeName());
        String alias = DynamicComposite.DEFAULT_DYNAMIC_COMPOSITE_ALIASES.substring(0, DynamicComposite.DEFAULT_DYNAMIC_COMPOSITE_ALIASES.length() - 1).concat(",d=>DoubleType)");

        cfDef.setComparatorTypeAlias(alias);
        // cfDef.setComparatorTypeAlias("(Int32Type, Int32Type)");
        //TODO here I should set the composite comparator type

        KeyspaceDefinition keyspace = HFactory.createKeyspaceDefinition(keyspaceName,
                ThriftKsDef.DEF_STRATEGY_CLASS,
                1, //replica factor 
                Arrays.asList(cfDef));
        //Add the schema to the cluster.
        //"true" as the second param means that Hector will block until all nodes see the change.
        cluster.addKeyspace(keyspace, true);
        Keyspace ksp = HFactory.createKeyspace(keyspaceName, cluster);


        ColumnFamilyTemplate<String, DynamicComposite> t = new ThriftColumnFamilyTemplate<String, DynamicComposite>(ksp,
                "prova",
                StringSerializer.get(),
                new DynamicCompositeSerializer());
        
        composite.addComponent("aa", StringSerializer.get());
        composite.addComponent(2, IntegerSerializer.get());
        composite.addComponent(5.5, DoubleSerializer.get());

        ColumnFamilyUpdater<String, DynamicComposite> up = t.createUpdater("prova2");
        up.setString(composite, "value");
        t.update(up);
        Thread.currentThread().sleep(1000);
        ColumnFamilyResult<String, DynamicComposite> q = t.queryColumns("prova2");
        System.out.println("Retrived " + q.getString(composite));
    }
}
