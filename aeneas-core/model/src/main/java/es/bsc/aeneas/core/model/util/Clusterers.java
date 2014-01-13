/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.model.util;

import com.google.common.base.CaseFormat;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.marshalling.TypeClusterer;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 *
 * @author cesare
 */
public class Clusterers {

    private final static Logger log = Logger.getLogger(Clusterers.class.getName());

    public static TypeClusterer<?> getClusterer(Type type) {
        TypeClusterer<?> s;

        if (checkNotNull(type).getStandardType() != null) {
            s = getClusterer(type.getStandardType());
        } else {
            String className = type.getCustomType().subSequence(0, type.getCustomType().length() - 4)
                    + "Clusterer";
            s = getClustererByClassName(className);

        }
        return s;
    }

    private static TypeClusterer<?> getClusterer(StandardType standardType) {
        TypeClusterer s;

        String className = "es.bsc.aeneas.model.marshalling."
                + standardType.value().subSequence(0, standardType.value().length() - 4) //it removes the "Type" suffix
                + "Clusterer";
        s = checkNotNull(getClustererByClassName(className));
        return s;
    }

    public static TypeClusterer<?> getClustererByClassName(String className) {
        try {
            Class<TypeClusterer<?>> sclass;
            sclass = (Class<TypeClusterer<?>>) Class.forName(className);
            TypeClusterer<?> clu = sclass.newInstance();
            return clu;
        } catch (Exception e) {
            throw new IllegalArgumentException("Not found the clusterer for the class " + className, e);

        }
    }

    public static TypeClusterer<?> getClustererByClass(Class valueClass) {
        TypeClusterer s;
        String sn = valueClass.getSimpleName();
        int tpos = sn.lastIndexOf("Type");
        String tname = ((tpos == -1) ? sn.toString() : sn.subSequence(0, tpos).toString());
        tname = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, tname);
        String className = "es.bsc.aeneas.model.marshalling."
                + tname//it removes the "Type" suffix
                + "Clusterer";
        s = checkNotNull(getClustererByClassName(className));
        return s;
    }
}
