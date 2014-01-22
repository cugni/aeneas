package es.bsc.aeneas.cassandra.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import es.bsc.aeneas.cassandra.translator.TrUtils;
import es.bsc.aeneas.core.model.gen.CassandraDestType;
import es.bsc.aeneas.core.model.gen.CassandraDestTypes;
import es.bsc.aeneas.core.model.gen.CassandraMatchType;
import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.gen.EntityType;
import es.bsc.aeneas.core.model.gen.FixedDest;
import es.bsc.aeneas.core.model.gen.FixedType;
import es.bsc.aeneas.core.model.gen.LevelType;
import es.bsc.aeneas.core.model.gen.NamedType;
import es.bsc.aeneas.core.model.gen.RefPathType;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.util.GenUtils;
import java.util.ArrayList;

/**
 * List.
 *
 * @author cesare
 */
public class Col {

    private final static Logger log = Logger.getLogger(Col.class.getName());
    public final String name;
    public final CF cf;
    public final ValueType valueType;
    final ImmutableList< Lev> keys;
    final ImmutableList< Lev> cols;
    final ImmutableList< Lev> vals;
    final ImmutableTable<LevelType, DestType, Tr> tmap;
    final ImmutableMap<DestType, Tr> tvalmap;
    final CassandraMatchType ct;
    private final Serializer<?> valueSerialiser;
    private ImmutableList<Type> keyTypes = null;
    private ImmutableList<Type> columnNameTypes = null;
    private ImmutableList<Type> valueTypes = null;
    /*
     * Return the column metadata definition. If the column is composed is for a
     * variable column name return null
     */

    Col(CF cf, CassandraMatchType mt) {
        this.ct = mt;
        this.cf = cf;
        name = mt.getName();


        RootType ref = GenUtils.getReferenceModel();
        List<EntityType> entity = ref.getEntity();
        SortedMap<Integer, Lev> keyb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> colb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> valb = new TreeMap<Integer, Lev>();
        ImmutableTable.Builder<LevelType, DestType, Tr> tmapb = ImmutableTable.builder();
        ImmutableMap.Builder<DestType, Tr> tvalmapb = ImmutableMap.builder();
        Type var = null;
        //joining the Cassandra model with the Reference one
        for (LevelType l : mt.getRefPath().getLevel()) {
            StandardType st = null;
            for (EntityType et : checkNotNull(entity,
                    "there's something wrong matching your Cassandra model with the Reference one.")) {
                if (et.getName().equals(l.getName())) {
                    st = et.getKeyType();
                    if (et.getValue().getNodeValue() != null) {
                        //going in deep in the three
                        entity = et.getValue().getNodeValue().getEntity();
                    } else {
                        var = et.getValue().getSimpleValue();
                        entity = null;
                    }
                    break;
                }
            }
            List<CassandraDestType> dcolumns = selectDest(l.getDest(), CassandraDestTypes.COLUMN_NAME);
            List<CassandraDestType> dkeys = selectDest(l.getDest(), CassandraDestTypes.KEY);
            List<CassandraDestType> dvalues = selectDest(l.getDest(), CassandraDestTypes.VALUE);
            if (!dcolumns.isEmpty()) {
                for (CassandraDestType cd : dcolumns) {
                    Tr t = new Tr(cd, wr(st));
                    colb.put(cd.getPosition(), new Lev(l.getName(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, cd, t);
                }
            }
            if (!dkeys.isEmpty()) {
                for (CassandraDestType ks : dkeys) {
                    Tr t = new Tr(ks, wr(st));
                    keyb.put(ks.getPosition(), new Lev(l.getName(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, ks, t);
                }
            }
            if (!dvalues.isEmpty()) {
                for (CassandraDestType vd : dvalues) {
                    Tr t = new Tr(vd, wr(st));
                    valb.put(vd.getPosition(), new Lev(l.getName(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, vd, t);
                }
            }


        }
        tmap = tmapb.build();
        //*adding the fixed types
        //*adding the fixed types
        /**
         * *
         * TODO: Now that the new fixed values are way more extensible, how to
         * handle them?
         */
        if (!ct.getFixedDests().getFixedDest().isEmpty()) {
            FixedColumnName f = ct.getFixedColumnName();
            colb.put(f.getPosition(), new Lev(f.getName(), wr(f.getStandardType())));

        }






        tvalmap = tvalmapb.build();
        if (valb.isEmpty()) {
            throw new IllegalArgumentException("Wrong configuration."
                    + " A level has been setted to be part of the value but this column is valueless");

        } else {
            if (valb.size() > 1) {
                valueType = ValueType.COMPOSITE_VALUE;
                valueSerialiser = CompositeSerializer.get();
            } else {
                valueType = ValueType.SINGLE_VALUE;
                valueSerialiser = Serializers.getSerializer(valb.get(0).type);
            }
        }

        log.log(Level.INFO, "{0} with valueType {1} ", new Object[]{name, valueType});

        checkArgument(keyb.size() > 0, "In the column %s of the CF %s it is not defined any keytype", new Object[]{name, cf.name});
        checkArgument(colb.size() > 0, "In the column %s of the CF %s it is not defined any columntype", new Object[]{name, cf.name});
        checkArgument(valb.size() > 0,
                "In the column %s of the CF %s it is not defined any valuetype", new Object[]{name, cf.name});
        checkArgument(keyb.firstKey().equals(0), "The key position must starts from 0");
        checkArgument(keyb.lastKey().equals(keyb.size() - 1), "The number position of the keys must be consegutive");
        checkArgument(colb.firstKey().equals(0), "The column position must starts from 0");
        checkArgument(colb.lastKey().equals(colb.size() - 1), "The number position of the columns must be consegutive");
        keys = ImmutableList.copyOf(keyb.values());
        cols = ImmutableList.copyOf(colb.values());
        vals = ImmutableList.copyOf(valb.values());



    }

    /**
     * This method transform one of the element of the path
     *
     * @param lev
     * @param DestType
     * @param object
     * @return
     */
    public Object transform(LevelType lev, DestType DestType, Object object) {
        Tr t = checkNotNull(tmap.get(lev, DestType), "Transformation class not found");
        return t.transform(object);
    }

    /**
     * This method transform the value (leaf of the reference model) following
     * the current model
     *
     * @param refPath
     * @param DestType
     * @param value
     * @return
     */
    public Object transform(DestType DestType, Object value) {
        Tr t = checkNotNull(tvalmap.get(DestType), "Transformation class not found");
        return t.transform(value);
    }

    public Serializer<?> getValueSerializer() {
        return valueSerialiser;
    }

    List<FixedDest> selectFixedDest(List<FixedDest> l, CassandraDestTypes type) {
        List<FixedDest> ret = new ArrayList();
        for (FixedDest dt : l) {
            CassandraDestType cdt = (CassandraDestType) dt.getDest();
            if (cdt.getWhere().equals(type.value())) {
                ret.add(dt);
            }

        }

        return ret;

    }

    List<CassandraDestType> selectDest(List<DestType> l, CassandraDestTypes type) {
        List<CassandraDestType> ret = new ArrayList();
        for (DestType dt : l) {
            CassandraDestType cdt = (CassandraDestType) dt;
            if (cdt.getWhere().equals(type.value())) {
                ret.add(cdt);
            }

        }

        return ret;

    }

    private Type wr(StandardType s) {
        Type t = new Type();
        t.setStandardType(s);
        return t;
    }

    public List<Type> getKeyTypes() {
        if (keyTypes != null) {
            return keyTypes;
        }

        Builder<Type> builder = ImmutableList.builder();

        for (Lev nt : keys) {
            builder.add(nt.type);
        }
        keyTypes = builder.build();
        return keyTypes;

    }

    public List<Type> getColumnNameTypes() {
        if (columnNameTypes != null) {
            return columnNameTypes;
        }
        Builder<Type> builder = ImmutableList.builder();
        for (Lev nt : cols) {
            builder.add(nt.type);
        }
        columnNameTypes = builder.build();
        return columnNameTypes;

    }

    public List<Type> getValueTypes() {
        if (valueTypes != null) {
            return valueTypes;
        }
        Builder<Type> builder = ImmutableList.builder();
        for (Lev nt : vals) {
            builder.add(nt.type);
        }
        valueTypes = builder.build();
        return valueTypes;

    }

    ColumnDefinition getColumnDefinition() {
        List<FixedDest> fixedDest = ct.getFixedDests().getFixedDest();

        if (fixedDest.isEmpty()) {
            return null;
        }
        BasicColumnDefinition c = new BasicColumnDefinition();
        log.log(Level.INFO, "column: {0}", name);
        //it checks if the column name is fixed or

        List<FixedDest> fixedColumns = selectFixedDest(fixedDest,
                CassandraDestTypes.COLUMN_NAME);



        switch (cf.columnSort) {
            case COMPOSITE: {
                Composite com = new Composite();
                for (FixedDest fd : fixedColumns) {

                    Object ob = GenUtils.castObject(fd.getFixedValue(), fd.getFixedValueType());
                    log.log(Level.INFO, "Column name: composite name made by {0}", ob);
                    com.add(ob);
                }
                c.setName(com.serialize());
            }
            break;
            case DYNAMIC: {
                DynamicComposite com = new DynamicComposite();
                for (FixedDest fd : fixedColumns) {
                    Object ob = GenUtils.castObject(fd.getFixedValue(), fd.getFixedValueType());
                    log.log(Level.INFO, "Column name: dynamic composite name made by {0}", ob);

                    com.add(ob);
                }
                c.setName(com.serialize());
            }
            break;
            default:
                //simple
                checkArgument(fixedColumns.size() == 1,
                        "For a single matching with a simple column types you must have only one fixed value for the column ");
                FixedDest fc = fixedColumns.get(0);
                Serializer s = Serializers.getSerializer(fc.getFixedValueType());
                c.setName(s.toByteBuffer(fc.getFixedValue()));
                log.log(Level.INFO, "Column name: simple name with the value {0}", fc.getFixedValue());
        }
        //validator for the value

        if (getValueTypes().isEmpty()) {
            c.setValidationClass(ComparatorType.BYTESTYPE.getClassName());
        } else if (valueTypes.size() == 1) {

            c.setValidationClass(TrUtils.getComparator(valueTypes.get(0)).getClassName());


        } else {
            c.setValidationClass(TrUtils.getCompositeDefinition(valueTypes));
        }
        log.log(Level.INFO, "Column value: set validator {0}", c.getValidationClass());
        if (ct.isIndex()) {
            c.setIndexType(ColumnIndexType.KEYS);
            c.setIndexName(ct.getName().replaceAll(" ", ""));
        }


        return c;
    }

    public RefPathType getRefPath() {
        return ct.getRefPath();
    }

    static class Lev {

        final String name;
        final Type type;

        Lev(String name, Type type) {
            checkArgument(type.getCollectionType() != null || type.getStandardType() != null);
            this.name = name;
            this.type = type;
        }
    }
}
