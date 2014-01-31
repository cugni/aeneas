package es.bsc.cassandrabm.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import es.bsc.cassandrabm.model.gen.*;
import es.bsc.cassandrabm.model.util.GenUtils;
import es.bsc.cassandrabm.model.util.Serializers;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
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
    final ImmutableTable<LevelType, Dest, Tr> tmap;
    final ImmutableMap<Dest, Tr> tvalmap;
    final ColumnType ct;
    private final Serializer<?> valueSerialiser;
    private final FixedRowType fixedRowType;
    private ImmutableList<Type> keyTypes = null;
    private ImmutableList<Type> columnNameTypes = null;
    private ImmutableList<Type> valueTypes = null;
    /*
     * If the value compose the key it gives its position, otherwise return -1.
     *
     */
    public final int valueKeyPosition;
    /*
     * If the value compose the Column Name it gives its position, otherwise
     * return -1.
     *
     */
    public final int valueCNPosition;
    /*
     * If the value compose the Value it gives its position, otherwise return
     * -1.
     *
     */
    public final int valueCVPosition;
    /*
     * Return the column metadata definition. If the column is composed is for a
     * variable column name return null
     */

    Col(CF cf, ColumnType ct) {
        this(cf, ct, null);
    }

    Col(CF cf, ColumnType ct, FixedRowType fixedRow) {
        this.ct = ct;
        this.cf = cf;
        name = ct.getName();
        this.fixedRowType = fixedRow;

        RootType ref = GenUtils.getReferenceModel();
        List<EntityType> entity = ref.getEntity();
        SortedMap<Integer, Lev> keyb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> colb = new TreeMap<Integer, Lev>();
        SortedMap<Integer, Lev> valb = new TreeMap<Integer, Lev>();
        ImmutableTable.Builder<LevelType, Dest, Tr> tmapb = ImmutableTable.builder();
        ImmutableMap.Builder<Dest, Tr> tvalmapb = ImmutableMap.builder();
        Type var = null;
        //joining the Cassandra model with the Reference one
        for (LevelType l : ct.getRefPath().getLevel()) {
            StandardType st = null;
            for (EntityType et : checkNotNull(entity,
                    "there's something wrong matching your Cassandra model with the Reference one.")) {
                if (et.getName().equals(l.getValue())) {
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
            if (!l.getColumnDest().isEmpty()) {
                for (ColumnDest cd : l.getColumnDest()) {
                    Tr t = new Tr(cd, wr(st));
                    colb.put(cd.getPosition(), new Lev(l.getValue(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, cd, t);
                }
            }
            if (!l.getKeyDest().isEmpty()) {
                for (KeyDest ks : l.getKeyDest()) {
                    Tr t = new Tr(ks, wr(st));
                    keyb.put(ks.getPosition(), new Lev(l.getValue(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, ks, t);
                }
            }
            if (!l.getValueDest().isEmpty()) {
                for (ValueDest vd : l.getValueDest()) {
                    Tr t = new Tr(vd, wr(st));
                    valb.put(vd.getPosition(), new Lev(l.getValue(), GenUtils.getTypeFromClass(t.valueClass)));
                    tmapb.put(l, vd, t);
                }
            }


        }
        tmap = tmapb.build();
        //*adding the fixed types
        if (ct.getFixedColumnName() != null) {
            FixedColumnName f = ct.getFixedColumnName();
            colb.put(f.getPosition(), new Lev(f.getName(), wr(f.getStandardType())));

        }
        if (isFixedRow()) {
            int i = 0;
            for (FixedType f : getFixedRowKey()) {
                keyb.put(i++,
                        new Lev(f.getValue(),
                        wr(f.getType())));

            }
        }


        //inserting the value (the leaf)
        if (!ct.getRefPath().getColumnDest().isEmpty()) {
            for (ColumnDest cd : ct.getRefPath().getColumnDest()) {
                Tr t = new Tr(cd, var);
                colb.put(cd.getPosition(),
                        new Lev("$leaf",
                        checkNotNull(GenUtils.getTypeFromClass(t.valueClass), "Not completed refPath")));
                tvalmapb.put(cd, t);
            }
            valueCNPosition = ct.getRefPath().getColumnDest().get(0).getPosition();
        } else {
            valueCNPosition = - 1;
        }
        if (!ct.getRefPath().getKeyDest().isEmpty()) {
            for (KeyDest kd : ct.getRefPath().getKeyDest()) {
                Tr t = new Tr(kd, var);
                keyb.put(kd.getPosition(),
                        new Lev("$leaf",
                        GenUtils.getTypeFromClass(t.valueClass)));
                tvalmapb.put(kd, t);
            }
            valueKeyPosition = ct.getRefPath().getKeyDest().get(0).getPosition();
        } else {
            valueKeyPosition = - 1;
        }
        if (!ct.getRefPath().getValueDest().isEmpty()) {
            for (ValueDest vd : ct.getRefPath().getValueDest()) {
                Tr t = new Tr(vd, var);
                valb.put(vd.getPosition(), new Lev("$leaf", GenUtils.getTypeFromClass(t.valueClass)));
                tvalmapb.put(vd, t);
            }
            valueCVPosition = ct.getRefPath().getValueDest().get(0).getPosition();
        } else {
            if (ct.getValueless() != null) {
                valueCVPosition = - 1;
            } else {
                if (valb.isEmpty()) {
                    valb.put(0, new Lev("$leaf", var));
                    //Setting the default value destination (0)
                    ValueDest vd = new ValueDest();
                    vd.setPosition(0);
                    Tr t = new Tr(vd, var);
                    tvalmapb.put(vd, t);
                    valueCVPosition = 0;
                } else {
                    valueCVPosition = -1;
                }
            }
        }
        tvalmap = tvalmapb.build();
        if (valb.isEmpty()) {
            if (ct.getValueless() != null) {
                valueSerialiser = BytesArraySerializer.get();
                valueType = ValueType.VALUELESS;
            } else {
                throw new IllegalArgumentException("Wrong configuration."
                        + " A level has been setted to be part of the value but this column is valueless");
            }

        } else {
            if (ct.getValueless() != null) {
                throw new IllegalArgumentException("Wrong configuration. "
                        + "A level has been setted to be part of the value but this column is valueless");

            }
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
        checkArgument(valb.size() > 0 || isValueless(),
                "In the column %s of the CF %s it is not defined any valuetype", new Object[]{name, cf.name});
        checkArgument(keyb.firstKey().equals(0), "The key position must starts from 0");
        checkArgument(keyb.lastKey().equals(keyb.size() - 1), "The number position of the keys must be consegutive");
        checkArgument(colb.firstKey().equals(0), "The column position must starts from 0");
        checkArgument(colb.lastKey().equals(colb.size() - 1), "The number position of the columns must be consegutive");
        if (!isValueless()) {
            checkArgument(valb.firstKey().equals(0), "The value position must starts from 0");
            checkArgument(valb.lastKey().equals(valb.size() - 1), "The number position of the values must be consegutive");
        }
        keys = ImmutableList.copyOf(keyb.values());
        cols = ImmutableList.copyOf(colb.values());
        vals = ImmutableList.copyOf(valb.values());



    }

    /**
     * This method transform one of the element of the path
     *
     * @param lev
     * @param dest
     * @param object
     * @return
     */
    public Object transform(LevelType lev, Dest dest, Object object) {
        Tr t = checkNotNull(tmap.get(lev, dest), "Transformation class not found");
        return t.transform(object);
    }

    /**
     * This method transform the value (leaf of the reference model) following
     * the current model
     *
     * @param refPath
     * @param dest
     * @param value
     * @return
     */
    public Object transform(Dest dest, Object value) {
        Tr t = checkNotNull(tvalmap.get(dest), "Transformation class not found");
        return t.transform(value);
    }

    public Serializer<?> getValueSerializer() {
        return valueSerialiser;
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

    public boolean isFixedRow() {
        return fixedRowType != null;
    }

    public List<FixedType> getFixedRowKey() {
        return checkNotNull(fixedRowType, "Invalid request on a not fixed row column").getKey();
    }

    ColumnDefinition getColumnDefinition() {
        NamedType fixedColumnName = ct.getFixedColumnName();

        if (fixedColumnName == null) {
            return null;
        }
        BasicColumnDefinition c = new BasicColumnDefinition();
        log.log(Level.INFO, "column: {0}", name);
        //it checks if the column name is fixed or
 
 
        switch (cf.columnSort) {
            case COMPOSITE:
                Object ob = GenUtils.castObject(fixedColumnName.getName(), fixedColumnName.getStandardType());
                log.log(Level.INFO, "Column name: composite name made by {0}", ob);
                c.setName(new Composite(ob).serialize());
                break;
            case DYNAMIC:
                Object ob2 = GenUtils.castObject(fixedColumnName.getName(), fixedColumnName.getStandardType());
                log.log(Level.INFO, "Column name: dynamic composite name made by {0}", ob2);
                c.setName(new DynamicComposite(ob2).serialize());
                break;
            default:
                //simple
                Serializer s = Serializers.getSerializer(fixedColumnName.getStandardType());
                c.setName(s.toByteBuffer(fixedColumnName.getName()));
                log.log(Level.INFO, "Column name: simple name with the value {0}", fixedColumnName.getName());
        }
        //validator for the value

        if (getValueTypes().isEmpty()) {
            c.setValidationClass(ComparatorType.BYTESTYPE.getClassName());
        } else if (valueTypes.size() == 1) {
            
            c.setValidationClass(GenUtils.getComparator(valueTypes.get(0)).getClassName());


        } else {
            c.setValidationClass(GenUtils.getCompositeDefinition(valueTypes));
        }
        log.log(Level.INFO, "Column value: set validator {0}", c.getValidationClass());
        if (ct.isIndex()) {
            c.setIndexType(ColumnIndexType.KEYS);
            c.setIndexName(ct.getName().replaceAll(" ", ""));
        }


        return c;
    }

    public Object getFixedColumnName() {
        return GenUtils.castObject(ct.getFixedColumnName().getName(),
                ct.getFixedColumnName().getStandardType());
    }

    public int getFixedColumnPosition() {
        return ct.getFixedColumnName().getPosition();
    }

    public boolean isFixedColumnName() {
        return ct.getFixedColumnName() != null;
    }

    public boolean isValueless() {
        return ct.getValueless() != null;
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
