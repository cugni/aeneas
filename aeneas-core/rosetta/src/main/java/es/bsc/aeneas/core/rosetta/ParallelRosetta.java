package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.rosetta.exceptions.TimeoutException;
import es.bsc.aeneas.core.model.gen.ClusterType;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.model.gen.EnvType;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;
import org.springframework.context.ApplicationContext;

/**
 * *
 *
 * @author ccugnasc
 */
public class ParallelRosetta implements Rosetta {

    private BiMap<ClusterHandler, PathMatchMap> clusterHandlers = HashBiMap.create();
    @Inject
    private Configuration conf;
    @Inject
    EnvType env;
    @Inject
    RootType root;
    private static Logger log = Logger.getLogger(ParallelRosetta.class.getName());
    private ListeningExecutorService service;
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        this.context = ac;
    }

    @Override
    public void init() throws UnreachableClusterException {
        checkArgument(env.getCluster().size() > 1,
                "If you have just one clustere use SimpleRosetta instead of the Parallel one");

        checkArgument(env.getCluster().size() != clusterHandlers.size(),
                "There are not cluster handler for each cluster");


        for (ClusterType ct : env.getCluster()) {
            ClusterHandler handler = context.getBean(ct.getHandlerName(), ClusterHandler.class);
            //Initing the handler
            handler.init(ct);
            clusterHandlers.put(handler, new PathMatchMap(root, ct));

        }
        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    }

    @Override
    public Result queryAll(CrudType crud, Object... path) throws TimeoutException {
        final CountDownLatch running = new CountDownLatch(clusterHandlers.size());
        final List<Result> r = Collections.synchronizedList(new ArrayList());
        for (Map.Entry<ClusterHandler, PathMatchMap> ch : clusterHandlers.entrySet()) {
            for (PathMatch matches : ch.getValue().getPathMatches(crud, path)) {
                ListenableFuture<Result> submit = service.submit(
                        ch.getKey().query(crud, matches.getId(), matches.split(path)));
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

        }
        try {
            if (!running.await(conf.getInt("rosetta.timeout", 5 * 60), TimeUnit.SECONDS)) {
                throw new TimeoutException("Maximum query time exceeded");

            } else {
                return Result.mergeAll(r);
            }
        } catch (InterruptedException ex) {
            throw new TimeoutException("lock interrupted", ex);
        }

    }

    @Override
    public Result getMatching(CrudType crud, String url) {
        throw new RuntimeException("To be implemented");
    }
}
