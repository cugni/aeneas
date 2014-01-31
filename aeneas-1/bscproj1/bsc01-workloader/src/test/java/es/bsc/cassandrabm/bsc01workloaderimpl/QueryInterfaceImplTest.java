/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

import com.google.common.collect.Table;
import es.bsc.cassandrabm.bsc01workloaderimpl.TestingInterface;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author cesare
 */
public abstract class QueryInterfaceImplTest {

    protected TestingInterface qi;
    private Random r = new Random();
    int FRAMES_NUMBER = CUtils.getInt("framesnumber", 1001);
    int ATOMS_NUMBER = CUtils.getInt("atomsnumber", 99637);
    final static Logger log = Logger.getLogger("QueryInterfaceImplTest");

    protected QueryInterfaceImplTest(TestingInterface qi) {
        this.qi = qi;

    }

    @Test
    public void testBoudaries() {
        Table<Integer, Integer, PointType> p;
        try {
            p = (Table<Integer, Integer, PointType>) qi.getPoints(0, 0, 0, 0, "").get("points");
            assertTrue(p.isEmpty());
        } catch (Exception e) {
            log.log(Level.INFO, "Expected exception", e);
        }
        try {
            p = (Table<Integer, Integer, PointType>) qi.getPoints(0, 1, 0, 0, "").get("points");
            assertTrue(p.isEmpty());
        } catch (Exception e) {
            log.log(Level.INFO, "Expected exception", e);
        }
        try {
            p = (Table<Integer, Integer, PointType>) qi.getPoints(0, 0, 0, 1, "").get("points");
            assertTrue(p.isEmpty());
        } catch (Exception e) {
            log.log(Level.INFO, "Expected exception", e);
        }

        p = (Table<Integer, Integer, PointType>) qi.getPoints(0, 1, 0, 1, "").get("points");
        assertFalse(p.isEmpty());
        assertEquals((Integer) 1, (Integer) p.values().size());

        p = (Table<Integer, Integer, PointType>) qi.getPoints(0, 1, 0, 10, "").get("points");
        assertFalse(p.isEmpty());
        assertEquals((Integer) 10, (Integer) p.values().size());
    }

    @Test
    public void testGetFrameRangePoints10pc() {

        Integer frameFrom = r.nextInt(FRAMES_NUMBER - FRAMES_NUMBER / 10);
        testGetPoints(frameFrom, frameFrom + FRAMES_NUMBER / 10, 0, ATOMS_NUMBER);

    }

    // @Test
    public void testGetFrameRangePoints20pc() {

        Integer frameFrom = r.nextInt(FRAMES_NUMBER);
        testGetPoints(frameFrom, FRAMES_NUMBER / 5, 0, ATOMS_NUMBER);

    }

    //   @Test
    public void testGetFrameRangePoints50pc() {

        Integer frameFrom = r.nextInt(FRAMES_NUMBER - FRAMES_NUMBER / 2);
        testGetPoints(frameFrom, frameFrom + FRAMES_NUMBER / 2, 0, ATOMS_NUMBER);

    }

    //  @Test
    public void testGetFrameRangePoints100pc() {

        Integer frameFrom = r.nextInt(FRAMES_NUMBER - FRAMES_NUMBER);
        testGetPoints(frameFrom, frameFrom + FRAMES_NUMBER, 0, ATOMS_NUMBER);

    }

    Table<Integer, Integer, PointType> testGetPoints(Integer frameFrom, Integer frameTo, Integer atomFrom, Integer atomTo) {
        Map framePoints = qi.getPoints(frameFrom, frameTo, atomFrom, atomTo, "");
        long stime = System.currentTimeMillis();
        Table<Integer, Integer, PointType> points = (Table<Integer, Integer, PointType>) framePoints.get("points");
        stime = System.currentTimeMillis() - stime;
        Integer framestoget = frameTo - frameFrom;
        Integer atomstoget = atomTo - atomFrom;
        log.log(Level.INFO, "query on GetFrameRangePoints from {0} for {1} frames executed in {2} milliseconds",
                new Object[]{frameFrom, framestoget, stime});

        assertNotNull(points);
        assertEquals("number of frame get [" + frameFrom, (Integer) framestoget,
                (Integer) points.rowKeySet().size());
        assertEquals("number of atom get [" + atomFrom, (Integer) atomstoget,
                (Integer) points.columnKeySet().size());
        assertEquals("total size [" + frameFrom, (atomstoget) * framestoget, points.size());
        for (int f = frameFrom; f < frameTo; f++) {
            for (int a = atomFrom; a < atomTo; a++) {
                assertNotNull("missed the point f=" + f + " a=" + a,
                        points.get(f, a));
                assertEquals(PointType.class, points.get(f, a).getClass());
            }
        }
        assertNull("It is not [ )", points.get(frameTo, atomTo));
        assertNull("It is not [ )", points.get(frameFrom, atomTo));
        assertNull("It is not [ )", points.get(frameTo, atomFrom));

        return points;
    }

    @Test
    public void testGetAtmosRangePoints10pc() {

        Integer frameFrom = r.nextInt(ATOMS_NUMBER - ATOMS_NUMBER / 10);
        testGetPoints(0, FRAMES_NUMBER, frameFrom, frameFrom + ATOMS_NUMBER / 10);

    }

    // @Test
    public void testGetAtmosRangePoints20pc() {

        Integer atomFrom = r.nextInt(ATOMS_NUMBER);
        testGetPoints(0, FRAMES_NUMBER, atomFrom, ATOMS_NUMBER / 5);

    }

    //   @Test
    public void testGetAtmosRangePoints50pc() {

        Integer atomFrom = r.nextInt(ATOMS_NUMBER);
        testGetPoints(0, FRAMES_NUMBER, atomFrom, ATOMS_NUMBER / 2);

    }

    // @Test
    public void testGetAtmosRangePoints100pc() {

        Integer atomFrom = r.nextInt(ATOMS_NUMBER);
        testGetPoints(0, FRAMES_NUMBER, atomFrom, ATOMS_NUMBER);

    }
}
