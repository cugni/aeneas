/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.model.util;

import es.bsc.cassandrabm.model.Ks;
import es.bsc.cassandrabm.model.gen.*;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.factory.HFactory;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 *
 * @author cesare
 */
public class GenUtilsTest {

    private static final Logger LOG = Logger.getLogger(GenUtilsTest.class.getName());

    public GenUtilsTest() {
        GenUtils.setRefmodel("referenceModelTest");
    }

    @Test
    public void testCastObject() {
    }

    @Test
    public void testGetComparator() {
    }

    @Test
    public void testGetLevelName() {
    }

    @Test
    public void testGetColumnName() {
        List<ColumnDefinition> listCD = new ArrayList<ColumnDefinition>(4);
        BasicColumnDefinition step = new BasicColumnDefinition();
        step.setName(StringSerializer.get().toByteBuffer("step"));
        step.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(step);
        BasicColumnDefinition box = new BasicColumnDefinition();
        box.setName(StringSerializer.get().toByteBuffer("box"));
        box.setValidationClass(ComparatorType.BYTESTYPE.getClassName());
        listCD.add(box);
        BasicColumnDefinition prec = new BasicColumnDefinition();
        box.setName(StringSerializer.get().toByteBuffer("prec"));
        box.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(prec);
        BasicColumnDefinition natoms = new BasicColumnDefinition();
        box.setName(StringSerializer.get().toByteBuffer("natoms"));
        box.setValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        listCD.add(natoms);
        ColumnFamilyDefinition frame = HFactory.createColumnFamilyDefinition("testspace",
                "frame",
                ComparatorType.BYTESTYPE,
                listCD);
        frame.setKeyCacheSavePeriodInSeconds(60);
        frame.setKeyCacheSize(200.0);
        frame.setRowCacheSavePeriodInSeconds(120);
        frame.setRowCacheSize(100.0);
        frame.setKeyValidationClass(ComparatorType.INTEGERTYPE.getClassName());
        ColumnFamilyDefinition time = HFactory.createColumnFamilyDefinition("testspace",
                "time",
                ComparatorType.INTEGERTYPE);
        time.setKeyCacheSavePeriodInSeconds(30);
        time.setKeyCacheSize(50.0);
        time.setRowCacheSavePeriodInSeconds(45);
        time.setRowCacheSize(0.75);
        time.setKeyValidationClass(ComparatorType.LONGTYPE.getClassName());


        List<LevelType> levs = new ArrayList<LevelType>(3);
        LevelType lt = new LevelType();
        lt.setValue("frame");
        KeyDest kd = new KeyDest();
        kd.setPosition(0);
        lt.getKeyDest().add(kd);
        levs.add(lt);
        LevelType lt2 = new LevelType();
        lt2.setValue("natoms");
        levs.add(lt2);

        //PathMatch pm=new PathMatch(frame, point,lt,null);

        //GenUtils.getColumnName(this, new Object[]{1,"points",2}, pm);
    }

    @Test
    public void testGetKey() {
        /*
         * ColumnFamilyType columnFamily =new columnFamilyType(); ColumnType
         * column=new ColumnType();
         *
         * List<LevelType> levels=new ArrayList<LevelType>();
         * ColumnFamilyTemplate<Object, Object> template=new
         * ColumnFamilyTemplate<Object, Object> template PathMatch pm=new
         * PatchMatch(); GenUtils.getKey(path, null)
         *
         */
    }

    @Test
    public void testGetQueryImplementationFile() throws IOException {
        URL queryImplementationFile = GenUtils.getQueryImplementationFile("test");
        
        assertTrue(queryImplementationFile.openStream().available()>0);

    }

    public void testGetCassandraModelFile() throws IOException {
        URL cassandraModelFile = GenUtils.getCassandraModelFile("test1");
        assertTrue(cassandraModelFile.openStream().available()>0);
    }

    @Test
    public void testParseCassandraModel() {
        Ks k = GenUtils.getCassandraModel("test1");
        assertNotNull(k);
        assertEquals("testspace", k.name);
        //others possible..
    }

    @Test
    public void testGetQueryModel() {
        ModelType m = GenUtils.getQueryModel();
        assertNotNull(m);
        assertTrue(m.getQueries().size() > 0);

    }

    @Test
    public void testGetQueryImplementation() {
        Model q = GenUtils.getQueryImplementation("test");
        assertNotNull(q);
        assertTrue(q.getQuery().size() > 0);
    }

    @Test
    public void testGetReferenceModel() {
        RootType r = GenUtils.getReferenceModel();
        assertNotNull(r);
        assertTrue(r.getEntity().size() > 0);
    }

    @Test
    public void testCompareTypes() {



        Type t1;
        Type t2;
        t1 = new Type();
        t2 = new Type();
        try {
            GenUtils.compareTypes(t1, t2);
            fail("Not raised exception");
        } catch (Exception e) {
        }
        t1 = new Type();
        t2 = new Type();
        t1.setStandardType(StandardType.LONG_TYPE);
        t2.setStandardType(StandardType.LONG_TYPE);
        assertTrue(GenUtils.compareTypes(t1, t2));
        t2.setStandardType(StandardType.INT_32_TYPE);
        assertFalse(GenUtils.compareTypes(t1, t2));
        t1 = new Type();
        t2 = new Type();
        t1.setCustomType("tipo");
        t2.setStandardType(StandardType.LONG_TYPE);
        assertFalse(GenUtils.compareTypes(t1, t2));

        t1 = new Type();
        t2 = new Type();
        t1.setCustomType("tipo");
        t2.setCustomType("tipo");
        assertTrue(GenUtils.compareTypes(t1, t2));

        t1 = new Type();
        t2 = new Type();
        t1.setCustomType("false");
        t2.setCustomType("tipo");
        assertFalse(GenUtils.compareTypes(t1, t2));


    }
}
