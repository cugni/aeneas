/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.exceptions.TimeoutException;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;

/**
 *
 * @author ccugnasc
 */
public interface Rosetta {

    Result getMatching(CrudType crud, String url);

    void init()  throws UnreachableClusterException;

    Result queryAll(CrudType crud, Object... path) throws   TimeoutException  ;
    
}
