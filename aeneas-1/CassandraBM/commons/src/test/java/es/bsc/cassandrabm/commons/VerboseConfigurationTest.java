/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.commons;

import java.util.HashMap;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class VerboseConfigurationTest {

    public VerboseConfigurationTest() {
    }

    @Test
    public void testProprieties() {
        VerboseConfiguration vc = new VerboseConfiguration(new MapConfiguration(new HashMap(10)));
        assertEquals((int) 20, vc.getInt("prova", 20));

        vc.setProperty("prova", "40");
        assertEquals((int) 40, vc.getInt("prova", 20));
        vc.setProperty("prova2", "pres");
        assertEquals("pres", vc.getString("prova2", "pres"));
        vc.setProperty("prova2", "sec2");
        assertEquals("sec2", vc.getString("prova2", "pres"));

        assertEquals(false, vc.getBoolean("prova3", false));
        vc.setProperty("prova3", "true");
        assertEquals(true, vc.getBoolean("prova3", false));
    }
}