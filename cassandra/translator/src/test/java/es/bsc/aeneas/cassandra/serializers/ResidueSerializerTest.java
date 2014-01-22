/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.serializers;

 s
import es.bsc.aeneas.core.model.PointType;
import es.bsc.aeneas.core.model.ResidueType;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class ResidueSerializerTest {

    public ResidueSerializerTest() {
    }
    Random r = new Random();

    public PointType randomPoint() {
        PointType pa = new PointType();
        pa.setX(r.nextDouble());
        pa.setY(r.nextDouble());
        pa.setZ(r.nextDouble());
        return pa;
    }

    public ResidueType randomResidue() {
        ResidueType re = new ResidueType();
        for (int i = 0; i < r.nextInt(1000); i++) {
            re.addAtom(i, randomPoint());
        }
        return re;
    }

    /**
     * Test of fromByteBuffer method, of class ResidueSerializer.
     */
    @Test
    public void testRandomConvertAndRevert() {

        ResidueType res = randomResidue();
        ResidueType res2 = ser.fromByteBuffer(ser.toByteBuffer(res));
        assertEquals("", res, res2);
        assertEquals(res.getAtoms(), res2.getAtoms());
    }
    ResidueSerializer ser = new ResidueSerializer();

    @Test
    public void testRandomConvertAndRevertEmpty() {
        ResidueType res = new ResidueType();
        ResidueType res2 = ser.fromByteBuffer(ser.toByteBuffer(res));
        assertEquals("", res, res2);
        assertEquals(res.getAtoms(), res2.getAtoms());
        assertTrue(res2.getAtoms().size() == 0);
    }

    @Test
    public void testRandomConvertAndRevertOne() {
        ResidueType res = new ResidueType();
        res.addAtom(0, randomPoint());
        ResidueType res2 = ser.fromByteBuffer(ser.toByteBuffer(res));
        assertEquals("", res, res2);
        assertEquals(res.getAtoms(), res2.getAtoms());
        assertTrue(res2.getAtoms().size() == 1);


    }
}