import me.prettyprint.cassandra.serializers.IntegerSerializer;
import org.apache.cassandra.dht.RandomPartitioner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ccugnasc
 */
public class TokenBalancer {

    static final BigInteger MAX_TOKEN = BigInteger.valueOf(2).pow(127);

    private class Node {

        float ownership;
        BigInteger oldtoken;
        BigInteger newtoken;
    }

    public static void main(String[] args) {

        RandomPartitioner rp = new RandomPartitioner();
        IntegerSerializer is= IntegerSerializer.get();
        
        TreeMap<BigInteger, Integer> keys = new TreeMap();
        for (int i = 0; i < 1000; i++) {
              keys.put(rp.getToken(is.toByteBuffer(i)).token, i);
        }
        //
        //        CSVPrinter p=new CSVPrinter(System.out);
        //        for(BigInteger b:keys.keySet()){
        //            p.print(b.toString());
        //            p.println(keys.get(b).toString());
        //
        //        }
        //
        BigDecimal max = BigDecimal.valueOf(2).pow(127);
        int i=0;
        BigInteger p=null;
        for(BigInteger b:keys.keySet()){
           
            if(i++%125==124){
                BigInteger tok = b.add(p).shiftRight(1);
                System.out.println(tok+"\t"+new BigDecimal(tok).divide(max).multiply(BigDecimal.valueOf(100)));
            }
             p=b;
        }
      
    }
}
