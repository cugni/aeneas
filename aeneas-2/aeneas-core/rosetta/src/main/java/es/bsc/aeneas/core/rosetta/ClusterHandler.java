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

    public ClusterType getClusterType();
    
    /**
     * This is the phase where the ClusterHandler receives the ClusterType object
     * and thus it starts to initialize its internal structure and connects to
     * the cluster. 
     * @param clusterType the ClusterType parsed from the XML configuration file
     * @throws UnreachableClusterException In case it is not possible to connect 
     * the cluster
     */
    public void init(ClusterType clusterType) throws UnreachableClusterException;
    
    public void close();

    public Callable<Result> query(CrudType ct,String matchid, ImmutableList<Mapping> match);
}
