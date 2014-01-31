package es.bsc.cassandrabm.model.util;

import com.google.common.collect.MutableClassToInstanceMap;
import es.bsc.cassandrabm.model.gen.StandardType;
import es.bsc.cassandrabm.model.gen.SuperType;
import es.bsc.cassandrabm.model.gen.Type;
import me.prettyprint.cassandra.serializers.*;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.DynamicComposite;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class Serializers {

    private static final Logger log = Logger.getLogger(Serializers.class.getName());
    /*
     * This is made to workaround that the method get is not in the serializer
     * interface
     */
    private static final MutableClassToInstanceMap< Serializer<?>> serializers = MutableClassToInstanceMap.create();

    static {
        serializers.putInstance(IntegerSerializer.class, IntegerSerializer.get());
        serializers.putInstance(BigIntegerSerializer.class, BigIntegerSerializer.get());
        serializers.putInstance(BooleanSerializer.class, BooleanSerializer.get());
        serializers.putInstance(BytesArraySerializer.class, BytesArraySerializer.get());
        serializers.putInstance(CharSerializer.class, CharSerializer.get());
        serializers.putInstance(CompositeSerializer.class, CompositeSerializer.get());
        serializers.putInstance(DynamicCompositeSerializer.class, DynamicCompositeSerializer.get());
        serializers.putInstance(DateSerializer.class, DateSerializer.get());
        serializers.putInstance(DoubleSerializer.class, DoubleSerializer.get());
        serializers.putInstance(FloatSerializer.class, FloatSerializer.get());
        serializers.putInstance(IntegerSerializer.class, IntegerSerializer.get());
        serializers.putInstance(LongSerializer.class, LongSerializer.get());
        serializers.putInstance(ObjectSerializer.class, ObjectSerializer.get());
        serializers.putInstance(ShortSerializer.class, ShortSerializer.get());
        serializers.putInstance(StringSerializer.class, StringSerializer.get());
        serializers.putInstance(UUIDSerializer.class, UUIDSerializer.get());
        serializers.putInstance(BytesArraySerializer.class, BytesArraySerializer.get());



    }

    public static Serializer<?> getSerializer(Type value) {

        Serializer<?> s;

        if (checkNotNull(value).getStandardType() != null) {
            s = getSerializer(value.getStandardType());
        } else {
            String className = value.getCustomType().subSequence(0, value.getCustomType().length() - 4)
                    + "Serializer";
            s = getSerializerByClassName(className);

        }
        return s;

    }

    public static Serializer<?> getSerializer(SuperType key) {
        if (key.getSingleType() != null) {
            return Serializers.getSerializer(key.getSingleType());
        } else {
            return CompositeSerializer.get();
        }
    }

    @SuppressWarnings("rawtypes")
    public static Serializer<?> getSerializer(StandardType standardType) {
        Serializer<?> s;
        switch (standardType) {
            case ANY_TYPE:
                return DynamicCompositeSerializer.get();
            case UTF_8_TYPE:
                return StringSerializer.get(); //workaround UFT name
            case INT_32_TYPE:
                return IntegerSerializer.get();
            default:
                String className = "me.prettyprint.cassandra.serializers."
                        + standardType.value().subSequence(0, standardType.value().length() - 4) //it removes the "Type" suffix
                        + "Serializer";
                s = getSerializerByClassName(className);
                checkNotNull(s);
                return s;
        }

    }

    public static Serializer<?> getSerializerBySerializerClass(Class<? extends Serializer> cla) {
        Serializer<?> s = serializers.getInstance(cla);
        if (s != null) {
            return s;
        }
        return getSerializerByClassName(cla.getCanonicalName());
    }
    /*
     * Returns the serializer by the serializer class name
     */

    public static Serializer<?> getSerializerByClassName(String classname) {

        try {
            @SuppressWarnings("unchecked")
            Class< Serializer<?>> sclass = (Class<Serializer<?>>) Class.forName(classname);

            Serializer<?> ser = serializers.getInstance(sclass);
            if (ser == null) {
                try {
                    Method method = sclass.getMethod("get");
                    ser = (Serializer<?>) method.invoke(null);
                } catch (NoSuchMethodException nsm) {
                    log.log(Level.INFO, "the class {0} doesn't provide the method get. why?", classname);
                    ser = sclass.newInstance();
                  
                    serializers.putInstance(sclass, ser);
                }


            }
            return ser;

        } catch (Exception e) {
            throw new IllegalArgumentException("Not found the serializer for the class " + classname, e);

        }
    }

    public static Serializer<?> getSerializer(Object instance) {
        if (instance instanceof BigInteger) {
            return BigIntegerSerializer.get();
        }
        if (instance instanceof Integer) {
            return IntegerSerializer.get();
        }
        if (instance instanceof DynamicComposite) {
            log.log(Level.WARNING, "creating a default DynamicCompositeSerializer, any customization will be ignored");
            return DynamicCompositeSerializer.get();

        }
        Serializer<?> ser = SerializerTypeInferer.getSerializer(instance);
        if (ser == null || ser instanceof ObjectSerializer) {
            //trying with the Custom type
            try {
                String classname = instance.getClass().getCanonicalName().replaceAll("Type$", "Serializer");
                ser = getSerializerByClassName(classname);
            } catch (Exception e) {
                //Ignoring the exception
            }


        }
        return checkNotNull(ser, "Serializer class not found");

    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> getSerializer(Class<T> cla) {
        Serializer<T> ser = SerializerTypeInferer.getSerializer(cla);
        if (ser == null || ser instanceof ObjectSerializer) {
            //trying with the Custom type
            try {
                String classname = cla.getCanonicalName().replaceAll("Type$", "Serializer");
                ser = (Serializer<T>) getSerializerByClassName(classname);
            } catch (Exception e) {
            }


        }
        return checkNotNull(ser);

    }

    public static String getTypeName(Serializer<?> s) {
        if (s.getClass().getPackage().getName().equals("me.prettyprint.cassandra.serializers")) {
            return s.getClass().getSimpleName().replace("Serializer", "");
        } else {
            return s.getClass().getSimpleName().replace("Serializer", "Type");
        }
    }

    public static String toString(Serializer<?> s) {
        return s.getClass().getSimpleName().replace("Serializer", "Type");
    }

    private Serializers() {
    }
}
