/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.model.util;

import es.bsc.aeneas.core.model.gen.ClustererType;
import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.gen.TransformType;
import es.bsc.aeneas.core.model.clusterer.TypeClusterer;
import es.bsc.aeneas.core.model.gen.StandardType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ccugnasc
 */
public class TransformerUtil {

    private static final ConcurrentHashMap<String, THolder> classMap = new ConcurrentHashMap<String, THolder>();

    /**
     * This method apply the all the chain of transformation defined in the
     * DestType to the provided object. The transformation are made following
     * their list order.
     *
     * @param dt Destination type
     * @param o the object to transform
     * @return the transformed value
     */
    public static Object transform(DestType dt, Object o) {
        THolder th;
        Object res = o;
        Class calling = String.class;
        for (TransformType tra : dt.getTransform()) {
            String methodId = tra.getClassName() + "#" + tra.getMethodName() + "(" + calling.getName() + ")";
            if (classMap.containsKey(methodId)) {
                th = classMap.get(methodId);
            } else {
                th = new THolder(tra, calling);
                classMap.put(methodId, th);
            }
            res = th.invoke(res);
            calling = res.getClass();

        }
        return res;

    }
    /***
     * This method resolves, for a given DestType, the chain of transformations
     * are returns the StandardType corresponding to the returning object. 
     * @param dt
     * @param levelType
     * @return the StandardType of the returning object
     */

    public static StandardType getReturningStandardType(DestType dt, StandardType levelType) {

        if (dt.getTransform().isEmpty()) {
            return levelType;
        }
        return GenUtils
                .getStandardTypeFromClass(
                getReturningClass(dt, levelType));

    }

    /***
     * This method resolves, for a given DestType, the chain of transformations
     * are returns the Class of  the returning object. 
     * @param dt
     * @param levelType
     * @return the Class of the returning object
     */
    public static Class getReturningClass(DestType dt, StandardType levelType) {
        Class calling = GenUtils.getClass(levelType);
        if (dt.getTransform().isEmpty()) {
            return calling;
        }
        THolder th;
        for (TransformType tra : dt.getTransform()) {
            String methodId = tra.getClassName() + "#" + tra.getMethodName() + "(" + calling.getName() + ")";
            if (classMap.containsKey(methodId)) {
                th = classMap.get(methodId);
            } else {
                th = new THolder(tra, calling);
                classMap.put(methodId, th);
            }
            calling = th.retClass;

        }
        return calling;

    }

    private static class THolder {

        THolder(TransformType tra, Class calling) {
            try {

                cla = Class.forName(tra.getClassName());

                method = cla.getMethod(tra.getMethodName(), calling);
                retClass = method.getReturnType();
                isStatic = Modifier.isStatic(method.getModifiers());
                obj = (isStatic) ? null : cla.newInstance();
                if (tra instanceof ClustererType) {
                    ClustererType ct = (ClustererType) tra;
                    TypeClusterer tc = (TypeClusterer) obj;
                    tc.setGrouping(ct.getFrom(), ct.getTo(), ct.getIntervals().intValue());


                }
            } catch (Exception ex) {
                throw new IllegalStateException("Exception creating "
                        + "the trasforming class "
                        + tra.getClassName() + "#" + tra.getMethodName(), ex);
            }
        }
        private final boolean isStatic;
        private final Class cla;
        private final Object obj;
        private final Method method;
        private final Class retClass;

        private Object invoke(Object o) {
            try {
                if (isStatic) {
                    return method.invoke(cla, o);
                } else {
                    return method.invoke(obj, o);
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Exception invoking "
                        + "the trasforming class "
                        + method.getName(), ex);
            }
        }
    }
}
