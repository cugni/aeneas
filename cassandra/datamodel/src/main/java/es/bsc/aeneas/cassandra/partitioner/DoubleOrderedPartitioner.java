/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.partitioner;

import es.bsc.aeneas.commons.CUtils;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.dht.AbstractPartitioner;
import org.apache.cassandra.dht.Range;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.dht.Token.TokenFactory;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.FBUtilities;

import java.math.BigDecimal;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cesare
 */
public class DoubleOrderedPartitioner extends AbstractPartitioner<DoubleToken> {

    private final static double min=Double.parseDouble(CUtils.getString("minHash", "0.5"));
    private final static double max=Double.parseDouble(CUtils.getString("maxHash", "12.0"));
    public final static BigDecimal minHash = new BigDecimal(min);
    public final static BigDecimal maxHash = new BigDecimal(max);
    
    public final static BigDecimal scale = maxHash.subtract(minHash).divide(new BigDecimal(Math.pow(2.0, 127)));
    private final static DoubleToken MINIMUN=new DoubleToken(minHash.doubleValue());
    @Override
    public DecoratedKey<DoubleToken> convertFromDiskFormat(ByteBuffer key) {
        return new DecoratedKey<DoubleToken>(getToken(key), key);
    }

    @Override
    public DecoratedKey<DoubleToken> decorateKey(ByteBuffer key) {
        return new DecoratedKey<DoubleToken>(getToken(key), key);
    }

    @Override
    public Token midpoint(Token left, Token right) {
        double dl = ((DoubleToken) left).token;
        double dr = ((DoubleToken) right).token;
        return new DoubleToken((dl + dr) / 2);
    }

    @Override
    public DoubleToken getMinimumToken() {
        return MINIMUN;
    }

    @Override
    public DoubleToken getToken(ByteBuffer key) {
        if (key.remaining() == 0) {
            return MINIMUN;
        }
        if (key.remaining() != 8) {

            return hashToken(key);
        }
        Double dkey;
        try {
            dkey = ByteBufferUtil.toDouble(key);
            if(dkey<min||dkey>max){
                return hashToken(key);
            }
        } catch (Exception e) {
            return hashToken(key);
        }
        return new DoubleToken(dkey);
    }

    private DoubleToken hashToken(ByteBuffer key) {
        BigDecimal d = new BigDecimal(FBUtilities.hashToBigInteger(key));
        
        return new DoubleToken(d.multiply(scale).add(minHash).doubleValue());
    }
   

    @Override
    public DoubleToken getRandomToken() {
        return new DoubleToken((CUtils.random().nextDouble() 
                * maxHash.doubleValue())+minHash.doubleValue());
    }

    @Override
    public TokenFactory<Double> getTokenFactory() {
        return tokenFactory;
    }

    @Override
    public boolean preservesOrder() {
        return true;
    }

    @Override
    public Map<Token, Float> describeOwnership(List<Token> sortedTokens) {
     // allTokens will contain the count and be returned, sorted_ranges is shorthand for token<->token math.
        Map<Token, Float> allTokens = new HashMap<Token, Float>();
        List<Range<Token>> sortedRanges = new ArrayList<Range<Token>>();

        // this initializes the counts to 0 and calcs the ranges in order.
        Token lastToken = sortedTokens.get(sortedTokens.size() - 1);
        for (Token node : sortedTokens)
        {
            allTokens.put(node, new Float(0.0));
            sortedRanges.add(new Range<Token>(lastToken, node));
            lastToken = node;
        }

        for (String ks : Schema.instance.getTables())
        {
            for (CFMetaData cfmd : Schema.instance.getKSMetaData(ks).cfMetaData().values())
            {
                for (Range<Token> r : sortedRanges)
                {
                    // Looping over every KS:CF:Range, get the splits size and add it to the count
                    allTokens.put(r.right, allTokens.get(r.right) + StorageService.instance.getSplits(ks, cfmd.cfName, r, DatabaseDescriptor.getIndexInterval()).size());
                }
            }
        }

        // Sum every count up and divide count/total for the fractional ownership.
        Float total = new Float(0.0);
        for (Float f : allTokens.values()) {
            total += f;
        }
        for (Map.Entry<Token, Float> row : allTokens.entrySet()) {
            allTokens.put(row.getKey(), row.getValue() / total);
        }

        return allTokens;
    }
    private final Token.TokenFactory<Double> tokenFactory = new Token.TokenFactory<Double>() {
        @Override
        public ByteBuffer toByteArray(Token<Double> token) {
            return ByteBufferUtil.bytes(token.token);
        }

        @Override
        public Token<Double> fromByteArray(ByteBuffer bytes) {
            try {
                return new DoubleToken(ByteBufferUtil.toDouble(bytes));
            } catch (BufferUnderflowException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public String toString(Token<Double> token) {
            return token.token.toString();
        }

        @Override
        public Token<Double> fromString(String string) {
            return new DoubleToken(Double.parseDouble(string));
        }

        @Override
        public void validate(String token) throws ConfigurationException {
            try {
                Double.parseDouble(token);
            } catch (NumberFormatException n) {
                throw new ConfigurationException("Invalid double number format", n);
            }
        }
    };
}
