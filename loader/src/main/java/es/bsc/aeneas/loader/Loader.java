/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.loader;

import com.google.common.util.concurrent.*;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import es.bsc.aeneas.commons.CUtils;
import es.bsc.aeneas.loader.exceptions.UnreachableClusterException;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesare
 */
public class Loader {

    public final static int NUMBER_OF_THREADS;
    private final static Logger log = Logger.getLogger(Loader.class.getName());
    private final static Counter daemonalive = Metrics.newCounter(Loader.class, "daemon-alive");

    static {
        NUMBER_OF_THREADS = CUtils.getInt("numberofthreads",Runtime.getRuntime().availableProcessors() * 2 + 1 );
        log.log(Level.INFO, "Number of threads setted to {0}", NUMBER_OF_THREADS);

    }
    private static Loader loader = null;
    private static ListeningExecutorService service =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public Cluster configureCluster(String clusterName, String location)
            throws UnreachableClusterException {
        log.log(Level.INFO,"connecting to the cluster {0} at {1}",new Object[]{clusterName,location});
        Cluster cluster = HFactory.getOrCreateCluster(clusterName, location);
        String nodes = CUtils.getString("nodes");
        if (nodes != null) {
            StringTokenizer tok = new StringTokenizer(nodes, ";");
            while (tok.hasMoreTokens()) {
                String node = tok.nextToken();
                cluster.getConnectionManager().addCassandraHost(new CassandraHost(node));
                log.log(Level.INFO, "Adding the cassandra node {0}", node);
            }
        }
//        cluster.getConnectionManager().setTimer(new SpeedForJOpTimer(Loader.getInstance().clustername));
        if (cluster.getConnectionManager().getActivePools().isEmpty()) {
            throw new UnreachableClusterException();
        }
        return cluster;
    }

    public Cluster configureCluster() throws UnreachableClusterException {

        return configureCluster(clustername,clusterlocation);
    }
    public final String clustername;
    public final String clusterlocation;

    public static Loader getInstance() {
        if (loader == null) {
            loader = new Loader();
        }

        return loader;

    }

    private Loader() {
       clustername = CUtils.getString("clustername","aeneas");
              clusterlocation = CUtils.getString("clusterlocation","localhost:9160");
        

    }

    public CountDownLatch submitConsumerDaemons(List<Callable<Boolean>> calls) {
        final CountDownLatch count = new CountDownLatch(calls.size());
        log.log(Level.INFO,"Initializated {0} daemons",calls.size());
        for (Callable<Boolean> call : calls) {
            daemonalive.inc();
            ListenableFuture<Boolean> submit = service.submit(call);
            Futures.addCallback(submit, new FutureCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {
                    count.countDown();
                    daemonalive.dec();
                }

                @Override
                public void onFailure(Throwable t) {
                    daemonalive.dec();
                    log.log(Level.SEVERE, "Exception thrown in one of the threads", t);
                    count.countDown();
                    t.printStackTrace(System.out);
                    System.out.println(t.getMessage());
                    System.exit(-1);
                }
            });
        }
        return count;
    }

    public void shutdown() {
        service.shutdown();
    }

    public void bruteShutdown() {
        List<Runnable> shutdownNow = service.shutdownNow();
        for (Runnable r : shutdownNow) {
            log.log(Level.FINE, "Sospesed the Runnable {0}", r.toString());
        }
    }
}
