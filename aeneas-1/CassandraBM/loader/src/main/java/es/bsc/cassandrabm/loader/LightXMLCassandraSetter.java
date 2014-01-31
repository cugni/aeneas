package es.bsc.cassandrabm.loader;

import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.loader.exceptions.InvalidGetRequest;
import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import es.bsc.cassandrabm.loader.exceptions.NotSupportedQuery;
import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;
import es.bsc.cassandrabm.model.Ks;
import es.bsc.cassandrabm.model.gen.RootType;
import es.bsc.cassandrabm.model.util.GenUtils;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.commons.configuration.Configuration;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class LightXMLCassandraSetter extends AbstractCassandraDB {

    private static final Logger log = Logger.getLogger(LightXMLCassandraSetter.class.getName());
    private final HConsistencyLevel readconsistency =
            HConsistencyLevel.valueOf(
            CUtils.getString("readconsistency", HConsistencyLevel.QUORUM.name()));
    private final HConsistencyLevel writeconsistency =
            HConsistencyLevel.valueOf(CUtils.getString("writeconsistency", HConsistencyLevel.ANY.name()));
    private final boolean autoDiscoverHosts = CUtils.getBoolean("autoDiscoverHosts", true);
    private final Ks ks;
    private PathMatchMap pathTempl = null;
    private RootType root;
    private volatile Keyspace keyspace;
    private volatile boolean close = false;
    private KeyspaceDefinition kd = null;

    public LightXMLCassandraSetter(String cassandraModelName) {
        this(GenUtils.getCassandraModelFile(cassandraModelName));
    }

    public LightXMLCassandraSetter(URL cassandraModel) {

//        checkArgument(cassandraModel.exists(),
//                "Cassandra model file not found: {0}", cassandraModel.getAbsolutePath());
        ks = GenUtils.getCassandraModel(cassandraModel);
        root = GenUtils.getReferenceModel();

    }

    public LightXMLCassandraSetter(String cassandraModelName, String referenceModel) {
        GenUtils.setRefmodel(referenceModel);
        root = GenUtils.getReferenceModel();
        ks = GenUtils.getCassandraModel(cassandraModelName);

    }

    @Override
    public Object testGet(Object value, Object... path) throws InvalidGetRequest, NotSupportedQuery {


        Set<PathMatch> templateMatch = pathTempl.getTemplateMatch(path);
        if (templateMatch.isEmpty()) {
            throw new NotSupportedQuery("No path match found");
        }
        Object res = null;
        for (PathMatch pm : templateMatch) {
            res = pm.testGet(value, path);

        }
        if (res != null) {
            return res;
        }
        log.log(Level.WARNING, "Impossible read the value {0} with the path {1}",
                new Object[]{value, Arrays.toString(path)});
        throw new NotSupportedQuery("Impossible to retrieve the data");


    }

    /**
     * In this implementations if the keyspace already exists it is not modified
     *
     * @param sets
     */
    @Override
    public void configure() {
//        if (checkNotNull(cluster, "Is required to call the open method before the configure one")
//                .describeKeyspace(ks.name) != null) {
//            cluster.dropKeyspace(ks.name, true);
//        }
//        //Add the schema to the cluster.
//        //"true" as the second param means that Hector will block until all nodes see the change.
//        cluster.addKeyspace(getKeyspaceDefinition(), true);
        if (checkNotNull(cluster, "Is required to call the open method before the configure one")
                .describeKeyspace(ks.name) == null) {
            cluster.addKeyspace(getKeyspaceDefinition(), true);
        }
        //Add the schema to the cluster.
        //"true" as the second param means that Hector will block until all nodes see the change.


    }

    @Override
    public KeyspaceDefinition getKeyspaceDefinition() {
        if (kd != null) {
            return kd;
        }
        kd = ks.createKeyspaceDefinition();



        return kd;
    }

    @Override
    public void put(Object value, Object... path) throws InvalidPutRequest {
        checkNotNull(keyspace, "Invoke open() before use this method");
        //log.log(Level.INFO, "[grepme]Put on value {0} with {1} path", new Object[]{value, Arrays.toString(path)});
        Set<PathMatch> templateMatch =
                checkNotNull(pathTempl, "Invoke open() before use this method").getTemplateMatch(path);
        if (templateMatch.isEmpty()) {
            log.log(Level.FINE, "path without match {}", Arrays.toString(path));
            return;
        }
        for (PathMatch pm : templateMatch) {
            pm.pinsert(value, path);
        }
    }

    public Keyspace getKeyspace() {
        return checkNotNull(keyspace,
                "Invoke DBSource.open() before call this method");
    }

    @Override
    public void close() {
        super.close();
        close = true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void open(String clusterName, String location) throws UnreachableClusterException {
        super.open(clusterName, location);

        ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
        Map<String, HConsistencyLevel> clread = new HashMap<String, HConsistencyLevel>(4);
        Map<String, HConsistencyLevel> clwrite = new HashMap<String, HConsistencyLevel>(4);
        for (ColumnFamilyDefinition cfd : getKeyspaceDefinition().getCfDefs()) {
            clread.put(cfd.getName(), readconsistency);
            clwrite.put(cfd.getName(), writeconsistency);
        }
        configurableConsistencyLevel.setReadCfConsistencyLevels(clread);
        configurableConsistencyLevel.setWriteCfConsistencyLevels(clwrite);

        CassandraHostConfigurator conf = new CassandraHostConfigurator();
        conf.setAutoDiscoverHosts(autoDiscoverHosts);
        keyspace = HFactory.createKeyspace(getKeyspaceDefinition().getName(),
                cluster, configurableConsistencyLevel);

        pathTempl = new PathMatchMap(ks, keyspace, root);

    }

    PathMatchMap getPathMatchMap() {
        return pathTempl;
    }

    public boolean isClose() {
        return close;
    }
}
