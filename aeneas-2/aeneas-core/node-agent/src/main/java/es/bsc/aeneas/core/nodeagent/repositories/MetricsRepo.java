/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.repositories;

import es.bsc.aeneas.core.nodeagent.model.AEMetric;
import java.util.List;

/**
 *
 * @author ccugnasc
 */
public interface MetricsRepo {

    List<AEMetric> getMetrics(String context);

    List<AEMetric> getMetrics();

    /**
     * *
     *
     * @param context {
     * @o}
     * @param from
     * @return a list of metrics
     *
     */
    List<AEMetric> getMetrics(String context, String from);
    
}
