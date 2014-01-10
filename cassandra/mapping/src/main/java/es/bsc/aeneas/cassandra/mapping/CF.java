/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import es.bsc.aeneas.cassandra.mapping.Col.Lev;
import es.bsc.aeneas.model.gen.*;
import es.bsc.aeneas.model.util.GenUtils;
import es.bsc.aeneas.model.util.Serializers;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public class CF {

    private final static Logger log = Logger.getLogger(CF.class.getName());
    public final ColumnSort columnSort;
    final ColumnFamilyType cft;
    public final ComparatorType comparator;
    public final String comparatorAlias;
    public final KeyType keyType;
    public final String keyValidationClass;
    public final Serializer<? extends Object> columnNameS;
    public final Class<? extends Object> columnNameType;
    public final ImmutableList<Col> columns;
    private final ImmutableList<Type> keyTypes;
    private final Type keySingleType;
    public final Serializer<?> keyserializer;
    public final String name;

    CF(ColumnFamilyType cf) {

        this.cft = cf;
        name = cf.getName();
        //setting te columns
        {
            Builder<Col> lb = ImmutableList.builder();
            for (ColumnType c : cf.getColumn()) {
                lb.add(new Col(this, c));
            }
            for (FixedRowType frt : cf.getFixedRow()) {
                for (ColumnType c : frt.getColumn()) {
                    lb.add(new Col(this, c, frt));
                }
            }
            columns = lb.build();
        }
        /*
         * Setting the column sorter
         */ {
            Set<Type> lst = new HashSet<Type>(5);
            int multi = 0;
            for (Col column : columns) {
                if (column.ct.getFixedColumnName() != null) {
                    lst.add(column.ct.getFixedColumnName());
                }
                List<Type> t = checkNotNull(column.getColumnNameTypes());
                if (t.size() == 1) {
                    lst.add(t.get(0));
                } else if (t.size() > 1) {
                    multi++;
                } else {
                    throw new IllegalArgumentException("No matching value for the Column Name");

                }
            }
            //Choosing the comparator
            int ntypes;
            {
                Set<String> custom = new HashSet<String>(3);
                Set<StandardType> st = new HashSet(3);
                for (Type t : lst) {
                    if (t.getStandardType() != null) {
                        st.add(t.getStandardType());
                    } else {
                        custom.add(checkNotNull(t.getCustomType()));
                    }
                }
                ntypes = custom.size() + st.size();
            }
            if (multi > 0) {
                //Composite or dynamic composite?
                boolean equal = true;
                List<Type> ts = columns.get(0).getColumnNameTypes();
                for (Col col : columns) {
                    List<Type> t = col.getColumnNameTypes();
                    if (t.size() != ts.size()) {
                        equal = false;
                        break;
                    }
                    for (int i = 0; i < t.size(); i++) {
                        if (!GenUtils.compareTypes(ts.get(i), t.get(i))) {
                            equal = false;
                            break;
                        }
                    }

                }
                if (equal) {
                    columnSort = ColumnSort.COMPOSITE;

                } else {
                    columnSort = ColumnSort.DYNAMIC;
                }
            } else {
                //only one type or dynamic composite?
                if (ntypes == 1) {
                    columnSort = ColumnSort.SINGLE;
                } else {
                    //if(l.size()>1){
                    columnSort = ColumnSort.DYNAMIC;
                }
            }
            //setting the comparatator
            switch (columnSort) {
                case SINGLE:
                    comparator = GenUtils.getComparator(lst.iterator().next());
                    comparatorAlias = "";
                    columnNameS = Serializers.getSerializer(lst.iterator().next());
                    columnNameType = GenUtils.getClass(lst.iterator().next());
                    log.log(Level.INFO, "CF {0}: simple column sorter with {1} type", new Object[]{name, columnNameType});
                    break;
                case DYNAMIC:
                    comparator = ComparatorType.DYNAMICCOMPOSITETYPE;
                    comparatorAlias = DynamicComposite.DEFAULT_DYNAMIC_COMPOSITE_ALIASES;
                    columnNameS = DynamicCompositeSerializer.get();
                    columnNameType = DynamicComposite.class;
                    log.log(Level.INFO, "CF {0}: dynamic column sorter ", name);
                    break;
                case COMPOSITE:
                    comparator = ComparatorType.COMPOSITETYPE;
                    StringBuilder sb = new StringBuilder(20);
                    sb.append("(");
                    for (Type t : columns.get(0).getColumnNameTypes()) {
                        sb.append(GenUtils.getComparator(t).getTypeName()).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1).append(")");
                    comparatorAlias = sb.toString();
                    columnNameS = CompositeSerializer.get();
                    columnNameType = Composite.class;
                    log.log(Level.INFO, "CF {0}: composite column sorter with {1} types", new Object[]{name, comparatorAlias});
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        //Setting the key validator

        {
            boolean multi = false;
            for (Col c : columns) {
                if (c.getKeyTypes().size() > 1) {
                    multi = true;
                } else {
                    checkArgument(c.isFixedRow() || c.getKeyTypes().size() == 1,
                            "Wrong column definition: Undefined the matching key-value to level");
                }
            }
            if (multi) {
                //Check if composite or dynamic composite
                List<Type> t = columns.get(0).getKeyTypes();
                boolean dynamic = false;
                for (Col c : columns) {
                    List<Type> t2 = c.getKeyTypes();

                    if (t2.size() != t.size()) {
                        dynamic = true;
                        break;
                    }
                    for (int i = 0; i < t.size(); i++) {
                        if (!GenUtils.compareTypes(t.get(i), t2.get(i))) {
                            dynamic = true;
                            break;
                        }
                    }

                }
                if (dynamic) {
                    keyType = KeyType.DYNAMIC_KEY;
                    keyserializer = DynamicCompositeSerializer.get();
                    keyValidationClass = "DynamicCompositeType";
                    keyTypes = null;
                    keySingleType = null;
                } else {
                    keyserializer = CompositeSerializer.get();
                    keyType = KeyType.COMPOSITE_KEY;
                    ArrayList<Type> types = new ArrayList<Type>(3);
                    for (Lev nt : columns.get(0).keys) {
                        types.add(nt.type);
                    }
                    keyTypes = ImmutableList.copyOf(types);
                    keyValidationClass = GenUtils.getCompositeDefinition(keyTypes);
                    keySingleType = null;
                }
            } else {
                //check if simple or dynamic composite
                List<Type> t = columns.get(0).getKeyTypes();
                boolean dynamic = false;
                for (Col c : columns) {
                    List<Type> t2 = c.getKeyTypes();

                    if (t2.size() != t.size()) {
                        dynamic = true;
                        break;
                    }
                    for (int i = 0; i < t.size(); i++) {

                        if (!GenUtils.compareTypes(t.get(i), t2.get(i))) {
                            dynamic = true;
                            break;
                        }
                    }

                }
                if (dynamic) {
                    keyType = KeyType.DYNAMIC_KEY;
                    keyserializer = DynamicCompositeSerializer.get();
                    keyValidationClass = "DynamicComposite" + DynamicComposite.DEFAULT_DYNAMIC_COMPOSITE_ALIASES;
                    keyTypes = null;
                    keySingleType = null;
                    log.log(Level.INFO, "CF:{0} keyType:{1},keyValidationClass:{2},");
                } else {
                    keyTypes = null;
                    keyType = KeyType.SINGLE_KEY;
                    keyserializer = Serializers.getSerializer(t.get(0));
                    keyValidationClass = GenUtils.getComparator(t.get(0)).getTypeName();
                    keySingleType = t.get(0);
                }
                log.log(Level.INFO, "CF:{0} keyType:{1},keyValidationClass:{2},",
                        new Object[]{name, keyType, keyValidationClass});
            }
        }
    }

    public ImmutableList<Type> getKeyType() {
        return checkNotNull(keyTypes, "Invalid code: this method could be used only for composite keys");
    }

    public Type getKeySingleType() {
        return checkNotNull(keySingleType, "Invalid code: this method could be used only for single type keys");
    }

    ColumnFamilyDefinition createColumnFamilyDefinition(String keyspacename) {
        List<ColumnDefinition> columnmetadata = new ArrayList<ColumnDefinition>(columns.size());
        for (Col c : columns) {
            ColumnDefinition columnDefinition = c.getColumnDefinition();
            if (columnDefinition != null) {
                columnmetadata.add(columnDefinition);
            }
        }
        ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(keyspacename,
                cft.getName(), comparator, columnmetadata);

        cfd.setKeyValidationClass(keyValidationClass);
        //   cfd.setDefaultValidationClass(comparatorAlias);
        cfd.setComparatorType(comparator);
        cfd.setComparatorTypeAlias(comparatorAlias);

        //TODO puts here others column families configurations
        if (cft.getKeyCacheSize() != null) {
            cfd.setKeyCacheSize(cft.getKeyCacheSize());
        }
        if (cft.getKeyCachePeriod() != null) {
            cfd.setKeyCacheSavePeriodInSeconds(cft.getKeyCachePeriod());
        }
        if (cft.getRowCacheSize() != null) {
            cfd.setRowCacheSize(cft.getRowCacheSize());
        }
        if (cft.getRowCachePeriod() != null) {
            cfd.setRowCacheSavePeriodInSeconds(cft.getRowCachePeriod());
        }

        return cfd;
    }
}
