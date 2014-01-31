/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.datamodel;

import com.google.common.collect.TreeMultimap;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class DoubleClustererTest {

    private DoubleClusterer ds;
    private TreeMultimap<Double, Double> ms;
    private int ints;
    private int k;
    private ArrayList<Double> points;

    
    @Before
    public void DoubleClustererInit() {
        Double from = 1.0;
        Double to = 100.0;
        ints = 1000;
        ds = new DoubleClusterer();
        ds.setGrouping(from, to, ints);
        ms = TreeMultimap.create();
        k = 100;
        points = new ArrayList<Double>(ints * k);
        for (int i = 0; i < ints * k; i++) {
            points.add(rangeRand(from, to));
        }
    }

    @Test
    public void testZero() {
        Double from = 0.0;
        Double to = 1.0;
        int in = 1000;
        DoubleClusterer p = new DoubleClusterer();
        p.setGrouping(from, to, in);
        Double p1 = p.parseFromString("1.124289515171076E-4");
        Double p2 = p.parseFromString("1.2370157642292858E-4");
        Double g1 = p.getGroup(p1);
        Double g2 = p.getGroup(p2);
        assertEquals(g1, g2);
    }

    @Test
    public void testGetGroupBig() {
        Double from = 1.0;
        Double to = 10000.0;
        int intervals = 10;
        DoubleClusterer ps = new DoubleClusterer();
        ps.setGrouping(from, to, intervals);
        Double t1 = 2.0;
        assertEquals(ps.getGroup(from), ps.getGroup(t1));
        Double t2 = 1100.0;
        Double mod = ps.getGroup(from);
        mod += 1.0;
        assertEquals(mod, ps.getGroup(t2));


    }

    @Test
    public void testGetGroupFract() {
        Double from = 0.0001;
        Double to = 1.0;
        int intervals = 10;
        DoubleClusterer ps = new DoubleClusterer();
        ps.setGrouping(from, to, intervals);
        Double t1 = 0.0002;
        assertEquals(ps.getGroup(from), ps.getGroup(t1));
        Double t2 = 0.1009;
        Double mod = ps.getGroup(from);
        mod += 1.0;
        assertEquals(mod, ps.getGroup(t2));
    }

    @Test
    public void testRange() {
        Double from = 0.0001;
        Double to = 1.0;
        int intervals = 10;
        DoubleClusterer ps = new DoubleClusterer();
        ps.setGrouping(from, to, intervals);
        from = 0.001;
        to = 0.002;
        List<Double> r = ps.getGroupsInterval(from, to);
        assertEquals(1, r.size());
        from = 0.001;
        to = 0.11;
        r = ps.getGroupsInterval(from, to);
        assertEquals(2, r.size());
        Double f = rangeRand(from, to);
        Double t = rangeRand(from, to);
        Double gf = ps.getGroup(f);
        Double gt = ps.getGroup(t);
        int num = (int) Math.round(Math.abs(gt -gf)) + 1;
        assertEquals(num, ps.getGroupsInterval(f, t).size());

    }

    //@Test
    public void testDistrib() {

        assertEquals("Wrong distribution", (double) ints, (double) ms.keySet().size(), ints * 0.3);
        int sum = 0;
        for (Double p : ms.keySet()) {
            assertEquals((double) k, (double) ms.get(p).size(), k * 0.5);
            sum += ms.get(p).size();
        }
        assertEquals(points.size(), sum);
    }

    
    Random r = new Random();

    private SortedSet<Double> limit(Double from, Double to, Collection<Double> col) {
        TreeSet<Double> ret = new TreeSet();
        for (Double p : col) {
            if (p>= from && p<= to) {
                ret.add(p);
            }
        }
        return ret;
    }

    public double rangeRand(double from, double to) {
        return (to - from) * r.nextDouble() + from;
    }

    @Test
    public void testGetGroupsInterval() {
    }

    @Test
    public void testParseFromString() {
    }
}
