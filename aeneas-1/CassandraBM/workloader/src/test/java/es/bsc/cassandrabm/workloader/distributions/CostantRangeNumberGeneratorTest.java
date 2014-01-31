/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import es.bsc.cassandrabm.workloader.controller.Range;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class CostantRangeNumberGeneratorTest   {

   
    @Test
    public void testNextTestInteger() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        int from = Math.abs(r.nextInt());
        int to = from + minrange + (int) ((r.nextFloat() * (maxrange - minrange)));
        CostantRangeNumberGenerator t = new CostantRangeNumberGenerator(from, to);
        for (int i = 0; i < 2000; i++) {
            Range n = t.getNextRange();
            assertTrue(n.getFrom() instanceof Integer);
            assertTrue(n.getTo() instanceof Integer);
            assertEquals(n.getFrom(), from);
            assertEquals(n.getTo(), to);
        }
    }
    @Test
     public void testNextTestLong() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        long from = Math.abs(r.nextLong());
        long to = from + minrange + (long) ((r.nextFloat() * (maxrange - minrange)));
        CostantRangeNumberGenerator t = new CostantRangeNumberGenerator(from, to);
        for (int i = 0; i < 2000; i++) {
            Range n = t.getNextRange();
            assertTrue(n.getFrom() instanceof Long);
            assertTrue(n.getTo() instanceof Long);
            assertEquals(n.getFrom(), from);
            assertEquals(n.getTo(), to);
        }
    }
}
