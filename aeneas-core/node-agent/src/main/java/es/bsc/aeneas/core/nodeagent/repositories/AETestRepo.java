/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.repositories;

import es.bsc.aeneas.core.nodeagent.model.AETest;
import java.util.List;

/**
 *
 * @author ccugnasc
 */
public interface AETestRepo {

    List<AETest> getTests(String context, String from) throws Exception;
    
}
