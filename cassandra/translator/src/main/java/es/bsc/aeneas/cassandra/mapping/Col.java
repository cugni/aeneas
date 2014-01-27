package es.bsc.aeneas.cassandra.mapping;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

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

import static com.google.common.base.Preconditions.checkNotNull;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import es.bsc.aeneas.cassandra.translator.TrUtils;
import es.bsc.aeneas.core.model.gen.CassandraDestType;
import es.bsc.aeneas.core.model.gen.CassandraDestTypes;
import es.bsc.aeneas.core.model.gen.CassandraMatchType;
import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.gen.EntityType;
import es.bsc.aeneas.core.model.gen.FixedDest;
import es.bsc.aeneas.core.model.gen.LevelType;
import es.bsc.aeneas.core.model.gen.RefPathType;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.util.GenUtils;
import es.bsc.aeneas.core.model.util.TransformerUtil;
import es.bsc.aeneas.core.rosetta.Mapping;
import java.util.ArrayList;
import java.util.Collection;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.beans.AbstractComposite;

/**
 * List.
 *
 * @author cesare
 */
public class Col {

    private final static Logger log = Logger.getLogger(Col.class.getName());
    public final String id;
    public final CF cf;
    public final ValueType valueType;
    final ImmutableList< Lev> keys;
    final ImmutableList< Lev> cols;
    final ImmutableList< Lev> vals;
    final ImmutableMap<DestType, Type> tvalmap;
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
        id = mt.getId();


        RootType ref = GenUtils.getReferenceModel();
        List<EntityType> entity = ref.getEntity();
        SortedMap<Integer, Lev> keyb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> colb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> valb = new TreeMap<Integer, Lev>();
        ImmutableMap.Builder<DestType, Type> tvalmapb = ImmutableMap.builder();
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
            //I'm selecting all the destinations which go as key, column ro value



        }

        //*adding the fixed types
        //*adding the fixed types
        /**
         * *
         * TODO: Now that the new fixed values are way more extensible, how to
         * handle them?
         */
        if (!ct.getFixedDests().getFixedDest().isEmpty()) {
            List<FixedDest> dcolumns = selectFixedDest(ct.getFixedDests().getFixedDest(),
                    CassandraDestTypes.COLUMN_NAME);
            List<FixedDest> dkeys = selectFixedDest(ct.getFixedDests().getFixedDest(),
                    CassandraDestTypes.KEY);
            List<FixedDest> dvalues = selectFixedDest(ct.getFixedDests().getFixedDest(),
                    CassandraDestTypes.VALUE);
            if (!dcolumns.isEmpty()) {
                for (FixedDest cd : dcolumns) {
                    CassandraDestType dest = (CassandraDestType) cd.getDest();
                    colb.put(dest.getPosition(), new Lev(cd.getFixedValue(),
                            wr(TransformerUtil.getReturningStandardType(cd.getDest(),
                            cd.getFixedValueType()))));

                }
            }
            if (!dkeys.isEmpty()) {
                for (FixedDest ks : dkeys) {
                    CassandraDestType dest = (CassandraDestType) ks.getDest();
                    keyb.put(dest.getPosition(), new Lev(ks.getFixedValue(),
                            wr(TransformerUtil.getReturningStandardType(ks.getDest(),
                            ks.getFixedValueType()))));

                }
            }
            if (!dvalues.isEmpty()) {
                for (FixedDest vd : dvalues) {
                    CassandraDestType dest = (CassandraDestType) vd.getDest();

                    valb.put(dest.getPosition(), new Lev(vd.getFixedValue(),
                            wr(TransformerUtil.getReturningStandardType(vd.getDest(),
                            vd.getFixedValueType()))));

                }
            }
        }








        tvalmap = tvalmapb.build();
        if (valb.isEmpty()) {
            valueType = ValueType.VALUELESS;

        } else if (valb.size() > 1) {
            valueType = ValueType.COMPOSITE_VALUE;
            valueSerialiser = CompositeSerializer.get();
        } else {
            valueType = ValueType.SINGLE_VALUE;
            valueSerialiser = Serializers.getSerializer(valb.get(0).type);

        }

        log.log(Level.INFO, "{0} with valueType {1} ", new Object[]{id, valueType});

        checkArgument(keyb.size() > 0, "In the column %s of the CF %s it is not defined any keytype", new Object[]{id, cf.name});
        checkArgument(colb.size() > 0, "In the column %s of the CF %s it is not defined any columntype", new Object[]{id, cf.name});
        checkArgument(valb.size() > 0,
                "In the column %s of the CF %s it is not defined any valuetype", new Object[]{id, cf.name});
        checkArgument(keyb.firstKey().equals(0), "The key position must starts from 0");
        checkArgument(keyb.lastKey().equals(keyb.size() - 1), "The number position of the keys must be consegutive");
        checkArgument(colb.firstKey().equals(0), "The column position must starts from 0");
        checkArgument(colb.lastKey().equals(colb.size() - 1), "The number position of the columns must be consegutive");
        keys = ImmutableList.copyOf(keyb.values());
        cols = ImmutableList.copyOf(colb.values());
        vals = ImmutableList.copyOf(valb.values());




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

    /**
     * Wraps a standard Type into a General Type.
     *
     * @param s
     * @return
     */
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
        log.log(Level.INFO, "column: {0}", id);
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
            c.setIndexName(ct.getId().replaceAll(" ", ""));
        }


        return c;
    }

    public RefPathType getRefPath() {
        return ct.getRefPath();
    }

    public Object[] getParts(Collection<Mapping> match) {
        List<Mapping> keymaps = new ArrayList<Mapping>();
        List<Mapping> colmaps = new ArrayList<Mapping>();
        List<Mapping> valmaps = new ArrayList<Mapping>();
        for (Mapping m : match) {
            String w = m.getDest().getWhere();
            if (w.equals(CassandraDestTypes.KEY.value())) {
                keymaps.add(m);
            } else if (w.equals(CassandraDestTypes.COLUMN_NAME.value())) {
                colmaps.add(m);
            } else if (w.equals(CassandraDestTypes.VALUE.value())) {
                valmaps.add(m);
            } else {
                throw new IllegalStateException("Unknown destination type");
            }
        }
        Object[] result = new Object[3];
        if (keymaps.size() == 1) {
            checkArgument(this.cf.getKeyType().size() == 1,
                    "wrong number of key elements");
            result[0] = cf.keySerializer.toByteBuffer(log)
        }

    }
    /*
     * How to determine the row key: the list of levels for each column
     * determine the object to use to set the key. The @path is the list of
     * value of key and in @pm there's the refPath for determine which path
     * entry to use to composite the row key. The value of the key could be
     * taken by the @path or, in the case of valueless column, from the @value
     *
     *
     */

    public Object getKey(ImmutableList<Mapping> match) {

        switch (cf.keyType) {
            case SINGLE_KEY: {
                //if the key is fixed
                for (Mapping m : match) {
                    CassandraDestType dest = (CassandraDestType) m.getDest();
                    if (dest.getWhere().equals(CassandraDestTypes.KEY.value())) {
                        return wrap(m.getTransformedValue());
                    }
                }

                throw new IllegalArgumentException("Illegal configuration");
            }

            case COMPOSITE_KEY: {

                Composite cs = new Composite();

                Object[] componets = new Object[getKeyTypes().size()];
                for (Mapping m : match) {

                    CassandraDestType dest = (CassandraDestType) m.getDest();
                    if (dest.getWhere().equals(CassandraDestTypes.KEY.value())) {
                        componets[dest.getPosition()] = m.getTransformedValue();
                    }
                }
                for (Object c : componets) {
                    cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
                }

                checkArgument(cs.size() > 0);
                return cs;
            }

            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }


    }
    //TODO check here for index columnName  

    public Object getColumnName(ImmutableList<Mapping> match) {
        //

        List<Type> cntypes = getColumnNameTypes();
        checkArgument(cntypes.size() > 0);
        if (cntypes.size() == 1) {
            for (Mapping m : match) {
                CassandraDestType dest = (CassandraDestType) m.getDest();
                if (dest.getWhere().equals(CassandraDestTypes.COLUMN_NAME.value())) {
                    return wrap(m.getTransformedValue());
                }
            }

            throw new IllegalArgumentException("Illegal configuration");
        } else {

            //TODO CORRECT HERE
            AbstractComposite cs;
            switch (cf.columnSort) {
                case DYNAMIC:
                    cs = new DynamicComposite();
                    break;
                case COMPOSITE:
                    cs = new Composite();
                    break;
                default:
                    throw new IllegalArgumentException("Value " + cf.columnSort);
            }

            Object[] componets = new Object[getColumnNameTypes().size()];

            for (Mapping m : match) {

                CassandraDestType dest = (CassandraDestType) m.getDest();
                if (dest.getWhere().equals(CassandraDestTypes.COLUMN_NAME.value())) {
                    componets[dest.getPosition()] = m.getTransformedValue();
                }
            }

            for (Object c : componets) {
                cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
            }
            checkArgument(cs.size() > 0);
            return cs;
        }

    }

    private Object wrap(Object o) {
        switch (cf.columnSort) {
            case DYNAMIC:
                DynamicComposite dc = new DynamicComposite();
                dc.addComponent(o, (Serializer) Serializers.getSerializer(o));
                return dc;
            case COMPOSITE:
                Composite c = new Composite();
                c.addComponent(0, (Serializer) Serializers.getSerializer(o));
                return c;
            case SINGLE:
            default:
                return o;
        }
    }

    public Object getValue(ImmutableList<Mapping> match) {

        if (this.valueType.equals(ValueType.VALUELESS)) {
            return new byte[0];
        }


        List<Type> valtypes = getValueTypes();
        checkArgument(valtypes.size() > 0);
        if (valtypes.size() == 1) {

            for (Mapping m : match) {
                CassandraDestType dest = (CassandraDestType) m.getDest();
                if (dest.getWhere().equals(CassandraDestTypes.VALUE.value())) {
                    return m.getTransformedValue();
                }
            }
            throw new IllegalArgumentException("Illegal configuration");

        } else {
            Composite cs = new Composite();
            Object[] componets = new Object[getColumnNameTypes().size()];

            for (Mapping m : match) {

                CassandraDestType dest = (CassandraDestType) m.getDest();
                if (dest.getWhere().equals(CassandraDestTypes.KEY.value())) {
                    componets[dest.getPosition()] = m.getTransformedValue();
                }
            }

            for (Object c : componets) {
                cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
            }
            checkArgument(cs.size() > 0);
            return cs;
        }
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
