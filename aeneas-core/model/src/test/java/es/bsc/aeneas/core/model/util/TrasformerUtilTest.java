/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.model.util;

import es.bsc.aeneas.core.model.gen.ClustererType;
import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.gen.TransformType;
import es.bsc.aeneas.core.model.clusterer.IntegerClusterer;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class TrasformerUtilTest {

    public TrasformerUtilTest() {
    }

    @Test
    public void testTransform() {
        System.out.println("transform");
        DestType dt = new DestType() {
        };


        TransformType tt = new TransformType();
        tt.setClassName(Integer.class.getCanonicalName());
        tt.setMethodName("parseInt");
        dt.getTransform().add(tt);

        Object o = "26";
        TrasformerUtil instance = new TrasformerUtil();
        Object expResult = 26;
        Object result = instance.transform(dt, o);
        assertEquals(expResult, result);

    }

    @Test
    public void testTransformChain() {
        System.out.println("transform");
        DestType dt = new DestType() {
        };
        TransformType tt = new TransformType();
        tt.setClassName(Integer.class.getCanonicalName());
        tt.setMethodName("parseInt");
        dt.getTransform().add(tt);
        ClustererType ct = new ClustererType();
        ct.setClassName(IntegerClusterer.class.getCanonicalName());
        ct.setMethodName("getGroup");
        ct.setFrom("0");
        ct.setTo("30");
        ct.setIntervals(BigInteger.valueOf(3));
        dt.getTransform().add(ct);
        Object o = "26";
        TrasformerUtil instance = new TrasformerUtil();
        Object expResult = 16;
        Object result = instance.transform(dt, o);
        assertEquals(expResult, result);

        o = "28";
        instance = new TrasformerUtil();
        expResult = 16;
        result = instance.transform(dt, o);
        assertEquals(expResult, result);
        
         o = "24";
        instance = new TrasformerUtil();
        expResult = 16;
        result = instance.transform(dt, o);
        assertEquals(expResult, result);

    }
}