/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.loader.examples;

import es.bsc.aeneas.loader.Loader;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.Arrays;

/**
 *
 * @author cesare
 */
public class CompositeTest {

    public static void main(String[] args) {
        Loader l = Loader.getInstance();
        Cluster cluster = HFactory.getOrCreateCluster(l.clustername, l.clusterlocation);
        String keyspaceName = "compotest";
        if (cluster.describeKeyspace(keyspaceName) != null) {
            cluster.dropKeyspace(keyspaceName);
        }
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspaceName,
                "prova",
                ComparatorType.COMPOSITETYPE);
        //cfDef.setKeyValidationClass(ComparatorType.COMPOSITETYPE.getTypeName());
        cfDef.setKeyValidationClass("CompositeType(Int32Type,Int32Type)");
        cfDef.setComparatorTypeAlias("(Int32Type, Int32Type)");
        //TODO here I should set the composite comparator type

        KeyspaceDefinition keyspace = HFactory.createKeyspaceDefinition(keyspaceName,
                ThriftKsDef.DEF_STRATEGY_CLASS,
                1, //replica factor 
                Arrays.asList(cfDef));
        //Add the schema to the cluster.
        //"true" as the second param means that Hector will block until all nodes see the change.
        cluster.addKeyspace(keyspace, true);
        Keyspace ksp = HFactory.createKeyspace(keyspaceName, cluster);


        ColumnFamilyTemplate<Composite, String> t = new ThriftColumnFamilyTemplate<Composite, String>(ksp,
                "prova",
                new CompositeSerializer(),
                StringSerializer.get());
        Composite composite = new Composite(1,2);
        composite.addComponent(1, IntegerSerializer.get());
        composite.addComponent(2, IntegerSerializer.get());

        ColumnFamilyUpdater<Composite, String> up = t.createUpdater(composite);
        up.setString("columnname", "value");
        t.update(up);
        ColumnFamilyResult<Composite, String> q = t.queryColumns(composite);
        System.out.println("Retrived " + q.getString("columnanme"));
    }
}
