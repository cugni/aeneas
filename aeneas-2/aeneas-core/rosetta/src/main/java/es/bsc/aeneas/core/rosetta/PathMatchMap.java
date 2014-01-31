/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import com.google.common.collect.ImmutableList;


import es.bsc.aeneas.core.model.util.GenUtils;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.ComplexValueType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.model.gen.EntityType;
import es.bsc.aeneas.core.model.gen.LevelType;
import es.bsc.aeneas.core.model.gen.MatchType;
import es.bsc.aeneas.core.model.gen.RootType;
import java.util.logging.Level;

/**
 *
 * @author cesare
 */
public class PathMatchMap {

    private static final Logger log = Logger.getLogger(PathMatchMap.class.getName());
//    public static PathMatchMap createPathMatchMap(URL cassandraModel) {
//        ClusterType matchingModel = GenUtils.getMatchingModel(cassandraModel);
//        RootType root = GenUtils.getReferenceModel();
//        return new PathMatchMap(matchingModel, root);
//
//    }
    private final EnumMap<CrudType, Multimap<ImmutableList<String>, PathMatch>> referenceMap;
    private final RootType root;

    public PathMatchMap(RootType root, ClusterType mm) {
        this.root = root;
        referenceMap = new EnumMap(CrudType.class);
        for (CrudType c : CrudType.values()) {
            Multimap<ImmutableList<String>, PathMatch> m = HashMultimap.create();
            referenceMap.put(c, m);
        }

        //Creating the template
        log.log(Level.INFO, "Generating matching for {0}", mm.getName());

        for (MatchType mt : mm.getMatches().getMatch()) {
            ImmutableList.Builder<String> mtemp = ImmutableList.builder();
            //I'm generating the list of elements in the path
            for (LevelType lt : mt.getRefPath().getLevel()) {
                //logger.log(Level.CONFIG, "Level {0}", lt.getValue());
                mtemp.add(lt.getName());
            }
            PathMatch pm = new PathMatch(mtemp.build(),mt);
            switch (mt.getCrudType()) {
                case CREATE_OR_UPDATE:
                    referenceMap.get(CrudType.CREATE).put(mtemp.build(), pm);
                    referenceMap.get(CrudType.UPDATE).put(mtemp.build(), pm);
                    break;
                case READ_OR_DELETE:
                    referenceMap.get(CrudType.READ).put(mtemp.build(), pm);
                    referenceMap.get(CrudType.DELETE).put(mtemp.build(), pm);
                    break;
                default:
                    referenceMap.get(mt.getCrudType()).put(mtemp.build(), pm);
                    break;

            }



        }

    }

    public Collection<PathMatch> getPathMatches(CrudType crud, Object[] path) {
        List<String> tmp = getLevelName(path);
        ImmutableList<String> stringPath = ImmutableList.copyOf(tmp);
        return referenceMap.get(crud).get(stringPath);
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
}
