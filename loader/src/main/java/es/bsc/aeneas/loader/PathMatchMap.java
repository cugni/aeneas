/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.loader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSetMultimap;
import es.bsc.aeneas.loader.exceptions.UnreachableClusterException;
import es.bsc.aeneas.cassandra.mapping.CF;
import es.bsc.aeneas.cassandra.mapping.Col;
import es.bsc.aeneas.cassandra.mapping.Ks;
import es.bsc.aeneas.model.gen.ComplexValueType;
import es.bsc.aeneas.model.gen.EntityType;
import es.bsc.aeneas.model.gen.LevelType;
import es.bsc.aeneas.model.gen.RootType;
import es.bsc.aeneas.model.util.GenUtils;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public class PathMatchMap {
    private static final Logger log = Logger.getLogger(PathMatchMap.class.getName());

    public static PathMatchMap createPathMatchMap(URL cassandraModel) throws UnreachableClusterException {
        Ks ks = GenUtils.getCassandraModel(cassandraModel);
        RootType root = GenUtils.getReferenceModel();

        Keyspace k = HFactory.createKeyspace(ks.name,
                Loader.getInstance().configureCluster());
        return new PathMatchMap(ks, k, root);

    }

    private final ImmutableSetMultimap<ImmutableList<String>, PathMatch> referenceMap;
    private final ImmutableMap<String, ColumnFamilyTemplate<Object, Object>> cassandraMap;
    private final RootType root;
    private final ImmutableMap<String, CF> families;
    private boolean close = false;
    private final Ks k;

    public PathMatchMap(Ks k, Keyspace hectorkeyspace, RootType root) {
        this.k = k;
        this.root = root;

        Map<String, ColumnFamilyTemplate<Object, Object>> bcassandraMap = new HashMap(5);
        Builder<String, CF> bfamilies = ImmutableMap.builder();
        ImmutableSetMultimap.Builder<ImmutableList<String>, PathMatch> builder = ImmutableSetMultimap.builder();
        //Creating the template

        for (CF cft : k.cfs) {
            bfamilies.put(cft.name, cft);
            Serializer keyS = cft.keyserializer;
            for (Col cd : cft.columns) {
                Serializer columnS = cft.columnNameS;
                ColumnFamilyTemplate<Object, Object> templ;
                String key = cft.name + ";" + Serializers.toString(columnS);
                if (bcassandraMap.containsKey(key)) {
                    templ = bcassandraMap.get(key);
                } else {
                    templ = new ThriftColumnFamilyTemplate<Object, Object>(hectorkeyspace,
                            cft.name,
                            keyS,
                            columnS);
                    bcassandraMap.put(key, templ);
                }

                ImmutableList.Builder<String> mtemp = ImmutableList.builder();
                for (LevelType lt : cd.getRefPath().getLevel()) {
                    //logger.log(Level.CONFIG, "Level {0}", lt.getValue());
                    mtemp.add(lt.getValue());
                }
                PathMatch pm;
                //checks if pm is the template of a fixed columns

                pm = new PathMatch(this, cd, templ);

                builder.put(mtemp.build(), pm);
            }

        }
        referenceMap = builder.build();
        families = bfamilies.build();
        cassandraMap = ImmutableMap.copyOf(bcassandraMap);
    }

    public Set<PathMatch> getTemplateMatch(Object[] path) {
        List<String> tmp = getLevelName(path);
        ImmutableList<String> stringPath = ImmutableList.copyOf(tmp);
        return referenceMap.get(stringPath);
    }

    public ColumnFamilyTemplate<Object, Object> getTemplate(String columnFamilyName, String columnNameType) {
        String key = columnFamilyName + ";" + columnNameType;
        return checkNotNull(cassandraMap.get(key), "Template key not found");
    }

    public CF getCF(String familyName) {
        return families.get(familyName);
    }

    public List<String> getLevelName(Object[] path) {
        return getLevelName(path, 0, root.getEntity(), new ArrayList(path.length));
    }

    private List<String> getLevelName(Object[] path, int i, List<EntityType> en, List<String> caller) {
        //fixed type exploration

        for (EntityType e : en) {
            if ((e.getKeyValue() != null && e.getKeyValue().equals(path[i]))) { //if fixed type
                caller.add(e.getName());
                i++;
                if (e.getValue().getSimpleValue() != null) {
                    checkArgument(i == path.length);
                    return caller;
                } else {
                    //map value. 
                    ComplexValueType complex = checkNotNull(e.getValue().getNodeValue());
                    return getLevelName(path, i, complex.getEntity(), caller);
                    }
                }
            }

        //type exploration
        for (EntityType e : en) {
            if ((e.getKeyType() != null
                    && e.getKeyType().equals(GenUtils.getStandardType(path[i].getClass())))) { //if fixed type
                caller.add(e.getName());
                i++;
                if (e.getValue().getSimpleValue() != null) {
                    checkArgument(i == path.length);
                    return caller;
                } else {
                    //map value. 
                    ComplexValueType complex = checkNotNull(e.getValue().getNodeValue());
                    return getLevelName(path, i, complex.getEntity(), caller);
                    }
                }
            }
        throw new IllegalArgumentException("Level name not found: " + path[i] + " at level " + i);

    }

    public Ks getK() {
        return k;
    }
}
