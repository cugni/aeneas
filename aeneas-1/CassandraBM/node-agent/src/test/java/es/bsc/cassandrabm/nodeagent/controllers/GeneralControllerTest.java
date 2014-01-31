/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.controllers;

import es.bsc.cassandrabm.nodeagent.NodeAgentDaemon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author ccugnasc
 */
public class GeneralControllerTest {

    private static final Logger LOG = Logger.getLogger(GeneralControllerTest.class.getName());
    private NodeAgentDaemon nad;

    @Before
    public void serverUp() throws Exception {
        //Turning on the server
        nad =new  NodeAgentDaemon();
        nad.init(null);
        nad.start();


    }

    @After
    public void serverDown() throws Exception {
        //Turning on the server
       if(nad==null)return;
        nad.stop();
        nad.destroy();


    }

    /**
     * Test of set method, of class GeneralController.
     */
  //  @Test
    public void testStart() throws Exception {

        URL url = new URL("http://localhost:2626/api/init?columns=metricspace:metrics");
        HttpURLConnection req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        Scanner s = new Scanner(req.getInputStream());
        assertTrue(s.nextLine().contains("server configurated"));


        url = new URL("http://localhost:2626/api/start?testname=prova");
        req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        s = new Scanner(req.getInputStream());
        assertTrue(s.nextLine().contains("server running on the test"));
        //make one samble run
        Thread.sleep(20000);


        url = new URL("http://localhost:2626/api/status");
        req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        s = new Scanner(req.getInputStream());
        String l = s.nextLine();
        System.out.print(l);
        assertTrue(!l.contains("Exception "));

    }

    @Test
    public void testWrongSequence() throws Exception {

        URL url = new URL("http://localhost:2626/api/start?testname=prova");
        HttpURLConnection req = (HttpURLConnection) url.openConnection();
        assertEquals(500, req.getResponseCode());

        //make one samble run
        Thread.sleep(20000);

        url = new URL("http://localhost:2626/api/init?columns=metricspace:metrics");
        req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        Scanner s = new Scanner(req.getInputStream());
        assertTrue(s.nextLine().contains("server configurated"));


        url = new URL("http://localhost:2626/api/start?testname=prova");
        req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        s = new Scanner(req.getInputStream());
        assertTrue(s.nextLine().contains("server running on the test"));
        //make one samble run
        Thread.sleep(20000);


        url = new URL("http://localhost:2626/api/status");
        req = (HttpURLConnection) url.openConnection();
        assertEquals(200, req.getResponseCode());
        s = new Scanner(req.getInputStream());
        String l = s.nextLine();
        System.out.print(l);
        assertTrue(!l.contains("Exception "));

    }

    /**
     * Test of startSamplig method, of class GeneralController.
     */
   
}