/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader;

import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.ClusterHandler;
import es.bsc.aeneas.core.rosetta.Mapping;
import es.bsc.aeneas.core.rosetta.Result;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ccugnasc
 */
public class LoggingDBSetter implements ClusterHandler {

    private static final Logger log = Logger.getLogger(LoggingDBSetter.class.getName());
    private ClusterType clusterType;

    @Override
    public void close() {
        log.info("Called the method close");
    }

    @Override
    public void init(ClusterType clusterType) throws UnreachableClusterException {
        this.clusterType=clusterType;
        log.info("Called the method init");
    }

    @Override
    public Callable<Result> query(CrudType ct, String matchid, ImmutableList<Mapping> match) {

        log.log(Level.INFO,
                "Called the query of type {0} on the match {1} with objects {2}", new Object[]{ct, matchid, Arrays.toString(match.toArray())});
        return new Callable<Result>() {
            @Override
            public Result call() throws Exception {

                return Result.createSuccess();
            }
        };
    }

    @Override
    public ClusterType getClusterType() {
        return clusterType;
    }
}
