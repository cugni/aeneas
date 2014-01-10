package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.model.gen.ClustererType;
import es.bsc.aeneas.model.gen.Dest;
import es.bsc.aeneas.model.gen.StandardType;
import es.bsc.aeneas.model.gen.Type;
import es.bsc.aeneas.model.marshalling.TypeClusterer;
import es.bsc.aeneas.model.util.Clusterers;
import es.bsc.aeneas.model.util.GenUtils;
import me.prettyprint.hector.api.beans.DynamicComposite;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
/*
 * This class encapsulates the trasformation required to insert a value such as
 * retrive an attribute or clusterer the value
 * 
 */
public class Tr {

    private static final Logger LOG = Logger.getLogger(Tr.class.getName());
    private final TType type;
    public final Class valueClass;
    private final Method method;
    private final TypeClusterer<?> clusterer;
    final boolean anytype;

    Tr(Dest dest, Type e) {
        if (e.getStandardType() != null && 
                e.getStandardType().equals(StandardType.ANY_TYPE)) {
            anytype = true;
        } else {
            anytype = false;
        }


        if (dest.getAttr().isEmpty()) {

            valueClass = GenUtils.getClass(checkNotNull(e));
            method = null;
            if (dest.getClusterer() == null) {
                type = TType.SIMPLE;
                clusterer = null;
            } else {
                type = TType.CLUSTERING;
                clusterer = getClusterer(dest.getClusterer());

            }
        } else {
            Class tmp = GenUtils.getClass(e);
            try {
                method = tmp.getMethod(dest.getAttr());

            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("The method " + dest.getAttr() + " on the class " + tmp
                        + " doesn't exists.", ex);
            } catch (SecurityException ex) {
                throw new IllegalArgumentException(ex);
            }
            valueClass = method.getReturnType();
            if (dest.getClusterer() == null) {
                type = TType.ATTR;

                clusterer = null;
            } else {
                type = TType.ATTR_CLUSTERING;
                clusterer = getClusterer(dest.getClusterer());
            }

        }
    }

    public Object transform(Object o) {
        try {
            Object retu;
            switch (type) {
                case SIMPLE:
                    retu = o;
                    break;
                case CLUSTERING:
                    retu = clusterer.getGroupGeneric(o);
                    break;
                case ATTR:
                    retu = method.invoke(o);
                    break;
                case ATTR_CLUSTERING:
                default:
                    retu = clusterer.getGroupGeneric(method.invoke(o));
                    break;
            }
            if (anytype) {
                return new DynamicComposite(retu);
            } else {
                return retu;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception invoking the method " + method.getName()
                    + "on the object " + o.toString(), ex);
        }
    }

    private TypeClusterer<?> getClusterer(ClustererType clusterer) {

        TypeClusterer<?> tc;
        if (clusterer.getClassName() == null) {
            tc = Clusterers.getClustererByClass(valueClass);
        } else {
            tc = Clusterers.getClustererByClassName(clusterer.getClassName());
        }
        tc.setGrouping(clusterer.getFrom(),
                clusterer.getTo(),
                clusterer.getIntervals().intValue());

        return tc;

    }

    private enum TType {

        SIMPLE,
        ANYTYPE,
        CLUSTERING,
        ATTR,
        ATTR_CLUSTERING
    }
}
