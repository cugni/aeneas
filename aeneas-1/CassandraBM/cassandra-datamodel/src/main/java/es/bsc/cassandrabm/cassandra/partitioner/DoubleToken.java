/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.cassandra.partitioner;

import org.apache.cassandra.dht.Token;

/**
 *
 * @author cesare
 */
public class DoubleToken extends Token<Double> {

    @Override
    public int compareTo(Token<Double> o) {
      return token.compareTo(o.token);
    }

    public DoubleToken(Double token) {
        super(token);
    }
    
}
