/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.commons;

import org.apache.commons.configuration.Configuration;

/**
 * Used basically to easy testing. Use injection where you can.
 *
 * @author ccugnasc
 */
public interface Configurable {

    @Deprecated
    public void setConf(Configuration c);

    @Deprecated
    public Configuration getConf();
}
