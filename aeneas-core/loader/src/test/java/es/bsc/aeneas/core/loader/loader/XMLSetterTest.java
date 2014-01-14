package es.bsc.aeneas.core.loader.loader;

import es.bsc.aeneas.cassandra.translator.Loader;
import es.bsc.aeneas.cassandra.translator.XMLCassandraSetter;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class XMLSetterTest {

    private static final Logger logger = Logger.getLogger(XMLSetterTest.class.getCanonicalName());
    private static final String clustername = Loader.getInstance().clustername;
    private static final String clusterlocation = Loader.getInstance().clusterlocation;

    @Before
    public void setConfig() {
        String file = this.getClass().getClassLoader().getResource("referenceModel.xml").getFile();
        System.setProperty("confdir", new File(file).getParent() + "/");
    }

    @Test
    public void testGetKeyspaceDefinition() {

        List<ColumnDefinition> listCD = new ArrayList<ColumnDefinition>(4);
        BasicColumnDefinition step = new BasicColumnDefinition();
        step.setName(new DynamicComposite("step").serialize());
        step.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(step);
        BasicColumnDefinition box = new BasicColumnDefinition();
        box.setName(new DynamicComposite("box").serialize());
        box.setValidationClass(ComparatorType.BYTESTYPE.getClassName());
        listCD.add(box);
        BasicColumnDefinition prec = new BasicColumnDefinition();
        prec.setName(new DynamicComposite("prec").serialize());
        prec.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(prec);
        BasicColumnDefinition natoms = new BasicColumnDefinition();
        natoms.setName(new DynamicComposite("natoms").serialize());
        natoms.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(natoms);

        ColumnFamilyDefinition framemeta = HFactory.createColumnFamilyDefinition("testspace",
                "framemetadata",
                ComparatorType.UTF8TYPE,
                listCD);

        framemeta.setKeyValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        ColumnFamilyDefinition frame = HFactory.createColumnFamilyDefinition("testspace",
                "frame",
                ComparatorType.INTEGERTYPE,
                new ArrayList<ColumnDefinition>(0));
        frame.setKeyValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        frame.setKeyCacheSize(20000.0);
        frame.setKeyCacheSavePeriodInSeconds(60);
        frame.setRowCacheSize(100.0);
        frame.setRowCacheSavePeriodInSeconds(120);
        ColumnFamilyDefinition time = HFactory.createColumnFamilyDefinition("testspace",
                "time",
                ComparatorType.LONGTYPE);
        time.setKeyCacheSize(50.0);
        time.setKeyCacheSavePeriodInSeconds(30);
        time.setRowCacheSize(0.75);
        time.setRowCacheSavePeriodInSeconds(45);
        time.setKeyValidationClass(ComparatorType.LONGTYPE.getClassName());
        KeyspaceDefinition test = HFactory.createKeyspaceDefinition("testspace",
                ThriftKsDef.DEF_STRATEGY_CLASS,
                1, //replica factor 
                Arrays.asList(frame, framemeta, time));

        XMLCassandraSetter xs = new XMLCassandraSetter("test");
        KeyspaceDefinition gen = xs.getKeyspaceDefinition();
        assertNotNull(gen);
        compareKeyspace(test, gen);



    }

    @Test
    public void testOpen() throws UnreachableClusterException {
        XMLCassandraSetter xs = new XMLCassandraSetter("test");
        xs.open(clustername, clusterlocation);
    }
    //@Test

    public void testConfigure() throws UnreachableClusterException {
        XMLCassandraSetter xs = new XMLCassandraSetter("test");
        xs.open(clustername, clusterlocation);
        xs.configure( );
    }

    private void compareKeyspace(KeyspaceDefinition test, KeyspaceDefinition gen) {
        logger.log(Level.INFO, "Comparing keyspace {0}", test.getName());

        assertEquals("Name", test.getName(), gen.getName());
        assertEquals("Replication Factor", test.getReplicationFactor(), gen.getReplicationFactor());
        assertEquals("Strategy class", test.getStrategyClass(), gen.getStrategyClass());
        assertArrayEquals("Stategy options Key set", test.getStrategyOptions().keySet().toArray(),
                gen.getStrategyOptions().keySet().toArray());
        assertArrayEquals("Stategy options values", test.getStrategyOptions().values().toArray(),
                test.getStrategyOptions().values().toArray());
        assertEquals("Column family definition size", test.getCfDefs().size(), gen.getCfDefs().size());
        Comparator<ColumnFamilyDefinition> ord = new Comparator<ColumnFamilyDefinition>() {
            @Override
            public int compare(ColumnFamilyDefinition o1, ColumnFamilyDefinition o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        List<ColumnFamilyDefinition> gencfds = new ArrayList(gen.getCfDefs());
        List<ColumnFamilyDefinition> testcfds = new ArrayList(test.getCfDefs());
        Collections.sort(gencfds, ord);
        Collections.sort(testcfds, ord);

        for (int i = 0; i < gencfds.size(); i++) {

            compareColumnFamily(testcfds.get(i), gencfds.get(i));
        }

    }

    private void compareColumnFamily(
            ColumnFamilyDefinition test,
            ColumnFamilyDefinition gen) {
        logger.log(Level.INFO, "Comparing Column Family {0}", test.getName());
        assertEquals("Column type", test.getColumnType(), gen.getColumnType());
        assertEquals("Comparator type", test.getComparatorType().getClass(), gen.getComparatorType().getClass());
        assertEquals("Alias type", test.getKeyAlias(), gen.getKeyAlias());
        assertEquals("Column family name", test.getName(), gen.getName());
        assertEquals("Key validation class", test.getKeyValidationClass().replaceFirst("\\(.+", "").replaceAll("[A-z]+\\.", ""), gen.getKeyValidationClass().replaceFirst("\\(.+", "").replaceAll("[A-z]+\\.", ""));
        assertEquals("Column metadata size", test.getColumnMetadata().size(), gen.getColumnMetadata().size());
//        assertEquals("Key cache size", test.getKeyCacheSize(), gen.getKeyCacheSize(), 0.0);
//        assertEquals("Key cache period in second", test.getKeyCacheSavePeriodInSeconds(), gen.getKeyCacheSavePeriodInSeconds());
//        assertEquals("Row cache size", test.getRowCacheSize(), gen.getRowCacheSize(), 0.0);
//        assertEquals("Row cache period in second", test.getRowCacheSavePeriodInSeconds(), gen.getRowCacheSavePeriodInSeconds());


        HashMap<ByteBuffer, ColumnDefinition> meta = new HashMap<ByteBuffer, ColumnDefinition>();

    }

    private void compareColumnMetadata(ColumnDefinition test,
            ColumnDefinition gen) {
        logger.log(Level.INFO, "Comparing Column Metadata {0}", test.getName());
        assertEquals("Column meta: index name", test.getIndexName(), gen.getIndexName());
        assertEquals("Column meta: index type", test.getIndexType(), gen.getIndexType());
        assertEquals("Column meta: meta name", test.getName(), gen.getName());
        assertTrue("Column meta: validation class", test.getValidationClass().contains(gen.getValidationClass()));


    }
}
