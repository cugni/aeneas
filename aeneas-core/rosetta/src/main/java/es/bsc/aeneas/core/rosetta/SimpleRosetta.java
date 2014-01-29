package es.bsc.aeneas.core.rosetta;

import javax.inject.Inject;

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
import org.apache.commons.configuration.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * *
 *
 * @author ccugnasc
 */
public class SimpleRosetta implements Rosetta {

    private ClusterHandler clusterHandler;
    private PathMatchMap clusterPathMatch;
    private Logger log = Logger.getLogger(SimpleRosetta.class.getName());

    @Override
    public void init() throws UnreachableClusterException {
        ApplicationContext context = new ClassPathXmlApplicationContext("/rosetta-context.xml");
        //Selfwiring
        EnvType env = context.getBean(EnvType.class);
        RootType root = context.getBean(RootType.class);
        checkArgument(env.getCluster().size() != 1,
                "If you have more than one cluster use ParallelRosetta instead of the Simple one");
        ClusterType clusterType = env.getCluster().get(0);
        clusterPathMatch = new PathMatchMap(root, clusterType);
        clusterHandler = context.getBean(clusterType.getHandlerName(), ClusterHandler.class);
        clusterHandler.init(clusterType);
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
}
