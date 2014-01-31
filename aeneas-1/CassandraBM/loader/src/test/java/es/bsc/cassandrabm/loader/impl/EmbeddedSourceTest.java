/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.loader.impl;

import es.bsc.cassandrabm.loader.DBSetter;
import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;
import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesare
 */
public class EmbeddedSourceTest {
    
    public EmbeddedSourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

  

    @Test
    public void testCall() throws Exception {
        EmbeddedSource es=new EmbeddedSource();
        es.open(new File("./test.txt"));
        FileWriter f=new FileWriter(File.createTempFile("test", ".txt"));
        final BufferedWriter bw = new BufferedWriter(f);
        
        es.setDBSetter(new DBSetter() {

          

            @Override
            public void put(Object value, Object... path) throws InvalidPutRequest {
                try {
                    bw.append(Arrays.toString(path)+"-->"+value+"\n");
                } catch (IOException ex) {
                    Logger.getLogger(EmbeddedSourceTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void open(String clusterName, String location) throws UnreachableClusterException {
                 
            }

            @Override
            public void close() {
                 
            }

            @Override
            public void configure(
                    ) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        es.call();
    }
}
