/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.model.gen.CrudType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class SimpleRosettaTest {
    
    public SimpleRosettaTest() {
    }

    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        SimpleRosetta instance = new SimpleRosetta();
        instance.init();
        fail("The test case is a prototype.");
    }

    @Test
    public void testQueryAll() {
        System.out.println("queryAll");
        CrudType crud = null;
        Object[] path = null;
        SimpleRosetta instance = new SimpleRosetta();
        Result expResult = null;
        Result result = instance.queryAll(crud, path);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetMatching() {
        System.out.println("getMatching");
        CrudType crud = null;
        String url = "";
        SimpleRosetta instance = new SimpleRosetta();
        Result expResult = null;
        Result result = instance.getMatching(crud, url);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
}