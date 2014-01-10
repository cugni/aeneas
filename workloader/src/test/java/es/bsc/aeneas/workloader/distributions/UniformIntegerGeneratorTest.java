/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class UniformIntegerGeneratorTest {

    private static final Logger log = Logger.getLogger(UniformIntegerGeneratorTest.class.getName());

    @Test
    public void testNext() {
        Random r = new Random();
        int maxrange = 10000;
        int minrange = 100;
        int gentest = 1000000;
        int from = Math.abs(r.nextInt());
        int to = (int) (from + minrange + (r.nextFloat() * (maxrange - minrange)));
        log.log(Level.INFO, "From {0}", from);
        log.log(Level.INFO, "To {0}", to);
        Multiset<Integer> m = HashMultiset.create();
        UniformIntegerGenerator zig = new UniformIntegerGenerator(from, to);
        for (int i = 0; i < gentest; i++) {
            m.add(zig.next());
        }
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (Integer i : m.elementSet()) {
            int count = m.count(i);
            if (count > max) {
                max = count;
            }
            if (count < min) {
                min = count;
            }
            log.log(Level.FINE, "Item {0} found {1} times", new Object[]{i, count});

        }
        int itn = m.elementSet().size();
        log.log(Level.INFO, "Max occurents {0}", max);
        log.log(Level.INFO, "Min occurents {0}", min);
        log.log(Level.INFO, "Items numbers {0}", itn);

        assertEquals(max, min, min * 2); //unifor relaxed

    }
}
