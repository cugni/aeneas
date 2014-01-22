package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.rosetta.exceptions.TimeoutException;
import es.bsc.aeneas.core.model.gen.ClusterType;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sun.istack.internal.logging.Logger;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.model.gen.EnvType;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.model.util.GenUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;

/**
 * *
 *
 * @author ccugnasc
 */
public class ParallelRosetta implements Rosetta {

    @Inject
    private Map< Class<? extends ClusterType>, ClusterHandler> clusterHandlers;
    private Map< Class<? extends ClusterType>, PathMatchMap> clusterPathMatches;
    @Inject
    private Configuration conf;
    @Inject
    private EnvType env;
    @Inject
    RootType root;
    private Logger log = Logger.getLogger(ParallelRosetta.class);
    private static ListeningExecutorService service;

    @Override
    public void init() {
        checkArgument(env.getCluster().size() > 1,
                "If you have just one clustere use SimpleRosetta instead of the Parallel one");

        checkArgument(env.getCluster().size() != clusterHandlers.size(), "There are not cluster handler for each cluster");
        checkArgument(!clusterHandlers.isEmpty(), "There are not cluster handler declared");

        for (ClusterType ct : env.getCluster()) {
            //TODO check here. 
            clusterPathMatches.put(ct.getClass(), new PathMatchMap(root, ct));

        }


        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()); 

    }

    private Collection<PathMatch> clusterMatching(CrudType crud, Object[] path) {
        List<PathMatch> l = new ArrayList();
        for (PathMatchMap pmm : clusterPathMatches.values()) {
            l.addAll(pmm.getPathMatches(crud, path));
        }
        return l;
    }

    @Override
    public Result queryAll(CrudType crud, Object[] path) throws Exception {

        final CountDownLatch running = new CountDownLatch(clusterHandlers.size());
        final List<Result> r = Collections.synchronizedList(new ArrayList());
        for (ClusterHandler ch : clusterHandlers.values()) {
            ListenableFuture<Result> submit = service.submit(
                    ch.query(crud,
                    clusterMatching(crud, path)));
            Futures.addCallback(submit, new FutureCallback<Result>() {
                @Override
                public synchronized void onSuccess(Result result) {
                    running.countDown();
                    r.add(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    log.log(Level.SEVERE, "Exception thrown in one of the threads", t);
                    running.countDown();
                    t.printStackTrace(System.out);
                    System.out.println(t.getMessage());
                    Result res = new Result();
                    res.setSuccess(false);
                    res.getMeta().put("error.message", t.getMessage());

                }
            });

        }

        if (!running.await(conf.getInt("rosetta.timeout", 5 * 60), TimeUnit.SECONDS)) {
            throw new TimeoutException("Maximum query time exceeded");

        } else {
            return Result.mergeAll(r);
        }



    }

    @Override
    public Result getMatching(CrudType crud, String url) {
        throw new RuntimeException("To be implemented");
    }
}
