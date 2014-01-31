/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.repositories;

import es.bsc.aeneas.core.nodeagent.Recorder;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ccugnasc
 */
@Repository
public class WorkspaceRepository {
    @Inject 
    Recorder workspaceRecorder;
    
}
