/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.readers;

import es.bsc.aeneas.core.nodeagent.readers.NetStatReader;
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
public class NetStatReaderTest {

    private static final Logger LOG = Logger.getLogger(NetStatReaderTest.class.getName());

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public NetStatReaderTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCall() throws Exception {
        NetStatReader io = new NetStatReader();
        List<Metric> l = io.call();
        assertTrue(l.size() == 8);
        for (int i = 0; i < 8; i++) {
            assertEquals(l.get(i).getGroup(), "eth0");
        }
        assertEquals(l.get(0).getName(), "rx.bytes");
        assertEquals(l.get(1).getName(), "rx.packets");
        assertEquals(l.get(2).getName(), "rx.errs");
        assertEquals(l.get(3).getName(), "rx.drop");
        assertEquals(l.get(4).getName(), "tx.bytes");
        assertEquals(l.get(5).getName(), "tx.packets");
        assertEquals(l.get(6).getName(), "tx.errs");
        assertEquals(l.get(7).getName(), "tx.drop");

    }

    @Test
    public void testConfigure() throws Exception {
        NetStatReader io = new NetStatReader();
        Configuration c = new BaseConfiguration();
        c.addProperty("iostat-reader.deviceregexp", "eth0");
        io.setConf(c);
        io.configure();
        List<Metric> l = io.call();
        for (int i = 0; i < 4; i++) {
            assertEquals(l.get(i).getGroup(), "eth0");
        }
        assertTrue(l.size() == 8);
        assertEquals(l.get(0).getName(), "rx.bytes");
        assertEquals(l.get(1).getName(), "rx.packets");
        assertEquals(l.get(2).getName(), "rx.errs");
        assertEquals(l.get(3).getName(), "rx.drop");
        assertEquals(l.get(4).getName(), "tx.bytes");
        assertEquals(l.get(5).getName(), "tx.packets");
        assertEquals(l.get(6).getName(), "tx.errs");
        assertEquals(l.get(7).getName(), "tx.drop");
    }
}