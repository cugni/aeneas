/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import java.util.concurrent.Callable;

/**
 *
 * @author ccugnasc
 */
public interface ClusterHandler {

    public void init(ClusterType clusterType) throws UnreachableClusterException;
    
    public void close();

    public Callable<Result> query(CrudType ct,String matchid, ImmutableList<Mapping> match);
}
