/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.exceptions.TimeoutException;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author ccugnasc
 */
public interface Rosetta extends ApplicationContextAware {

    Result getMatching(CrudType crud, String url);

    void init()  throws UnreachableClusterException;

    Result queryAll(CrudType crud, Object... path) throws   TimeoutException  ;
    
}
