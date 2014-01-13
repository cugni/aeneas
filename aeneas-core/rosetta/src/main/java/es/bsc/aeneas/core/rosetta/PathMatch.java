/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import com.google.common.collect.ImmutableList;

import es.bsc.aeneas.core.model.gen.MatchType;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.gen.FixedDest;
import es.bsc.aeneas.core.model.gen.LevelType;
import es.bsc.aeneas.core.model.util.GenUtils;
import java.util.List;

/**
 *
 * @author cesare
 */
public class PathMatch {

    private final MatchType mt;
    private final List<Mapping> fixedMappings;
    private final List<String> path;

    PathMatch(List<String> path,MatchType mt) {
        this.path=path;
        this.mt = checkNotNull(mt);
        //inserting the fixed values

        ImmutableList.Builder<Mapping> b = ImmutableList.builder();
        for (FixedDest df : mt.getFixedDests().getFixedDest()) {
            Object o = GenUtils.castObject(df.getFixedValue(), df.getFixedValueType());
            for (DestType dt : df.getDest()) {
                Mapping m = new Mapping(dt, o);
                b.add(m);
            }

        }
        fixedMappings = b.build();

    }

    public List<String> getPath() {
        return path;
    }

    public List<Mapping> split(Object[] path) {
        List<LevelType> levels = mt.getRefPath().getLevel();
        checkArgument(path.length == levels.size());

        ImmutableList.Builder<Mapping> b = ImmutableList.builder();
        b.addAll(fixedMappings);

        //parsing the reference path
        for (int i = 0; i < path.length; i++) {
            LevelType l = levels.get(i);
            for (DestType dt : l.getDest()) {
                Mapping m = new Mapping(dt, path[i]);
                b.add(m);
            }
        }
        return b.build();

    }
}
