/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.model.util;


import es.bsc.aeneas.core.model.PointType;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.clusterer.DoubleClusterer;
import es.bsc.aeneas.core.model.clusterer.PointClusterer;
import org.junit.*;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author cesare
 */
public class ClusterersTest {

    public ClusterersTest() {
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
     * Test of getClustererByClassName method, of class Clusterers.
     */
    @Test
    public void testGetClusterer() {
        Type t = new Type();
        t.setStandardType(StandardType.DOUBLE_TYPE);
        assertTrue(Clusterers.getClusterer(t) instanceof DoubleClusterer);
        t = new Type();
        t.setCustomType(PointType.class.getCanonicalName());
        assertTrue(Clusterers.getClusterer(t) instanceof PointClusterer);

    }

    /**
     * Test of getClustererByClass method, of class Clusterers.
     */
    @Test
    public void testGetClustererByClass() {
        assertTrue(Clusterers.getClustererByClass(double.class) instanceof DoubleClusterer);
        assertTrue(Clusterers.getClustererByClass(Double.class) instanceof DoubleClusterer);
        assertTrue(Clusterers.getClustererByClass(PointType.class) instanceof PointClusterer);
    }
}
