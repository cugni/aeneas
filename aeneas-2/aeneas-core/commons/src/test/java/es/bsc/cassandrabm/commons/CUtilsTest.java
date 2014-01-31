package es.bsc.aeneas.commons;

import es.bsc.aeneas.commons.CUtils;
import java.util.Arrays;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class CUtilsTest {

    @Test
    public void testProprieties() {
        assertEquals((Integer) 20, CUtils.getInt("prova", 20));
        Configuration c = CUtils.getConfiguration();

        System.setProperty("prova", "40");
        assertEquals((Integer) 40, CUtils.getInt("prova", 20));
        System.setProperty("prova2", "pres");
        assertEquals("pres", CUtils.getString("prova2", "pres"));
        System.setProperty("prova2", "sec2");
        assertEquals("sec2", CUtils.getString("prova2", "pres"));

        assertEquals(false, CUtils.getBoolean("prova3", false));
        System.setProperty("prova3", "true");
        assertEquals(true, CUtils.getBoolean("prova3", false));
    }

    @Test
    public void testGetDiscreteRange() {
        assertEquals((Integer) 1, (Integer) CUtils.getDiscreteRange(0, 1).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(0, 0).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(1, 1).size());
        assertEquals((Integer) 10, (Integer) CUtils.getDiscreteRange(0, 10).size());
        assertEquals((Integer) 9, (Integer) CUtils.getDiscreteRange(1, 10).size());

        assertEquals((Integer) 1, (Integer) CUtils.getDiscreteRange(0, 1, 1000).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(0, 0, 1000).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(1, 1, 1000).size());
        assertEquals((Integer) 10, (Integer) CUtils.getDiscreteRange(0, 10, 1000).size());
        assertEquals((Integer) 9, (Integer) CUtils.getDiscreteRange(1, 10, 1000).size());

        assertEquals((Integer) 1, (Integer) CUtils.getDiscreteRange(0, 1, 1).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(0, 0, 1).size());
        assertEquals((Integer) 0, (Integer) CUtils.getDiscreteRange(1, 1, 1).size());
        assertEquals((Integer) 1, (Integer) CUtils.getDiscreteRange(0, 10, 1).size());
        assertEquals((Integer) 3, (Integer) CUtils.getDiscreteRange(0, 10, 3).size());
        assertEquals((Integer) 2, (Integer) CUtils.getDiscreteRange(1, 10, 2).size());
    }
}
