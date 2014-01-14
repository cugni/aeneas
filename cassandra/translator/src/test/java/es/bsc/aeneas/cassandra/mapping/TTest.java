package es.bsc.aeneas.cassandra.mapping;


import es.bsc.aeneas.cassandra.translator.mapping.Tr;
import es.bsc.aeneas.model.gen.*;
import es.bsc.aeneas.model.marshalling.PointType;
import org.junit.*;

import java.math.BigInteger;

public class TTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTransformSimple() {
        System.out.println("transform");
        Object o = Integer.valueOf(2);
        Dest d = new KeyDest();
        d.setPosition(0);
        Type t = new Type();
        t.setStandardType(StandardType.INT_32_TYPE);
        Tr tra = new Tr(d, t);

        Object expResult = Integer.valueOf(2);
        Object result = tra.transform(o);
        Assert.assertEquals(expResult, result);
        Assert.assertEquals(tra.valueClass, Integer.class);
    }

    @Test
    public void testTransformComplextr() {
        System.out.println("transform");
        PointType o = new PointType();
        o.setX(Double.valueOf(1.0D));
        o.setY(Double.valueOf(2.2D));
        o.setZ(Double.valueOf(3.9D));
        Dest d = new KeyDest();
        d.setPosition(0);
        Type t = new Type();
        t.setCustomType("es.bsc.aeneas.model.marshalling.PointType");
        Tr tra = new Tr(d, t);
        Object result = tra.transform(o);
        Assert.assertEquals(o, result);
        Assert.assertEquals(PointType.class, tra.valueClass);
    }

    @Test
    public void testTransformComplexAtt() {
        System.out.println("transform");
        PointType o = new PointType();
        o.setX(Double.valueOf(1.0D));
        o.setY(Double.valueOf(2.2D));
        o.setZ(Double.valueOf(3.9D));
        Dest d = new KeyDest();
        d.setAttr("getX");
        d.setPosition(0);
        Type t = new Type();
        t.setCustomType("es.bsc.aeneas.model.marshalling.PointType");
        Tr tra = new Tr(d, t);
        Object result = tra.transform(o);
        Assert.assertEquals(o.getX(), result);
        Assert.assertEquals(Double.class, tra.valueClass);
    }

    @Test
    public void testTransformDoubleClusterer() {
        System.out.println("transform");
        Double o = Double.valueOf(0.5D);
        Dest d = new KeyDest();
        ClustererType ct = new ClustererType();
        ct.setFrom("0.1");
        ct.setTo("1.0");
        ct.setIntervals(BigInteger.valueOf(10L));
        d.setClusterer(ct);
        d.setPosition(0);
        Type t = new Type();
        t.setStandardType(StandardType.DOUBLE_TYPE);
        Tr tra = new Tr(d, t);
        Object result = tra.transform(o);
        Assert.assertEquals(Double.valueOf(5.0D), result);
        Assert.assertEquals(Double.class, tra.valueClass);
    }

    @Test
    public void testTransformPointAttrClusterer() {
        System.out.println("transform");
        PointType o = new PointType(Double.valueOf(0.5D), Double.valueOf(10.0D), Double.valueOf(20.0D));
        Dest d = new KeyDest();
        d.setAttr("getX");
        ClustererType ct = new ClustererType();
        ct.setFrom("0.1");
        ct.setTo("1.0");
        ct.setIntervals(BigInteger.valueOf(10L));
        d.setClusterer(ct);
        d.setPosition(0);
        Type t = new Type();
        t.setCustomType("es.bsc.aeneas.model.marshalling.PointType");
        Tr tra = new Tr(d, t);
        Object result = tra.transform(o);
        Assert.assertEquals(Double.valueOf(5.0D), result);
        Assert.assertEquals(Double.class, tra.valueClass);
    }

    @Test
    public void testTransformComplexAttException() {
        System.out.println("transform");
        PointType o = new PointType();
        o.setX(Double.valueOf(1.0D));
        o.setY(Double.valueOf(2.2D));
        o.setZ(Double.valueOf(3.9D));
        Dest d = new KeyDest();
        d.setAttr("wrongName");
        d.setPosition(0);
        Type t = new Type();
        t.setCustomType("es.bsc.aeneas.model.marshalling.PointType");
        try {
            Tr tra = new Tr(d, t);
            Assert.fail("Expected and exception");
        } catch (Exception e) {
        }
    }
}