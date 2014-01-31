/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.readers;

import es.bsc.aeneas.core.nodeagent.readers.PagefaultReader;
import es.bsc.aeneas.core.nodeagent.Metric;
import org.junit.*;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class PagefaultReaderTest {

    public PagefaultReaderTest() {
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
    public void testCall() throws Exception {
        PagefaultReader io = new PagefaultReader();
        List<Metric> l = io.call();
        assertTrue(l.size() == 4);
        HashMap<String, Long> map = new HashMap<String, Long>(4);
        for (Metric met : l) {
            assertEquals(met.getGroup(), "memory");
            assertTrue(met.getValue() instanceof Long);
            map.put(met.getName(), (Long) met.getValue());
            assertTrue(0 <= (Long) met.getValue());
            System.out.println(met.getName()+"-->"+met.getValue());
        }
        assertTrue(map.containsKey("pgpgin"));
        assertTrue(map.containsKey("pgpgout"));
        assertTrue(map.containsKey("pswpin"));
        assertTrue(map.containsKey("pswpout"));


    }
}