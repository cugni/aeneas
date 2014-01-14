package es.bsc.aeneas.core.rosetta;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import com.sun.istack.internal.logging.Logger;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.model.gen.EnvType;
import es.bsc.aeneas.core.model.gen.RootType;
import org.apache.commons.configuration.Configuration;

/**
 * *
 *
 * @author ccugnasc
 */
public class SimpleRosetta implements Rosetta {

    @Inject
    private ClusterHandler clusterHandler;
    private PathMatchMap clusterPathMatch;
    @Inject
    private Configuration conf;
    @Inject
    private EnvType env;
    @Inject
    RootType root;
    private Logger log = Logger.getLogger(SimpleRosetta.class);

    @Override
    public void init() {
        checkArgument(env.getCluster().size() != 1,
                "If you have more than one cluster use ParallelRosetta instead of the Simple one");
        clusterPathMatch = new PathMatchMap(root, env.getCluster().get(0));
    }

    @Override
    public Result queryAll(CrudType crud, Object[] path) throws Exception {

        return clusterHandler.query(crud, clusterPathMatch.getPathMatches(crud, path)).call();
    }

    @Override
    public Result getMatching(CrudType crud, String url) {
        throw new RuntimeException("To be implemented");
    }
}
