/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.distributions;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class ConstantIntegerGeneratorTest   {
    
    
    @Test
    public void testNext() {
        Integer t=new Random().nextInt();
        ConstantIntegerGenerator test=new ConstantIntegerGenerator(t);
        for(int i=0;i<20;i++){
            assertEquals(t,test.next());
        }
    }
}
