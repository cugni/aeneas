/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.cassandra.translator.mapping.Col;
import es.bsc.aeneas.cassandra.translator.mapping.CF;
import es.bsc.aeneas.cassandra.translator.mapping.Ks;
import es.bsc.aeneas.model.gen.ValueDest;
import es.bsc.aeneas.model.util.GenUtils;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author ccugnasc
 */
public class ColTest {

    private static final Logger LOG = Logger.getLogger(ColTest.class.getName());

    static Map<String, Col> getCols(String cModel) {
        Ks k = GenUtils.getCassandraModel(cModel);
        Map<String, Col> a = new HashMap<String, Col>(5);
        for (CF c : k.cfs) {
            for (Col cl : c.columns) {
                a.put(cl.name, cl);
            }

        }
        return a;
    }

    public ColTest() {
        GenUtils.setRefmodel("referenceModelTest");
    }

    /**
     * Test of transform method, of class Col.
     */
    @Test
    public void testTransform_3args_1() {
    }

    /**
     * Test of transform method, of class Col.
     */
    @Test
    public void testTransform_3args_2() {
        Map<String, Col> cols = getCols("test1");


        ValueDest vd = new ValueDest();
        vd.setPosition(0);

        /*
         * Getting a integer
         */
        Object t = cols.get("natoms").transform(vd, 20);
        assertEquals(t.getClass(), DynamicComposite.class);
        assertEquals(((DynamicComposite) t).get(0), 20);
        //getomg a double
        t = cols.get("natoms").transform(vd, 20.123);
        assertEquals(t.getClass(), DynamicComposite.class);
        assertEquals(((DynamicComposite) t).get(0), 20.123);
        //getting a string
        t = cols.get("natoms").transform(vd, "ciao123");
        assertEquals(t.getClass(), DynamicComposite.class);
        assertEquals(((DynamicComposite) t).get(0), "ciao123");





        t = cols.get("step").transform(vd, 20);
        assertEquals(t.getClass(), Integer.class);
        assertEquals((Integer) t, (Integer) 20);

    }

    /**
     * Test of getValueSerializer method, of class Col.
     */
    @Test
    public void testGetValueSerializer() {
    }

    /**
     * Test of getKeyTypes method, of class Col.
     */
    @Test
    public void testGetKeyTypes() {
    }

    /**
     * Test of getColumnNameTypes method, of class Col.
     */
    @Test
    public void testGetColumnNameTypes() {
    }

    /**
     * Test of getValueTypes method, of class Col.
     */
    @Test
    public void testGetValueTypes() {
    }

    /**
     * Test of isFixedRow method, of class Col.
     */
    @Test
    public void testIsFixedRow() {
    }

    /**
     * Test of getFixedRowKey method, of class Col.
     */
    @Test
    public void testGetFixedRowKey() {
    }

    /**
     * Test of getColumnDefinition method, of class Col.
     */
    @Test
    public void testGetColumnDefinition() {
        Map<String, Col> cols = getCols("test1");


        ValueDest vd = new ValueDest();
        vd.setPosition(0);
        /*
         * Getting a integer
         */
        ColumnDefinition columnDefinition = cols.get("natoms").getColumnDefinition();
        assertNull(columnDefinition.getIndexName());
        assertEquals(ComparatorType.DYNAMICCOMPOSITETYPE.getClassName(), columnDefinition.getValidationClass());

        columnDefinition = cols.get("prec").getColumnDefinition();
        assertNull(columnDefinition.getIndexName());
        assertEquals(ComparatorType.INT32TYPE.getClassName(), columnDefinition.getValidationClass());






    }

    /**
     * Test of getFixedColumnName method, of class Col.
     */
    @Test
    public void testGetFixedColumnName() {
    }
}