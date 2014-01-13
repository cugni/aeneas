/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 *
 * @author ccugnasc
 */
public interface ClusterHandler {

    public void init(ClusterType clusterType);

    public Callable<Result> query(CrudType ct, Collection<PathMatch> match);
}
