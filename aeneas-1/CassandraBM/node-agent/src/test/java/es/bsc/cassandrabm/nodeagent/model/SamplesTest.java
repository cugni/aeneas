/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.model;

import java.util.Arrays;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class SamplesTest {

    public SamplesTest() {
    }

    @Test
    public void testSerialize() throws Exception {
        ObjectMapper m = new ObjectMapper();
        Samples s = new Samples( "test","clusternode", "metric","minerva", Arrays.asList(213321L), Arrays.asList(123.2));
        assertTrue(m.canSerialize(Samples.class));
        assertEquals("serialization", "{\"test\":{\"context\":\"clusternode\",\"name\":\"test\"},\"metric\":\"metric\","
                + "\"dates\":[213321],\"values\":[123.2]}", m.writeValueAsString(s));
    }

    @Test
    public void testDeserialize() throws Exception {
        ObjectMapper m = new ObjectMapper();
        Samples s = new Samples( "test","clusternode", "metric","minerva", Arrays.asList(213321L), Arrays.asList(123.2));
        assertTrue(m.canSerialize(Samples.class));
        assertEquals("serialization", s, m.readValue("{\"test\":{\"context\":\"clusternode\",\"name\":\"test\"},\"metric\":\"metric\","
                + "\"dates\":[213321],\"values\":[123.2]}", Samples.class));
    }
    
    
     @Test
    public void testMultiSerialize() throws Exception {
        ObjectMapper m = new ObjectMapper();
        Samples s = new Samples( "test","clusternode", "metric","minerva", 
                 Arrays.asList(213321L,1213L), Arrays.asList(123.2,"ciao"));
        assertTrue(m.canSerialize(Samples.class));
        assertEquals("serialization", "{\"test\":{\"context\":\"clusternode\",\"name\":\"test\"},\"metric\":\"metric\","
                + "\"dates\":[213321,1213],\"values\":[123.2,\"ciao\"]}", m.writeValueAsString(s));
    }

    @Test
    public void testMultiDeserialize() throws Exception {
        ObjectMapper m = new ObjectMapper();
        Samples s = new Samples( "test","clusternode", "metric","minerva", 
                Arrays.asList(213321L,1213L), Arrays.asList(123.2,"ciao"));
        assertTrue(m.canSerialize(Samples.class));
        assertEquals("serialization", s, m.readValue("{\"test\":{\"context\":\"clusternode\",\"name\":\"test\"},\"metric\":\"metric\","
             + "\"dates\":[213321,1213],\"values\":[123.2,\"ciao\"]}", Samples.class));
    }
}