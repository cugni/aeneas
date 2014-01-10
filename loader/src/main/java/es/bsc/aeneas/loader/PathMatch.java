/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.loader;

import es.bsc.aeneas.loader.exceptions.InvalidGetRequest;
import es.bsc.aeneas.cassandra.mapping.Col;
import es.bsc.aeneas.model.gen.*;
import es.bsc.aeneas.model.util.GenUtils;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public class PathMatch {

    private static final Logger log = Logger.getLogger(PathMatch.class.getName());
    public final static Integer delayTime = Integer.getInteger("delayTime", 100);
    private final Col column;
    private ColumnFamilyTemplate<Object, Object> template;
    public final PathMatchMap father;

    public PathMatch(PathMatchMap father, Col column,
            ColumnFamilyTemplate<Object, Object> template) {
        super();
        this.father = checkNotNull(father);
        this.column = checkNotNull(column);
        this.template = checkNotNull(template);
    }

    public List<LevelType> getLevels() {
        return column.getRefPath().getLevel();
    }

    public ColumnFamilyTemplate<Object, Object> getTemplate() {
        return template;
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

    @SuppressWarnings("unchecked")
    public Object getKey(Object value, Object[] path) {
        RefPathType refPath = column.getRefPath();
        switch (column.cf.keyType) {
            case SINGLE_KEY: {
                //if the key is fixed
                if (column.isFixedRow()) {
                    return GenUtils.castObject(column.getFixedRowKey().get(0).getValue(),
                            checkNotNull(column.cf.getKeySingleType(), "Only standard types could have a fixed value").getStandardType());
                }
                List<LevelType> ll = refPath.getLevel();
                //if the key is composted by by the value
                if (!refPath.getKeyDest().isEmpty()) {
                    KeyDest dest = refPath.getKeyDest().get(0);
                    return column.transform(dest, value);

                }
                for (int i = 0; i < ll.size(); i++) {
                    //if the key is composted by one of the element of the path
                    if (!ll.get(i).getKeyDest().isEmpty()) {
                        LevelType lev = ll.get(i);
                        return column.transform(lev, lev.getKeyDest().get(0), path[i]);
                    }
                }
                throw new IllegalArgumentException("Illegal configuration");
            }

            case COMPOSITE_KEY: {

                Composite cs = new Composite();
                List<LevelType> ll = refPath.getLevel();
                if (column.isFixedRow()) {
                    List<String> fkeys = new ArrayList<String>(2);
                    for (FixedType ft : column.getFixedRowKey()) {
                        fkeys.add(ft.getValue());
                    }
                    List<Type> keyType = column.cf.getKeyType();
                    for (int i = 0; i < keyType.size(); i++) {
                        StandardType st2 = keyType.get(i).getStandardType();
                        checkNotNull(st2, "Composite keys with custom type not supported");
                        cs.add(GenUtils.castObject(fkeys.get(i), st2));
                    }

                } else {
                    Object[] componets = new Object[column.getKeyTypes().size()];

                    for (int i = 0; i < ll.size(); i++) {
                        LevelType lev = ll.get(i);
                        if (!ll.get(i).getKeyDest().isEmpty()) { //0 is the default value.
                            for (KeyDest kd : lev.getKeyDest()) {
                                componets[kd.getPosition()] = column.transform(lev, kd, path[i]);
                            }
                        }
                    }
                    for (KeyDest kd : refPath.getKeyDest()) {

                        componets[kd.getPosition()] = column.transform(kd, value);
                    }
                    for (Object c : componets) {
                        cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
                    }
                }
                checkArgument(cs.size() > 0);
                return cs;
            }

            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }


    }
    //TODO check here for index columnName  

    public Object getColumnName(Object value, Object[] path) {
        //

        RefPathType refPath = column.getRefPath();

        List<Type> cntypes = column.getColumnNameTypes();
        checkArgument(cntypes.size() > 0);
        if (cntypes.size() == 1) {
            if (column.isFixedColumnName()) {
                return wrap(column.getFixedColumnName());
            }
            List<LevelType> ll = refPath.getLevel();

            if (!refPath.getColumnDest().isEmpty()) {
                return wrap(column.transform(refPath.getColumnDest().get(0), value));
            }
            for (int i = 0; i < ll.size(); i++) {
                //if the key is composted by by the value
                LevelType lev = ll.get(i);

                if (!lev.getColumnDest().isEmpty()) {
                    ColumnDest dest = lev.getColumnDest().get(0);
                    return wrap(column.transform(lev, dest, path[i]));
                }
            }
            throw new IllegalArgumentException("Illegal configuration");
        } else {

            //TODO CORRECT HERE
            AbstractComposite cs;
            switch (column.cf.columnSort) {
                case DYNAMIC:
                    cs = new DynamicComposite();
                    break;
                case COMPOSITE:
                    cs = new Composite();
                    break;
                default:
                    throw new IllegalArgumentException("Value " + column.cf.columnSort);
            }

            List<LevelType> ll = refPath.getLevel();

            Object[] componets = new Object[column.getColumnNameTypes().size()];
            for (int i = 0; i < ll.size(); i++) {
                LevelType level = ll.get(i);
                if (!level.getColumnDest().isEmpty()) {
                    for (ColumnDest cd : level.getColumnDest()) {
                        componets[cd.getPosition()] = column.transform(level, cd, path[i]);
                    }
                }
            }
            for (ColumnDest cd : refPath.getColumnDest()) {
                componets[cd.getPosition()] = column.transform(cd, value);
            }
            if (column.isFixedColumnName()) {
                componets[column.getFixedColumnPosition()] = column.getFixedColumnName();
            }
            for (Object c : componets) {
                cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
            }
            checkArgument(cs.size() > 0);
            return cs;
        }

    }

    private Object wrap(Object o) {
        switch (column.cf.columnSort) {
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

    public Object getValue(Object value, Object[] path) {

        if (column.isValueless()) {
            return null;
        }
        RefPathType refPath = column.getRefPath();

        List<Type> valtypes = column.getValueTypes();
        checkArgument(valtypes.size() > 0);
        if (valtypes.size() == 1) {
            List<LevelType> ll = refPath.getLevel();
            for (int i = 0; i < ll.size(); i++) {
                //if the key is composted by by the value
                LevelType lev = ll.get(i);
                if (!lev.getValueDest().isEmpty()) {
                    return column.transform(lev, lev.getValueDest().get(0), path[i]);
                }
            }

            return value; //if it is not specified is supposed to the leaf is simply the value

        } else {
            List<LevelType> ll = refPath.getLevel();
            Composite cs = new Composite();
            Object[] componets = new Object[column.getColumnNameTypes().size()];
            for (int i = 0; i < ll.size(); i++) {

                for (ValueDest vd : ll.get(i).getValueDest()) {
                    componets[vd.getPosition()] = column.transform(ll.get(i), vd, path[i]);
                }
            }

            for (ValueDest vd : refPath.getValueDest()) {
                componets[vd.getPosition()] = column.transform(vd, value);
            }
            for (Object c : componets) {
                cs.addComponent(c, (Serializer) Serializers.getSerializer(c));
            }
            checkArgument(cs.size() > 0);
            return cs;
        }
    }

    void pinsert(Object val, Object[] path) {
        try {
            ColumnFamilyUpdater<Object, Object> updater = getTemplate().createUpdater();
            Object key = getKey(val, path);
            Object cn = getColumnName(val, path);
            Object value = getValue(val, path);
            updater.addKey(key);
            if (!column.isValueless()) { //if is not a valueless column
                Serializer<?> s = column.getValueSerializer();
                if (s instanceof DynamicCompositeSerializer
                        && !(value instanceof DynamicComposite)) {
                    value = new DynamicComposite(value);
                }
                updater.setValue(cn, value,
                        (Serializer) column.getValueSerializer());
            } else {
                updater.setByteArray(cn, new byte[0]);
            }
            getTemplate().update(updater);
        } catch (Exception e) {
            throw new RuntimeException("Exception inserting value " + val
                    + " with the path " + Arrays.toString(path), e);
        }

    }

    @SuppressWarnings("unchecked")
    private void pinsert(Object val, Object[] path, Mutator mut) {
        try {
            Object key = getKey(val, path);
            Object cname = getColumnName(val, path);
            Object value = getValue(val, path);
            HColumn col;

            Serializer nameserializer = (Serializer) column.cf.columnNameS;
            if (!column.isValueless()) { //if is not a valueless column
                col = HFactory.createColumn(cname,
                        value,
                        nameserializer,
                        (Serializer) column.getValueSerializer());
            } else {
                col = HFactory.createColumn(cname,
                        new byte[0],
                        nameserializer,
                        BytesArraySerializer.get());
            }
            mut.addInsertion(key, column.cf.name, col);
        } catch (Exception e) {
            throw new RuntimeException("Exception inserting value " + val
                    + " with the path " + Arrays.toString(path), e);
        }

    }

    public PutRequest getPutRequest(Object value, Object[] path) {
        return new PutRequest(value, path);
    }

    Object testGet(Object value, Object[] path) throws InvalidGetRequest {
        Object key = getKey(value, path);
        if (key == null) {
            throw new InvalidGetRequest("Impossible to generate the key");
        }

        Object colname = checkNotNull(getColumnName(value, path));
        HColumn hcolumn = template.querySingleColumn(key, colname, column.getValueSerializer());//TODO check here
        if (hcolumn == null) {
            return null;
        }
        //TODO controll it. This is a hector bug.. Hope somebody will fix it

        if (column.valueCVPosition >= 0) {
            Object val = hcolumn.getValue();
            switch (column.valueType) {
                case SINGLE_VALUE:
                    return val;
                case COMPOSITE_VALUE:
                    if (val instanceof AbstractComposite) {
                        return ((AbstractComposite) val).get(column.valueCVPosition);
                    } else {
                        throw new IllegalArgumentException("Illegal configuration. Expected a Composite one instead of " + val.getClass());
                    }
                case VALUELESS:
                default:
                    throw new IllegalArgumentException("Inconsistent configurations");

            }
        } else if (column.valueKeyPosition >= 0) {
            /*
             * The query returned a row with the value inside and so the query
             * works.
             */
            return value;
        } else if (column.valueCNPosition >= 0) {
            /*
             * The query has a column with the column name generated by the valu
             * and so the query works.
             */

            if (hcolumn != null) {
                return value;
            } else {
                return null;
            }

        } else {
            return null;
        }

    }

    public class PutRequest {

        final Object value;
        final Object[] path;

        public PutRequest(Object value, Object[] path) {
            this.value = value;
            this.path = path;
        }
        /*
         * This method is used for batched insertion
         */

        public void insert(Mutator mut) {
//            log.log(Level.FINE, "Batch Inserting value {0} with {1} path", new Object[]{value, Arrays.toString(path)});
            pinsert(value, path, mut);
        }
        /*
         * This method has to be used for not batched insertion
         */

        public void insert() {
            //log.log(Level.FINE, "Insertig value {0} with {1} path", new Object[]{value, Arrays.toString(path)});
            pinsert(value, path);
        }
        /*
         * Used for batching
         */

        public Serializer getKeySerializer() {
            return template.getKeySerializer();
        }
    }
}
