/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import es.bsc.cassandrabm.workloader.controller.Range;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class SequenceRangeNumberGeneratorTest {

    @Test
    public void testGetNextRangeInteger() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        int from = Math.abs(r.nextInt());
        int to = from + minrange + (int) ((r.nextFloat() * (maxrange - minrange)));
        int step = (int) ((to - from - 1) * r.nextFloat());

        SequenceRangeNumberGenerator test = new SequenceRangeNumberGenerator(from, to,step);
        for (int i = 0; i < 1000; i++) {
            Range ra = test.getNextRange();
            assertTrue(ra.getFrom() instanceof Integer);
            assertTrue(ra.getTo() instanceof Integer);
            Integer from1 = (Integer) ra.getFrom();
            Integer to1 = (Integer) ra.getTo();
            assertTrue(from1 >= from);
            assertTrue(to1 <= to1);

        }
    }

    @Test
    public void testGetNextRangeLong() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        long from = Math.abs(r.nextLong());
        long to = from + minrange + (long) ((r.nextFloat() * (maxrange - minrange)));
        long step = (long) ((to - from - 1) * r.nextFloat());

        SequenceRangeNumberGenerator test = new SequenceRangeNumberGenerator(from, to,step);
        for (int i = 0; i < 1000; i++) {
            Range ra = test.getNextRange();
            assertTrue(ra.getFrom() instanceof Long);
            assertTrue(ra.getTo() instanceof Long);
            Long from1 = (Long) ra.getFrom();
            Long to1 = (Long) ra.getTo();
            assertTrue(from1 >= from);
            assertTrue(to1 <= to1);

        }
    }
}
