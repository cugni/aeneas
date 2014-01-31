/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.thesisimpl.cassandra.partitioner;

import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.cassandra.partitioner.DoubleOrderedPartitioner;
import es.bsc.cassandrabm.cassandra.partitioner.DoubleToken;
import junit.framework.TestCase;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.dht.Token.TokenFactory;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 *
 * @author cesare
 */
public class DoubleOrderedPartitionerTest extends TestCase {

    DoubleOrderedPartitioner p = new DoubleOrderedPartitioner();
    TokenFactory<Double> f = p.getTokenFactory();

    @Test
    public void testCompare() {

        assertEquals(f.fromString("2.0").token, 2.0, 0.0);
        assertEquals(f.toString(new DoubleToken(2.0)), "2.0");



    }

    @Test
    public void testTokenFactoryBytes() {

        assertEquals(0, tok(1.)
                .compareTo(f.fromByteArray(
                f.toByteArray(tok(1.)))));
    }

    private DoubleToken tok(Double d) {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putDouble(0, d);
        return p.getToken(b);

    }
   

    private void assertMidpoint(Token left, Token right, int depth) {
        Token mid = p.midpoint(left, right);
        //  assertTrue( "For " + left + "," + right + ": range did not contain mid:" + mid, new Range<Token>(left, right).contains(mid));
        if (depth < 1) {
            return;
        }

        if (CUtils.random().nextBoolean()) {
            assertMidpoint(left, mid, depth - 1);
        } else {
            assertMidpoint(mid, right, depth - 1);
        }
    }

    @Test
    public void testMidpoint() {
        assertMidpoint(tok(20.0), tok(40.0), 16);
        assertMidpoint(tok(-20.0), tok(20.0), 16);
    }

    @Test
    public void testMidpointMinimum() {
        midpointMinimumTestCase();
    }

    protected void midpointMinimumTestCase() {
        DoubleToken mintoken = p.getMinimumToken();
        //   assertTrue(mintoken.compareTo(p.midpoint(mintoken, mintoken)) != 0);
        assertMidpoint(mintoken, tok(2.0), 16);
        assertMidpoint(mintoken, tok(3.0), 16);
        assertMidpoint(mintoken, mintoken, 126);
        assertMidpoint(tok(4.0), mintoken, 16);
    }

    @Test
    public void testMidpointWrapping() {
        assertMidpoint(tok(9.9), tok(.9), 16);
        assertMidpoint(tok(.678), tok(.7), 16);
    }

    @Test
    public void testTokenFactoryStrings() {
        Token.TokenFactory factory = p.getTokenFactory();
        assertTrue(tok(2.).compareTo(factory.fromString(factory.toString(tok(2.)))) == 0);
    }

    @Test
    public void testRandomTokens() {
        ByteBuffer b = ByteBuffer.wrap("randomval".getBytes());
        DoubleToken token = p.getToken(b);
        assertTrue("minor", token.token <= p.maxHash.doubleValue());
        assertTrue("greater", token.token >= p.minHash.doubleValue());
        DoubleToken token2 = p.getToken(b);
        assertEquals(token, token2);
//        b = ByteBuffer.wrap("eightval".getBytes());
//        token = p.getToken(b);
//        assertTrue("minor", token.token <= p.maxHash.doubleValue());
//        assertTrue("greater", token.token >= p.minHash.doubleValue());
//        token2 = p.getToken(b);
//        assertEquals(token, token2);
    }
}
