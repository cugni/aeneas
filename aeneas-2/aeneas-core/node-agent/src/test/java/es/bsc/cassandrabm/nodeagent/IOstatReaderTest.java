/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent;

import es.bsc.aeneas.core.nodeagent.Metric;
import es.bsc.aeneas.core.nodeagent.readers.IOstatReader;
import org.junit.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class IOstatReaderTest {
    private final static Logger log=Logger.getLogger(IOstatReaderTest.class.toString());

    public IOstatReaderTest() {
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

    @Test
    public void testGetSamples() throws Exception {
        IOstatReader t = new IOstatReader("sda");
        List<Metric> samples = t.call();
        assertTrue(!samples.isEmpty());
        assertTrue(samples.size() == 4);
        Metric io = samples.get(0);
        assertEquals("sda", io.getGroup());
        assertTrue(((Long)samples.get(0).getValue()) > 0);
        assertTrue(((Long)samples.get(1).getValue()) > 0);
        assertTrue(((Long)samples.get(2).getValue()) > 0);
        assertTrue(((Long)samples.get(3).getValue()) > 0);
         log.log(Level.INFO, " {0} {1} {2} {3} {4}", 
                new Object[]{samples.get(0).getValue(),
                    samples.get(1).getValue(),
                    samples.get(2).getValue(),
                    samples.get(3).getValue()});
        t = new IOstatReader("(sda|ram0|ram1)");
        samples = t.call();
        assertTrue(!samples.isEmpty());
        assertTrue(samples.size() == 3*4);
    }
}
