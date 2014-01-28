/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.repositories;

import es.bsc.aeneas.core.nodeagent.model.Samples;

/**
 *
 * @author ccugnasc
 */
public interface SamplesRepo {

    /**
     * The method returns the fist sample for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @return
     */
    Samples getFistSamples(String test, String context, String metric, String node);

    /**
     * The method returns the fist sample after
     * <code>from</code>, for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @param from
     * @return
     */
    Samples getFistSamples(String test, String context, String metric, String node, Long from);

    /**
     * The method returns the last sample for the provided parameters.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @return
     */
    Samples getLastSamples(String test, String context, String metric, String node);

    /**
     * The method returns the last sample before
     * <code>from</code>, for the provided parameters.
     *
     * @param test
     * @param metric
     * @param node
     * @param from
     * @return
     */
    Samples getLastSamples(String test, String context, String metric, String node, Long from);

    /**
     * This method retrieves all the samples for the provide metric, test and
     * node.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @return an object Samples containing the result
     */
    Samples getSamples(String test, String context, String metric, String node);

    /**
     * This method retrieves at most #results samples for the provide metric,
     * test and node where the samples are older then tfrom.
     *
     * @param test
     * @param context
     * @param metric
     * @param node
     * @param tfrom
     * @param results
     * @return an object Samples containing the result
     */
    Samples getSamples(String test, String context, String metric, String node, long tfrom, Integer results);
    
}
