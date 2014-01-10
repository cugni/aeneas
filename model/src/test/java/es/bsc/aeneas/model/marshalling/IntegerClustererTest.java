/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.model.marshalling;

import es.bsc.aeneas.model.marshalling.IntegerClusterer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class IntegerClustererTest {

    public IntegerClustererTest() {
    }

    @Test
    public void testSetGrouping() {
        System.out.println("setGrouping");

        IntegerClusterer i = new IntegerClusterer();
        i.setGrouping(0, 100, 10);
        assertEquals(0, (Object) i.getGroup(8));
        assertEquals(16, (Object) i.getGroup(20));
        assertEquals(80, (Object) i.getGroup(88));
        i.setGrouping(0, 5000, 10);
        assertEquals(0, (Object) i.getGroup(15));
        assertEquals(512, (Object) i.getGroup(1005));
    }

    /**
     * Test of getGroup method, of class IntegerClusterer.
     */
    @Test
    public void testGetGroup() {
        System.out.println("getGroup");

        IntegerClusterer i = new IntegerClusterer();
        i.setGrouping(0, 1000, 10);
        Integer result = i.getGroup(546);
        assertEquals((Integer) 512, result);

    }

    /**
     * Test of getGroupsInterval method, of class IntegerClusterer.
     */
    @Test
    public void testGetGroupsInterval() {
        System.out.println("getGroupsInterval");
        IntegerClusterer i = new IntegerClusterer();
        i.setGrouping(0, 1000, 10);
        List<Integer> sub = Arrays.asList(512, 640, 768);
        List result = i.getGroupsInterval(543, 863);
        assertEquals(sub, result);

    }

    /**
     * Test of parseFromString method, of class IntegerClusterer.
     */
    @Test
    public void testParseFromString() {
        System.out.println("parseFromString");
        String s = "1555432";
        IntegerClusterer instance = new IntegerClusterer();
        Integer result = instance.parseFromString(s);
        assertEquals((Integer) 1555432, result);
    }
}
