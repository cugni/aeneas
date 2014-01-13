/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.datamodel;

import es.bsc.aeneas.core.datamodel.MapPointType;
import es.bsc.aeneas.core.datamodel.MapPointClusterer;
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
public class MapPointTypeTest {

    public MapPointTypeTest() {
    }
    Random r = new Random();

    public MapPointType randomP() {
        MapPointType p = new MapPointType();
        p.setLat(r.nextDouble() * r.nextLong());
        p.setLon(r.nextDouble() * r.nextLong());
        return p;


    }

    @Test
    public void testEquals() {
        List<MapPointType> points = new ArrayList<MapPointType>(1000);
        for (int i = 0; i < 10000; i++) {
            points.add(randomP());
        }
        List<MapPointType> p2 = new ArrayList<MapPointType>(1000);
        MapPointClusterer pc = new MapPointClusterer();
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
        assertEquals(0, new MapPointType(1.0, 1.0).compareTo(new MapPointType(1.0, 1.0)));

        assertEquals(-1, new MapPointType(1.0, 1.0).compareTo(new MapPointType(2.0, 1.0)));

        assertEquals(1, new MapPointType(2.0, 2.0).compareTo(new MapPointType(1.0, 1.0)));
        assertEquals(1, new MapPointType(2.0, 2.0).compareTo(new MapPointType(2.0, 1.0)));
        assertEquals(0, new MapPointType(2.0, 2.0).compareTo(new MapPointType(2.0, 2.0)));

        assertEquals(-1, new MapPointType(1.0, 1.0).compareTo(new MapPointType(2.0, 2.0)));
        assertEquals(-1, new MapPointType(2.0, 1.0).compareTo(new MapPointType(2.0, 2.0)));
        assertEquals(0, new MapPointType(2.0, 2.0).compareTo(new MapPointType(2.0, 2.0)));




    }
}
