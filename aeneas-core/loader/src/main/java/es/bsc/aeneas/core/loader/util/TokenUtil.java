/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader.util;

import me.prettyprint.cassandra.serializers.IntegerSerializer;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author cesare
 */
public class TokenUtil {

    public static void main(String[] args) {
        int start = 0;
        int finish = 1000;
        byte[] bstart = IntegerSerializer.get().toBytes(start);
        byte[] bfinish = IntegerSerializer.get().toBytes(finish);
        int nserver = 10;
        calculateToken(bstart,  bfinish , nserver);
    }
   static void  calculateToken( byte[]  bstart, byte[]  bfinish ,int nserver){
        checkArgument(bstart.length==bfinish.length);
        int distance=0;
        for(int i=0;i<bstart.length;i++){
            distance+=(bfinish[i]-bstart[i])*255^i;
        }
       int pass=distance/nserver;
       for(int i=0;i<nserver;i++){
          System.out.println(pass*i);
       }
    }
  
}
