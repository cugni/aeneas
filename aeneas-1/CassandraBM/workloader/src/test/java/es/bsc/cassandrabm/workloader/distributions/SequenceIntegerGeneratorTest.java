/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class SequenceIntegerGeneratorTest {

    @Test
    public void testReset() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        int from = Math.abs(r.nextInt());
        int to = from + minrange + new Double(Math.ceil(r.nextFloat() * (maxrange - minrange))).intValue();
        SequenceIntegerGenerator test = new SequenceIntegerGenerator(from, to);
        int rand = (from + new Double(Math.ceil((to - from) * r.nextFloat())).intValue());
        for (int i = from; i < rand; i++) {
            int j = test.next();
            assertEquals(i, j);
        }
        test.reset();
        int l = test.next();
        assertEquals(l, from);
    }

    public void testNext() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        int from = Math.abs(r.nextInt());
        int to = from + minrange + new Double(Math.ceil(r.nextFloat() * (maxrange - minrange))).intValue();
        SequenceIntegerGenerator test = new SequenceIntegerGenerator(from, to);
        for (int i = from; i <= to; i++) {
            int j = test.next();
            assertEquals(i, j);

        }
        int l = test.next();
        assertEquals(l, from);

    }
}
