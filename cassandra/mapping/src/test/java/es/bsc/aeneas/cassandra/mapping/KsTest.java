/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.cassandra.mapping.Ks;
import es.bsc.aeneas.model.gen.StrategyType;
import es.bsc.aeneas.model.util.GenUtils;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.hector.api.ddl.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class KsTest {

    public KsTest() {
        GenUtils.setRefmodel("referenceModelTest");
    }

    /**
     * Test of createKeyspaceDefinition method, of class Ks.
     */
    @Test
    public void testCreateKeyspaceDefinitionTest1() {
        Ks k = GenUtils.getCassandraModel("test1");
        KeyspaceDefinition ks = k.createKeyspaceDefinition();
        assertEquals("testspace", k.name);
        assertEquals(1, k.getReplicationFactor());
        assertEquals(StrategyType.SIMPLE_STRATEGY, k.getStrategy());
        for (ColumnFamilyDefinition cds : ks.getCfDefs()) {
            assertEquals("testspace", cds.getKeyspaceName());
            assertEquals(ColumnType.STANDARD, cds.getColumnType());
            assertNotNull(cds.getComparatorType());
            assertNotNull(cds.getName());
            if (cds.getName().equals("frame")) {
                assertEquals(ComparatorType.INT32TYPE, cds.getComparatorType());
                List<ColumnDefinition> cm = cds.getColumnMetadata();
                assertTrue(cm.isEmpty());

            } else if (cds.getName().equals("framemetadata")) {
                assertEquals(ComparatorType.UTF8TYPE, cds.getComparatorType());

                List<ColumnDefinition> cm = cds.getColumnMetadata();
                assertEquals(4, cm.size());
                Iterator<ColumnDefinition> it = cm.iterator();

                ColumnDefinition cd = it.next();
                assertEquals(ByteBuffer.wrap("step".getBytes()), cd.getName());
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.INT32TYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());


                cd = it.next();
                assertEquals(ByteBuffer.wrap("box".getBytes()), cd.getName());
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.BYTESTYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());

                cd = it.next();
                assertEquals(ByteBuffer.wrap("natoms".getBytes()), cd.getName());
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.DYNAMICCOMPOSITETYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());


                cd = it.next();
                assertEquals(ByteBuffer.wrap("prec".getBytes()), cd.getName());
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.INT32TYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());


            } else if (cds.getName().equals("time")) {
                assertEquals(ComparatorType.INT32TYPE, cds.getComparatorType());

                List<ColumnDefinition> cm = cds.getColumnMetadata();
                assertEquals(0, cm.size());

            } else {
                fail("Unknown column family");
            }
        }


    }

    @Test
    public void testCreateKeyspaceDefinitionTest2() {
        Ks k = GenUtils.getCassandraModel("test2");
        KeyspaceDefinition ks = k.createKeyspaceDefinition();
        assertEquals("testspace", k.name);
        assertEquals(1, k.getReplicationFactor());
        assertEquals(StrategyType.SIMPLE_STRATEGY, k.getStrategy());
        for (ColumnFamilyDefinition cds : ks.getCfDefs()) {
            assertEquals("testspace", cds.getKeyspaceName());
            assertEquals(ColumnType.STANDARD, cds.getColumnType());
            assertNotNull(cds.getComparatorType());
            assertNotNull(cds.getName());
            if (cds.getName().equals("frame")) {
                assertEquals(ComparatorType.DYNAMICCOMPOSITETYPE, cds.getComparatorType());
                List<ColumnDefinition> cm = cds.getColumnMetadata();
                assertEquals(4, cm.size());
                Iterator<ColumnDefinition> it = cm.iterator();

                ColumnDefinition cd = it.next();
                assertEquals("step", DynamicCompositeSerializer.get().fromByteBuffer(cd.getName()).get(0));
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.INT32TYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());


                cd = it.next();
                assertEquals("box", DynamicCompositeSerializer.get().fromByteBuffer(cd.getName()).get(0));
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.BYTESTYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());

                cd = it.next();
                assertEquals("natoms", DynamicCompositeSerializer.get().fromByteBuffer(cd.getName()).get(0));
                assertNull(cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.DYNAMICCOMPOSITETYPE.getClassName(), cd.getValidationClass());
                assertNull(cd.getIndexName());


                cd = it.next();
                assertEquals("prec", DynamicCompositeSerializer.get().fromByteBuffer(cd.getName()).get(0));
                assertNotNull(cd.getIndexType());
                assertEquals(ColumnIndexType.KEYS, cd.getIndexType());
                assertNotNull(cd.getValidationClass());
                assertEquals(ComparatorType.INT32TYPE.getClassName(), cd.getValidationClass());
                assertNotNull(cd.getIndexName());
                assertEquals("prec", cd.getIndexName());



            } else if (cds.getName().equals("time")) {
                assertEquals(ComparatorType.INT32TYPE, cds.getComparatorType());
                List<ColumnDefinition> cm = cds.getColumnMetadata();
                assertEquals(0, cm.size());

            } else {
                fail("Unknown column family");
            }
        }


    }
}