/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.translator;

import es.bsc.aeneas.cassandra.mapping.Ks;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.util.GenUtils;
import java.util.List;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.ddl.ComparatorType;

/**
 *
 * @author ccugnasc
 */
public class TrUtils {
    
    public static ComparatorType getComparator(StandardType t) {
        return ComparatorType.getByClassName(t.value());
    }
    
    public static ComparatorType getComparator(Type value) {
        
        if (value.getStandardType() != null) {
            if (value.getStandardType().equals(StandardType.ANY_TYPE)) {
                return ComparatorType.DYNAMICCOMPOSITETYPE;
            }
            return ComparatorType.getByClassName(value.getStandardType().value());
        } else {
            Serializer<? extends Object> s = Serializers.getSerializer(value);
            
            return s == null ? ComparatorType.BYTESTYPE : s.getComparatorType();
            
        }
        
    }
        public static boolean compareComposite(AbstractComposite c1, AbstractComposite c2) {
        if (!c1.getClass().equals(c2.getClass())) {
            return false;
        }
        if (c1.size() != c2.size()) {
            return false;
        }
        for (int i = 0; i < c1.size(); i++) {
            Object get = c1.get(i);
            Object get1 = c2.get(i);
            if (!get.equals(get1)) {
                return false;
            }
        }
        return true;
    }
    public static String getCompositeDefinition(List<Type> keyTypes) {
        StringBuilder sb = new StringBuilder(20);
        sb.append("CompositeType(");
        
        for (Type ty : keyTypes) {
            sb.append(getComparator(ty).getTypeName()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return sb.toString();
    }
    
//    public static Ks getCassandraModel(String cassandraModelName) {
//        return getCassandraModel(getCassandraModelFile(cassandraModelName));
//          }
    
}
