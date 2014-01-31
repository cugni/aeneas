/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.model;

import es.bsc.aeneas.cassandra.model.CPointType;
import junit.framework.TestCase;

import java.nio.ByteBuffer;

/**
 *
 * @author cesare
 */
public class CPointTypeTest extends TestCase {

    CPointType cp = CPointType.instance;

    public CPointTypeTest(String testName) {
        super(testName);
    }

    public void testCompare() {
        ByteBuffer b1 = ByteBuffer.allocate(24);
        ByteBuffer b2 = ByteBuffer.allocate(24);
        b1.putDouble(1.0).putDouble(2.0).putDouble(3.0).position(0);
        b2.putDouble(1.0).putDouble(2.0).putDouble(3.0).position(0);
        assertEquals(0, cp.compare(b1, b2));
        b2 = ByteBuffer.allocate(24).putDouble(2.0).putDouble(2.0).putDouble(3.0);
        b2.position(0);
        assertEquals(-1, cp.compare(b1, b2));
        assertEquals(1, cp.compare(b2, b1));
    }

    public void testGetString() {
        ByteBuffer b1 = ByteBuffer.allocate(24);
        b1.putDouble(1.0234).putDouble(2.0532423).putDouble(3.0432432).position(0);
        assertEquals("{1.0234,2.0532423,3.0432432}", cp.getString(b1));
        assertEquals(0, b1.position());
    }

    public void testValidate() {
    }

    public void testCompose() {
    }

    public void testDecompose() {
    }

    public void testFromString() {
        ByteBuffer b1 = ByteBuffer.allocate(24);
        b1.putDouble(1.0).putDouble(2.0).putDouble(3.0).position(0);
        assertEquals(b1.position(0), cp.fromString("{1.0,2.0,3.0}"));
    }
}
