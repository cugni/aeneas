/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent;

import com.sun.net.httpserver.HttpServer;
import java.util.logging.Logger;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.mortbay.jetty.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ccugnasc
 */
public class NodeAgentDaemon implements Daemon {

    private static final Logger log = Logger.getLogger("NodeAgentDaemon");

    public static void main(String[] args) throws Exception {
        NodeAgentDaemon nad = new NodeAgentDaemon();
        nad.init(null);
        nad.start();


    }
    public final ApplicationContext jettycontext = new ClassPathXmlApplicationContext(
            "META-INF/jetty-context.xml");
    public final Server httpserver = jettycontext.getBean(Server.class);

    /**
     * Required by the interface but actually it do nothing.
     *
     * @param dc
     * @throws Exception
     */
    @Override
    public void init(DaemonContext dc) throws Exception {
    }

    /**
     * It starts an HTTP server on the default {
     *
     * @httplistenport} port number.
     * @throws Exception It throws and Exception in case the default port is not
     * available
     */
    @Override
    public void start() throws Exception {
        log.info("Starting the Node Agent Daemon");
        httpserver.start();




    }

    /**
     * It turn off the {@link HttpServer}
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        log.info("Stopping the Node Agent Daemon");
        httpserver.stop();    }

    /**
     * Required by the interface but actually it does nothing
     */
    @Override
    public void destroy() {
    log.info("Destroying  the Node Agent Daemon");
        httpserver.destroy();


    }
}
