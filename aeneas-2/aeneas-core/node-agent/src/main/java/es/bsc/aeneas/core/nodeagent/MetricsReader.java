/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent;

import es.bsc.aeneas.commons.Configurable;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import java.util.concurrent.Callable;
import javax.inject.Inject;

/**
 *
 * @author ccugnasc
 */
public interface MetricsReader extends Callable<List<Metric>>,Configurable {

  

    public void configure() throws Exception;
}
