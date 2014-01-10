/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.controller;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author cesare
 */
public class ClientTest extends TestCase {

   
    @Test
    public void testMain() throws Exception {
        System.setProperty("ntest", "1");
         
        System.setProperty("continueonerror", "false");
        
        Client.main(null);
    }
     @Test
    public void testMain1000() throws Exception {
        System.setProperty("ntest", "1000");
         
        System.setProperty("continueonerror", "false");
        
        Client.main(null);
    }

}
