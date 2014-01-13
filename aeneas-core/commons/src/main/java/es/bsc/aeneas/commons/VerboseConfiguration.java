/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.commons;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author ccugnasc
 */
public class VerboseConfiguration extends CompositeConfiguration {

    private static final Logger log = Logger.getLogger(VerboseConfiguration.class.getName());

    public VerboseConfiguration() {
    }

    public VerboseConfiguration(Configuration inMemoryConfiguration) {
        super(inMemoryConfiguration);
    }

    public VerboseConfiguration(Collection configurations) {
        super(configurations);
    }

    public VerboseConfiguration(Configuration inMemoryConfiguration, Collection configurations) {
        super(inMemoryConfiguration, configurations);
    }

    @Override
    public Float getFloat(String name, Float defval) {

        try {
            Float ret = super.getFloat(name);
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        } catch (NoSuchElementException e) {
            log.log(Level.WARNING, "Using the default value for {0} property: {1}",
                    new Object[]{name, defval});
            return defval;
        }
    }

    @Override
    public Object getProperty(String name) {
        try {
            Object ret = super.getProperty(name);
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        } catch (NoSuchElementException e) {
            log.log(Level.WARNING, "property {0} not configured", name);
            return null;
        }

    }

    public Object getProperty(String name, Object defval) {
        try {
            Object ret = super.getProperty(name);
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        } catch (NoSuchElementException e) {
            log.log(Level.WARNING, "Using the default value for {0} property: {1}",
                    new Object[]{name, defval});
            return defval;
        }

    }

    @Override
    public String getString(String name) {
        try {
            String ret = super.getString(name);
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        } catch (NoSuchElementException e) {
            log.log(Level.WARNING, "property {0} not configured", name);
            return null;
        }

    }

    @Override
    public String getString(String name, String defval) {
        String ret = super.getString(name, null);
        if (ret == null) {
            log.log(Level.WARNING, "Using the default value for {0} property: {1}",
                    new Object[]{name, defval});
            return defval;
        } else {
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        }


    }

    @Override
    public Boolean getBoolean(String name, Boolean defval) {

        Boolean ret = super.getBoolean(name, null);
        if (ret == null) {
            log.log(Level.WARNING, "Using the default value for {0} property: {1}",
                    new Object[]{name, defval});
            return defval;
        } else {
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        }


    }

    public Integer getInt(String name, Integer defval) {

        try {
            Integer ret = super.getInt(name);
            log.log(Level.INFO, "{0} property set to {1}",
                    new Object[]{name, ret});
            return ret;
        } catch (NoSuchElementException e) {
            log.log(Level.WARNING, "Using the default value for {0} property: {1}",
                    new Object[]{name, defval});
            return defval;
        }


    }
}
