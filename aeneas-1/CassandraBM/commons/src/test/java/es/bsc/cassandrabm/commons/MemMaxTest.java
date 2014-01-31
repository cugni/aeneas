/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.commons;

import org.junit.*;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author ccugnasc
 */
public class MemMaxTest {

    public MemMaxTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class MemMax.
     */
    @Test
    public void testInt() {
        MemMax<Integer> m = new MemMax<Integer>(0);
        for (int i = 0; i <= 100; i++) {
            assertEquals((Integer) i, m.add(i));
        }
        assertEquals((Integer) 100, m.getMax());

        m = new MemMax<Integer>(0);
        for (int i = 100; i > 0; i--) {
            assertEquals((Integer) i, m.add(i));
        }
        assertEquals((Integer) 100, m.getMax());
        
         m = new MemMax<Integer>(0);
         ArrayList<Integer> li=new ArrayList<Integer>();
          for (int i = 100; i > 0; i--) {
           li.add(i);
        }
          Collections.shuffle(li);
        for (int i :li) {
            assertEquals((Integer) i, m.add(i));
        }
        assertEquals((Integer) 100, m.getMax());
    }
}
