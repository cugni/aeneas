/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.model.marshalling;

import com.google.common.collect.TreeMultimap;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class PointClustererTest {

    private PointClusterer ps;
    private TreeMultimap<PointType, PointType> ms;
    private int intervals;
    private int k;
    private ArrayList<PointType> points;

    @Before
    public void PointClustererInit() {
//        PointType from = new PointType(0.0001, 0.0001, 0.0001);
//        PointType to = new PointType(1.0, 1.0, 1.0);
         PointType from = new PointType(1.0, 1.0, 1.0);
        PointType to = new PointType(100.0, 100.0, 100.0);
        intervals = 1000;
        ps = new PointClusterer();
        ps.setGrouping(from, to, intervals);
        ms = TreeMultimap.create();
        k = 100;
        points = new ArrayList<PointType>(intervals * k);
        for (int i = 0; i < intervals * k; i++) {
            PointType p = randomP(from, to);
            ms.put(ps.getGroup(p), p);
            points.add(p);
        }
    }

    @Test
    public void testSetGrouping() {
        PointType from = new PointType(0.0001, 0.0001, 0.0001);
        PointType to = new PointType(1.0, 1.0, 1.0);
        int intervals = 1000;
        PointClusterer ps = new PointClusterer();
        ps.setGrouping(from, to, intervals);
        PointType p1 = ps.parseFromString(
                "{1.124289515171076E-4,0.21142446792225209,0.8163755075028104}");
        PointType p2 = ps.parseFromString(
                "{1.2370157642292858E-4,0.2119690168629486641,0.816632218858629587}");
        PointType g1 = ps.getGroup(p1);
        PointType g2 = ps.getGroup(p2);
        assertEquals(g1, g2);
    }

    @Test
    public void testGetGroupBig() {
        PointType from = new PointType(1.0, 1.0, 1.0);
        PointType to = new PointType(10000.0, 10000.0, 10000.0);
        int intervals = 1000;
        PointClusterer ps = new PointClusterer();
        ps.setGrouping(from, to, intervals);
        PointType t1 = new PointType(2.0, 2.0, 2.0);
        assertEquals(ps.getGroup(from), ps.getGroup(t1));
        PointType t2 = new PointType(1100.0, 1100.0, 1100.0);
        PointType mod = ps.getGroup(from);
        mod.setX(mod.getX() + 1.0);
        mod.setY(mod.getY() + 1.0);
        mod.setZ(mod.getZ() + 1.0);
        assertEquals(mod, ps.getGroup(t2));


    }

    @Test
    public void testGetGroupFract() {
        PointType from = new PointType(0.0001, 0.0001, 0.0001);
        PointType to = new PointType(1.0, 1.0, 1.0);
        int intervals = 1000;
        PointClusterer ps = new PointClusterer();
        ps.setGrouping(from, to, intervals);
        PointType t1 = new PointType(0.0002, 0.0002, 0.0002);
        assertEquals(ps.getGroup(from), ps.getGroup(t1));
        PointType t2 = new PointType(0.1009, 0.1009, 0.1009);
        PointType mod = ps.getGroup(from);
        mod.setX(mod.getX() + 1.0);
        mod.setY(mod.getY() + 1.0);
        mod.setZ(mod.getZ() + 1.0);
        assertEquals(mod, ps.getGroup(t2));


    }

    @Test
    public void testRange() {
        PointType from = new PointType(0.0001, 0.0001, 0.0001);
        PointType to = new PointType(1.0, 1.0, 1.0);
        int intervals = 1000;
        PointClusterer ps = new PointClusterer();
        ps.setGrouping(from, to, intervals);
        from = new PointType(0.001, 0.001, 0.001);
        to = new PointType(0.002, 0.002, 0.002);
        List<PointType> r = ps.getGroupsInterval(from, to);
        assertEquals(1, r.size());
        from = new PointType(0.001, 0.001, 0.001);
        to = new PointType(0.11, 0.001, 0.001);
        r = ps.getGroupsInterval(from, to);
        assertEquals(2, r.size());
        PointType f = randomP(from, to);
        PointType t = randomP(from, to);
        PointType gf = ps.getGroup(f);
        PointType gt = ps.getGroup(t);
        int num = (int) Math.round(Math.abs((gt.getX() - gf.getX()) * (gt.getX() - gf.getX()) * (gt.getX() - gf.getX()))) + 1;
        assertEquals(num, ps.getGroupsInterval(f, t).size());

    }

    //@Test
    public void testDistrib() {

        assertEquals("Wrong distribution",(double)intervals,(double)ms.keySet().size(),  intervals * 0.3);
        int sum = 0;
        for (PointType p : ms.keySet()) {
            assertEquals((double) k, (double) ms.get(p).size(), k * 0.5);
            sum += ms.get(p).size();
        }
        assertEquals(points.size(), sum);
    }

    @Test
    public void TestSort() {
        //sorting test
        int f = r.nextInt(points.size());
        PointType from = points.get(f);
        PointType to = new PointType(from.getX()*(1.5),from.getY()*(1.5),from.getZ()*(1.5));
        points.add(to);
        ms.put(ps.getGroup(to), to);
        Collections.sort(points);
//        for (PointType p : points) {
//            System.out.println(p);
//        }
//        System.out.println("Grouping");
//        for (PointType p : ms.keySet()) {
//            System.out.println("Group :" + p);
//
//            for (PointType v : ms.get(p)) {
//                System.out.println(v);
//            }
//        }
        
       
       
        System.out.printf("Selected points are \n%s \n%s\n",from,to);
        List<PointType> range = ps.getGroupsInterval(from, to);
        
        SortedSet<PointType> ss = new TreeSet<PointType>();
        for (PointType p : range) {
            ss.addAll(ms.get(p));
        }
//        System.out.println("Clustered points ss");
//        for (PointType p : ss) {
//            System.out.println(p);
//        }
        SortedSet<PointType> treeSet =   limit(from,to,points);
        ss = limit(from, to,ss);
//        System.out.println("Clustered points reduced");
//        for (PointType p : ss) {
//            System.out.println(p);
//        }
//        System.out.println("Treeset points reducedd");
//        for (PointType p : treeSet) {
//            System.out.println(p);
//        }
        
        assertArrayEquals(treeSet.toArray(), ss.toArray());

    }
    //yep.. not thread safe.. and so?
    static Random r = new Random();
    private SortedSet<PointType> limit(PointType from,PointType to,Collection<PointType> col){
        TreeSet<PointType> ret=new TreeSet();
        for(PointType p:col){
            if(p.getX()>=from.getX()&&p.getY()>=from.getY()&&p.getZ()>=from.getZ()&&//
                    p.getX()<=to.getX()&&p.getY()<=to.getY()&&p.getZ()<=to.getZ()){
                ret.add(p);
            }
        }
        return ret;
    }
    public static PointType randomP(PointType from, PointType to) {
        PointType pa = new PointType();
        pa.setX(rangeRand(from.getX(), to.getX()));
        pa.setY(rangeRand(from.getY(), to.getY()));
        pa.setZ(rangeRand(from.getZ(), to.getZ()));
        return pa;


    }

    public static double rangeRand(double from, double to) {
        return (to - from) * r.nextDouble() + from;
    }

    @Test
    public void testGetGroupsInterval() {
    }

    @Test
    public void testParseFromString() {
    }
}
