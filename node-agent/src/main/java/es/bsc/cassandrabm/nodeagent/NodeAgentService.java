/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowire;

/**
 *
 * @author ccugnasc
 */
public abstract class NodeAgentService extends AbstractScheduledService implements Service {

    @Inject
    protected Configuration conf;

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }
 

    public abstract String getErrorsStackTrace();

    public abstract List<Recorder> getRecorders();

    @Override
    public abstract void runOneIteration() throws Exception;

    public abstract void setRecorders(List<Recorder> recorders);

    public abstract void configure() throws Exception;

    public abstract void startSampling() throws Exception;

    public abstract void stopSampling() throws Exception;

    public abstract boolean isSampling();

    

    public abstract void reportException(Throwable t);

    public abstract List<MetricsReader> getMetricsReaders();

    public abstract void setMetricsReaders(List<MetricsReader> mreaders);
}
