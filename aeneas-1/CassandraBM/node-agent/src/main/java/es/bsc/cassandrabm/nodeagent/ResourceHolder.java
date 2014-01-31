/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ccugnasc
 */
public class ResourceHolder {

    private static final Logger LOG = Logger.getLogger(ResourceHolder.class.getName());

    public static ResourceHolder getResourceHolder() {
        return HoldMeLazily.holder;

    }
    public final boolean amIjar;
    private final String basedir;

    private ResourceHolder() {
        String me = ResourceHolder.class.getResource("ResourceHolder.class").toString();
        LOG.log(Level.INFO, "This class path  {0}", me);
        amIjar = me.contains(".jar!");
        LOG.log(Level.INFO, "Running in a jar : {0}", amIjar);
        if (amIjar) {
            basedir = me.substring(0, me.indexOf(".jar!")) + ".jar!/";
        } else {
            basedir = "src/main/";

        }
        LOG.log(Level.INFO, "Base dir : {0}", basedir);


    }

    public String getWebappDir() {
        return basedir + "webapp/";
    }

    public String getResourceDir() {
        if (amIjar) {
            return basedir;
        } else {
            return basedir + "resources/";
        }
    }

    public URL getResource(String name) {

        try {
            if (amIjar) {

                return new URL(basedir + name);

            } else {
                return ClassLoader.getSystemResource(name);
            }
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Malformed url", ex);
        }
    }

    private static class HoldMeLazily {

        private static ResourceHolder holder = new ResourceHolder();

        private HoldMeLazily() {
        }
    }
}
