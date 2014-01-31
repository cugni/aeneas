/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent;

import es.bsc.aeneas.commons.Configurable;
import org.apache.commons.configuration.Configuration;

import java.util.Collection;
import javax.inject.Inject;

/**
 *
 * @author ccugnasc
 */
public interface Recorder extends Configurable {

    /**
     * At every call of this method it retrieve metrics from the testingnode and
     * store them in the reporting server. All metrics are recording using the
     * same time
     *
     * 
     */
   
    void store(Metric m);

    void store(Collection<Metric> m);

    void start();

    void stop();

    /**
     * it configure the name of the running test. It is a synchronized method
     *
     */
    void configure();
}
