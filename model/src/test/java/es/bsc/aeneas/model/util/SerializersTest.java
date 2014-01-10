/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.model.util;

import es.bsc.aeneas.cassandra.serializers.Serializers;
import es.bsc.aeneas.model.gen.StandardType;
import es.bsc.aeneas.model.gen.Type;
import es.bsc.aeneas.cassandra.serializers.BoxSerializer;
import es.bsc.aeneas.model.marshalling.BoxType;
import es.bsc.aeneas.cassandra.serializers.PointSerializer;
import es.bsc.aeneas.model.marshalling.PointType;
import me.prettyprint.cassandra.serializers.*;
import me.prettyprint.hector.api.beans.DynamicComposite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 *
 * @author cesare
 */
public class SerializersTest {

    public SerializersTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetSerializer_Type() {
        Type t = new Type();
        t.setStandardType(StandardType.ANY_TYPE);
        assertEquals(DynamicCompositeSerializer.get().getClass(), Serializers.getSerializer(t).getClass());
        assertTrue(DynamicCompositeSerializer.get().getClass() == Serializers.getSerializer(t).getClass());

        t = new Type();
        t.setStandardType(StandardType.UTF_8_TYPE);
        assertEquals(StringSerializer.get().getClass(), Serializers.getSerializer(t).getClass());
        assertTrue(StringSerializer.get().getClass() == Serializers.getSerializer(t).getClass());

    }

    @Test
    public void testGetSerializer_SuperType() {
    }

    @Test
    public void testGetSerializer_StandardType() {
          
     
        assertEquals(DynamicCompositeSerializer.get().getClass(), Serializers.getSerializer(StandardType.ANY_TYPE).getClass());
        assertTrue(DynamicCompositeSerializer.get().getClass() == Serializers.getSerializer(StandardType.ANY_TYPE).getClass());

       
        assertEquals(StringSerializer.get().getClass(), Serializers.getSerializer(StandardType.UTF_8_TYPE).getClass());
        assertTrue(StringSerializer.get().getClass() == Serializers.getSerializer(StandardType.UTF_8_TYPE).getClass());
    }

    @Test
    public void testToString() {
        Logger.getLogger(this.getClass().getSimpleName()).info("Testing Serializers.toString");
        assertEquals("StringType",
                Serializers.toString(StringSerializer.get()));
        assertEquals("IntegerType",
                Serializers.toString(IntegerSerializer.get()));
        assertEquals("BoxType",
                Serializers.toString(BoxSerializer.get()));
        assertEquals("PointType",
                Serializers.toString(PointSerializer.get()));
    }

    @Test
    public void tesGetSerializer() {
        Integer i = 1;
        assertEquals(IntegerSerializer.get().getClass(), Serializers.getSerializer(i).getClass());
        assertTrue(IntegerSerializer.get().getClass() == Serializers.getSerializer(i).getClass());
        String s = "prova";
        assertEquals(StringSerializer.get().getClass(), Serializers.getSerializer(s).getClass());

        assertTrue(StringSerializer.get() == Serializers.getSerializer(s));
        PointType pt = new PointType(1.0, 2.0, 3.0);
        assertEquals(PointSerializer.class, Serializers.getSerializer(pt).getClass());
        assertTrue(PointSerializer.get() == Serializers.getSerializer(pt));
        BoxType b = new BoxType();
        assertEquals(BoxSerializer.class, Serializers.getSerializer(b).getClass());
        assertTrue(BoxSerializer.get() == Serializers.getSerializer(b));
        TestClass1 tc = new TestClass1();
        try {
            assertEquals(ObjectSerializer.class, Serializers.getSerializer(tc).getClass());
            fail("Not thrown exception");
        } catch (Exception ial) {
        }

        TestClass2 t2 = new TestClass2();
        assertEquals(ObjectSerializer.get().getClass(), Serializers.getSerializer(t2).getClass());
        assertTrue(ObjectSerializer.get() == Serializers.getSerializer(t2));
        assertEquals(DoubleSerializer.class, Serializers.getSerializer(2.0).getClass());

        DynamicComposite c = new DynamicComposite();
        assertEquals(DynamicCompositeSerializer.get().getClass(), Serializers.getSerializer(c).getClass());
        assertTrue(DynamicCompositeSerializer.get().getClass() == Serializers.getSerializer(c).getClass());
    }

    private class TestClass1 {

        String name = "";
        Integer val = 1;
    }

    private class TestClass2 implements Serializable {

        String name = "";
        Integer val = 1;
    }
}
