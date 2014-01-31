/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class SequenceLongGeneratorTest {
    @Test
    public void testReset() {
        Random r = new Random();
        long maxrange = 2000;
        long minrange = 100;
        long from = Math.abs(r.nextLong());
        long to =from+ minrange + (long) ( Math.ceil(r.nextFloat() * (maxrange - minrange)));
        SequenceLongGenerator test = new SequenceLongGenerator(from, to);
        long rand=from+new Double(Math.ceil((to - from) * r.nextFloat())).longValue();
        for (long i = from; i <rand ; i++) {
            long j = test.next();
            assertTrue(i == j);
        }
        test.reset();
        long l = test.next();
        assertTrue(l == from);
    }
    @Test
    public void testNext() {
        Random r = new Random();
        long maxrange = 2000;
        long minrange = 100;
        long from = Math.abs(r.nextLong());
        long to  =from+ minrange + (long) ( Math.ceil(r.nextFloat() * (maxrange - minrange)));
        SequenceLongGenerator test = new SequenceLongGenerator(from, to);
        for (long i = from; i <= to; i++) {
            long j = test.next();
            assertEquals(i, j);

        }
        long l = test.next();

        assertEquals(from, l);
    }
}
