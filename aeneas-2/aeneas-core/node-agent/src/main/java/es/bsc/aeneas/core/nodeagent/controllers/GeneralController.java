/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.controllers;

import es.bsc.aeneas.commons.Configurable;
import es.bsc.aeneas.core.nodeagent.NodeAgentService;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ccugnasc
 */
@Controller("NodeAgent")
@RequestMapping("api/")
public class GeneralController implements Configurable {

    private static final Logger log = Logger.getLogger(GeneralController.class.getName());
    @Inject
    private NodeAgentService nodeAgent;
    private final String testingnode;
    private Configuration conf;

    @Inject
    public GeneralController(Configuration conf) {
        this.conf = conf;
        try {
            String tn = conf.getString("testingnode", null);
            if (tn == null) {
                tn = InetAddress.getLocalHost().getHostName();
                conf.setProperty("testingnode", tn);
            }
            testingnode = tn;

        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible to determine the localhost name."
                    + "Set it manually through the testingnode parameter", ex);
        }

    }

    /**
     * This is the method which return the implementation of the Init action.
     * The {@link ClientAction } implemented throws and Exception if: - the
     * sampling was already running - if it is not provided the parameters <i>
     * serverlocation, clustername, deviceregexp and the columns</i>
     *
     *
     * @param columns
     * @return the ClientAction implementation for the Init Action
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/init")
    public @ResponseBody
    String init(@RequestParam("columns") String columns) throws Exception {      
        if (nodeAgent.isSampling()) {
            log.warning("Invoked init while the server were already running");
            throw new Exception("The server is already runnig");
            
        }
        log.log(Level.INFO,"Invoked init with columns {0}",columns);
        conf.addProperty("columns", columns);
        //TODO fix it
        conf.addProperty("testingnode", testingnode);
        nodeAgent.configure();
        return "server configurated\n";

    }

    /**
     *
     * @param interval
     * @param timeunit
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/set")
    public @ResponseBody
    String set(
            @RequestParam(value = "interval", required = false) Integer interval,
            @RequestParam(value = "timeunit", required = false) String timeunit) throws Exception {
        StringBuilder sb = new StringBuilder(20);

        if (interval != null) {
            conf.addProperty("scheduler.interval", interval);
            sb.append("condigured interval to ").append(interval).append("\n");
            log.log(Level.INFO,"Called set : schedular.interval = {0} ",interval);
        }
        if (timeunit != null) {
            TimeUnit timeout = TimeUnit.valueOf(timeunit.toUpperCase());
            conf.addProperty("scheduler.timeunit", timeout);
            sb.append("condigured timeunit to ").append(timeout.name()).append("\n");
           log.log(Level.INFO,"Called set : schedular.timeunit = {0} ",timeout);

        }
        nodeAgent.configure();
        return sb.append("parameters changed\n").toString();
    }

    /**
     * This is the method which return the implementation of the Start action.
     * The {@link ClientAction } implemented throws and Exception if: - the
     * sampling was already running - if it has not already invoked the
     * <i>init</i> action - if the request doesn't contain the parameter
     * testname It is also possible to set the the interval between two samples.
     *
     * @param testname
     * @return the ClientAction implementation for the Set Action
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/start")
    public @ResponseBody
    String startSampling(@RequestParam(value = "testname") String testname) throws Exception {
        try {
            conf.setProperty("testname", testname);
            nodeAgent.configure();
            nodeAgent.startSampling();
            log.log(Level.INFO,"Called startSampling for testname {0}",testname);
            return String.format("server running on the test %s \n", testname);
        } catch (Throwable t) {
            nodeAgent.reportException(t);
            log.log(Level.WARNING,"Exception calling tartSampling for testname "+testname,t);
            throw new Exception("Impossible to start.\n" + nodeAgent.getErrorsStackTrace(), t);

        }
    }

//    /*@RequestMapping(method = RequestMethod.GET, value = "/")
//    public String index() throws Exception {
//        return "index";
//    }*/
    /**
     *
     *
     *
     * This is the method which return the implementation of the action stop.
     * The {@link ClientAction
     * } implemented throws and Exception if the sampling was already stopped.
     *
     *
     */
    @RequestMapping(method = RequestMethod.GET, value = "/stop")
    public @ResponseBody
    String stopSampling() throws Exception {
        nodeAgent.stopSampling();
        log.log(Level.WARNING,"Sampling stopped for test {0}",conf.getString("testname"));
        return "metrics recording stopped\n";

    }

    /**
     *
     *
     *
     * This is the method which return the implementation of the Status action.
     * The {@link ClientAction
     * } implemented print in a string the Nodeagent status,
     *
     * @return the ClientAction implementation for the Status Action
     */
    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public @ResponseBody
    String status() throws Exception {
        return (nodeAgent.isSampling() ? "the server is running" : "the server is stopped")
                + "\ntestname: " + conf.getString("testname")
                + "\ninterval: " + conf.getString("scheduler.interval")
                + "\ntimeunit: " + conf.getString("scheduler.timeunit")
                + "\nerrors:\n" + nodeAgent.getErrorsStackTrace() + "\n";
    }

    @Override
    public void setConf(Configuration c) {
        this.conf = c;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
