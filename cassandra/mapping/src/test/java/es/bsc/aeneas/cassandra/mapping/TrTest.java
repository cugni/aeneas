/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.cassandra.mapping.Tr;
import es.bsc.aeneas.model.gen.Dest;
import es.bsc.aeneas.model.gen.StandardType;
import es.bsc.aeneas.model.gen.Type;
import es.bsc.aeneas.model.gen.ValueDest;
import me.prettyprint.hector.api.beans.DynamicComposite;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author ccugnasc
 */
public class TrTest {

    public TrTest() {
    }

    /**
     * Test of transform method, of class Tr.
     */
    @Test
    public void testTransform() {
        Dest dest = new ValueDest();
        dest.setPosition(0);
        Type e = new Type();
        e.setStandardType(StandardType.ANY_TYPE);

        Tr t = new Tr(dest, e);
        String a = "ciao";
        Object transform = t.transform(a);
        assertEquals(transform.getClass(), DynamicComposite.class);
        assertEquals(((DynamicComposite) transform).get(0), a);
    }
}