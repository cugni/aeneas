/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.readers;

import es.bsc.aeneas.core.nodeagent.readers.IOstatReader;
import es.bsc.aeneas.core.nodeagent.Metric;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.*;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class IOstatReaderTest {
    private static final Logger LOG = Logger.getLogger(IOstatReaderTest.class.getName());

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

        public IOstatReaderTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCall() throws Exception {
        IOstatReader io = new IOstatReader();
        List<Metric> l = io.call();
        assertTrue(l.size() == 4);
        for (int i = 0; i < 4; i++) {
            assertEquals(l.get(i).getGroup(), "sda");
        }
        assertEquals(l.get(0).getName(), "n_reads");
        assertEquals(l.get(1).getName(), "mills_reading");
        assertEquals(l.get(2).getName(), "n_writes");
        assertEquals(l.get(3).getName(), "mills_reading");

    }

    @Test
    public void testConfigure() throws Exception {
        IOstatReader io = new IOstatReader();
        Configuration c = new BaseConfiguration();
        c.addProperty("iostat-reader.deviceregexp", "sda1");
        io.setConf(c);
        io.configure();
        List<Metric> l = io.call();
        for (int i = 0; i < 4; i++) {
            assertEquals(l.get(i).getGroup(), "sda1");
        }
        assertTrue(l.size() == 4);
        assertEquals(l.get(0).getName(), "n_reads");
        assertEquals(l.get(1).getName(), "mills_reading");
        assertEquals(l.get(2).getName(), "n_writes");
        assertEquals(l.get(3).getName(), "mills_reading");
    }
}