package es.bsc.aeneas.core.rosetta;

import static com.google.common.base.Preconditions.checkArgument;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.model.gen.EnvType;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.rosetta.exceptions.TimeoutException;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.springframework.context.ApplicationContext;

public class SimpleRosetta implements Rosetta {

    private static final Logger log = Logger.getLogger(SimpleRosetta.class.getName());
    private ClusterHandler clusterHandler = null;
    private PathMatchMap clusterPathMatch = null;
    @Inject
    private ApplicationContext context;
    @Inject
    private EnvType env;
    @Inject
    private RootType root;

    @Override
    public void init() throws UnreachableClusterException {

        checkArgument(env.getCluster().size() != 1,
                "If you have more than one cluster use ParallelRosetta instead of the Simple one");
        ClusterType clusterType = env.getCluster().get(0);
        clusterPathMatch = new PathMatchMap(root, clusterType);
        clusterHandler = context.getBean(clusterType.getHandlerName(), ClusterHandler.class);
        clusterHandler.init(clusterType);
        log.log(Level.INFO, "Configured cluster handler {0}", clusterType.getName());
    }

    void setContext(ApplicationContext context) {
        this.context = context;
    }

    void setEnv(EnvType env) {
        this.env = env;
    }

    void setRoot(RootType root) {
        this.root = root;
    }

    @Override
    public Result queryAll(CrudType crud, Object... path) throws TimeoutException {
        Collection<PathMatch> pathMatches = clusterPathMatch.getPathMatches(crud, path);
        Result r = new Result();
        for (PathMatch pm : pathMatches) {
            try {
                r = r.merge(clusterHandler.query(crud, pm.getId(), pm.split(path)).call());
            } catch (Exception ex) {
                throw new TimeoutException("Exception in the Callable", ex);
            }
            log.log(Level.FINE, "received result of query with path {0}", path);
        }
        return r;
    }

    @Override
    public Result getMatching(CrudType crud, String url) {
        throw new RuntimeException("To be implemented");
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        this.context = ac;
    }
}
