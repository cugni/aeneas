/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.datamodel;

import es.bsc.aeneas.datamodel.PointType;
import es.bsc.aeneas.datamodel.PointClusterer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class PointTypeTest {

    public PointTypeTest() {
    }
    Random r = new Random();

    public PointType randomP() {
        PointType p = new PointType();
        p.setX(r.nextDouble() * r.nextLong());
        p.setY(r.nextDouble() * r.nextLong());
        p.setZ(r.nextDouble() * r.nextLong());
        return p;


    }

    @Test
    public void testEquals() {
        List<PointType> points = new ArrayList<PointType>(1000);
        for (int i = 0; i < 10000; i++) {
            points.add(randomP());
        }
        List<PointType> p2 = new ArrayList<PointType>(1000);
        PointClusterer pc = new PointClusterer();
        for (int i = 0; i < 10000; i++) {
            p2.add(pc.parseFromString(points.get(i).toString()));
        }
        for (int i = 0; i < 10000; i++) {
            assertEquals(points.get(i), p2.get(i));
            assertTrue(p2.get(i).equals(points.get(i)));
            assertTrue(points.get(i).equals(p2.get(i)));

        }
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, new PointType(1.0, 1.0, 1.0).compareTo(new PointType(1.0, 1.0, 1.0)));

        assertEquals(-1, new PointType(1.0, 1.0, 1.0).compareTo(new PointType(2.0, 1.0, 1.0)));

        assertEquals(1, new PointType(2.0, 2.0, 2.0).compareTo(new PointType(1.0, 1.0, 1.0)));
        assertEquals(1, new PointType(2.0, 2.0, 2.0).compareTo(new PointType(2.0, 1.0, 1.0)));
        assertEquals(1, new PointType(2.0, 2.0, 2.0).compareTo(new PointType(2.0, 2.0, 1.0)));
        assertEquals(0, new PointType(2.0, 2.0, 2.0).compareTo(new PointType(2.0, 2.0, 2.0)));
        
        assertEquals(-1, new PointType(1.0, 1.0, 1.0).compareTo( new PointType(2.0, 2.0, 2.0)));
        assertEquals(-1, new PointType(2.0, 1.0, 1.0).compareTo( new PointType(2.0, 2.0, 2.0)));
        assertEquals(-1, new PointType(2.0, 2.0, 1.0).compareTo( new PointType(2.0, 2.0, 2.0)));
       
      


    }
}
