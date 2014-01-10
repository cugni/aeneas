/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

import es.bsc.aeneas.workloader.controller.Range;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class NumberRangeGeneratorTest {

  
    @Test
    public void testGetNextRange() {
        Random r = new Random();
        int maxrange = 2000;
        int minrange = 100;
        int fromi = Math.abs(r.nextInt());
        int toi = fromi + minrange + new Double(Math.ceil((r.nextFloat() * (maxrange - minrange)))).intValue();
        long froml = Math.abs(r.nextLong());
        long tol = froml + minrange + new Double(Math.ceil((r.nextFloat() * (maxrange - minrange)))).longValue();

        NumberGenerator[] gens = new NumberGenerator[]{
            new UniformIntegerGenerator(fromi, toi),
            new UniformLongGenerator(froml, tol),
            new ZipfianIntegerGenerator(fromi, toi),
            new ZipfianLongGenerator(froml, tol)
        };
        for (NumberGenerator g : gens) {
            NumberRangeGenerator test = new NumberRangeGenerator(g,null);
            for (int i = 0; i < 1000; i++) {
                Range n = test.getNextRange();
                assertFalse(n.getFrom().equals(n.getTo()));
                assertTrue(n.getFrom().compareTo(n.getTo()) < 0);
                assertTrue(n.getFrom() instanceof Number);
                assertTrue(n.getTo() instanceof Number);
            }
        }
    }
}
