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
public class ConstantLongGeneratorTest  {
    
    @Test
    public void testNext() {
        Long t=new Random().nextLong();
        ConstantLongGenerator test=new ConstantLongGenerator(t);
        for(int i=0;i<20;i++){
            assertEquals(t,test.next());
        }
    }
}
