package es.bsc.aeneas.cassandra.translator;

import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;

import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.cassandra.mapping.Col;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.Mapping;
import es.bsc.aeneas.core.rosetta.Result;
import java.util.concurrent.Callable;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;

public class LightXMLCassandraSetter extends AbstractCassandraDB {

    private static final Logger log = Logger.getLogger(LightXMLCassandraSetter.class.getName());

    /**
     * In this implementations if the keyspace already exists it is not modified
     *
     * @param sets
     */
    @Override
    public void init(ClusterType cd) throws UnreachableClusterException {
        super.init(cd);
        //        if (checkNotNull(cluster, "Is required to call the open method before the configure one")
        //                .describeKeyspace(ks.name) != null) {
        //            cluster.dropKeyspace(ks.name, true);
        //        }
        //        //Add the schema to the cluster.
        //        //"true" as the second param means that Hector will block until all nodes see the change.
        //        cluster.addKeyspace(getKeyspaceDefinition(), true);
        for (KeyspaceDefinition ks : cassa.createKeyspaceDefinitions()) {
            if (checkNotNull(cluster, "Is required to call the open method before the configure one")
                    .describeKeyspace(ks.getName()) == null) {
                cluster.addKeyspace(ks, true);

            }
        }

    }

    @Override
    public void close() {
        super.close();

    }

    @Override
    public Callable<Result> query(CrudType ct,
            final String matchid, final ImmutableList<Mapping> match) {
        if (ct == CrudType.CREATE
                || ct == CrudType.CREATE_OR_UPDATE || ct == CrudType.UPDATE) {
            return new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    Col c = cassa.getMatch(matchid);
                    Object key = c.getKey(match);
                    Object column = c.getColumnName(match);
                    Object value = c.getValue(match);

                    ColumnFamilyUpdater updater = c.cf.template.createUpdater(key);
                    updater.setValue(column, value, c.getValueSerializer());
                    c.cf.template.update(updater);
                    Result r = new Result();
                    r.setSuccess(true);
                    return r;
                }
            };

        } else {
            throw new UnsupportedOperationException("Not Yet implemented");
        }

    }
}
