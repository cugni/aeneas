/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.core.rosetta.exceptions.InvalidPutRequest;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import org.apache.commons.configuration.Configuration;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ccugnasc
 */
public class LoggingDBSetter implements DBSetter {

    private static final Logger log = Logger.getLogger(LoggingDBSetter.class.getName());

    @Override
    public void configure() {
        log.info("Called the method configure");
    }

    @Override
    public void put(Object value, Object... path) throws InvalidPutRequest {
        log.log(Level.INFO,
                "Called the method put with the value {0} and the path {1}", new Object[]{value, Arrays.toString(path)});

    }

    @Override
    public void open(String clusterName, String location) throws UnreachableClusterException {
        log.log(Level.INFO,"Called the method open for the cluster {0} in the location {1}",
                new Object[]{
                clusterName, location});
    }

    @Override
    public void close() {
        log.info("Called the method close");
    }
}
