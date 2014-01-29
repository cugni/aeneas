package es.bsc.aeneas.core.model.util;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkNotNull;
import es.bsc.aeneas.core.model.gen.RootType;
import es.bsc.aeneas.core.model.gen.StandardType;
import es.bsc.aeneas.core.model.gen.Type;
import es.bsc.aeneas.core.model.gen.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

/**
 *
 *
 * @author cesare
 */
public class GenUtils {

    private static final Logger log = Logger.getLogger(GenUtils.class.getCanonicalName());
    @Inject
    private URL referenceModel;
    // private static final URL queryModel = cl.getResource(CUtils.getString("querymodel", "queryModel.xml"));
    private URL refmodelXSD = ClassLoader.getSystemClassLoader().getResource("ReferenceModel.xsd");
    @Inject
    private URL matchingModelSchema;
    private static RootType root = null;

    public RootType getReferenceModel() {
        if (root != null) {
            return root;
        }
        try {
            Unmarshaller um = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(
                    javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            um.setSchema(sf.newSchema(refmodelXSD));
            @SuppressWarnings("unchecked")
            JAXBElement<RootType> jk =
                    (JAXBElement<RootType>) um.unmarshal(referenceModel);
            root = jk.getValue();
            return root;
        } catch (Exception ex) {
            Logger.getLogger(GenUtils.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public static boolean compareTypes(Type t1, Type t2) {
        if (t1.getStandardType() != null) {
            if (t2.getStandardType() != null) {
                return t1.getStandardType().equals(t2.getStandardType());
            } else {
                return false;
            }

        } else {
            String ct = checkNotNull(t1.getCustomType(), "Not initialized type");
            if (t2.getCustomType() != null) {
                return ct.equals(t2.getCustomType());

            } else {
                return false;
            }

        }

    }
    private final JAXBContext jc;

    {
        try {
            jc = JAXBContext.newInstance("es.bsc.aeneas.core.model.gen");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

//    
    public EnvType getMatchingModel(URL matchingFile) {
        try {
            checkNotNull(matchingFile);
//            checkArgument(cassandraModelFile.exists(), "cassandraModel not found. {0}", cassandraModelFile.getAbsolutePath());
            Unmarshaller um = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(
                    javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            um.setSchema(sf.newSchema(matchingModelSchema));
            @SuppressWarnings("unchecked")
            JAXBElement<EnvType> jk =
                    (JAXBElement<EnvType>) um.unmarshal(matchingFile);
            return jk.getValue();
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    public static Class<?> getClass(Type st) {

        if (checkNotNull(st).getStandardType() != null) {
            return getClass(st.getStandardType());
        } else {
            try {
                return Class.forName(st.getCustomType());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GenUtils.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalArgumentException("Class not found: " + st.getCustomType(), ex);
            }
        }
    }

    public static StandardType getStandardType(Class<?> cla) {
        if (cla.equals(String.class)) {
            return StandardType.UTF_8_TYPE;
        }
        if (cla.equals(Integer.class)) {
            return StandardType.INT_32_TYPE;
        } else if (cla.equals(BigInteger.class)) {
            return StandardType.BIG_INTEGER_TYPE;
        }
        if (cla.equals(UUID.class)) {
            return StandardType.LEXICAL_UUID_TYPE; //TODO this is a possible bug
        }
        if (cla.equals(Long.class)) {
            return StandardType.LONG_TYPE;
        }
        if (cla.equals(Double.class)) {
            return StandardType.DOUBLE_TYPE;
        }
        if (cla.equals(UUID.class)) {
            return StandardType.UUID_TYPE;
        }
        throw new UnsupportedOperationException("Impossible to find the corresponding standardtype class for " + cla.getName());
    }

    public static Class<?> getClass(StandardType st) {
        switch (checkNotNull(st)) {
            case ASCII_TYPE:
                return String.class;
            case UTF_8_TYPE:
                return String.class;
            case INT_32_TYPE:
                return Integer.class;
            case BIG_INTEGER_TYPE:
                return BigInteger.class;
            case LEXICAL_UUID_TYPE:
                return UUID.class;
            case DOUBLE_TYPE:
                return Double.class;
            case LONG_TYPE:
                return Long.class;
            case TIME_UUID_TYPE:
                return UUID.class;
            case ANY_TYPE:
                throw new UnsupportedOperationException("To be refactored"); //DynamicComposite.class;
            /*
             * } else if (val.equals("DynamicCompositeType") { } else if
             * (val.equals("UUIDType") {
             */
            default:
                throw new UnsupportedOperationException("Unsupported type " + st.name());
        }
    }

    public static Object castObject(String oj, StandardType st) {
        checkNotNull(st);
        switch (st) {
            case ASCII_TYPE:
                return oj.getBytes(Charsets.US_ASCII);
            case UTF_8_TYPE:
                return oj;
            case INT_32_TYPE:
                return Integer.parseInt(oj);
            case BIG_INTEGER_TYPE:
                return BigInteger.valueOf(Integer.parseInt(oj));
            case LEXICAL_UUID_TYPE:
                return UUID.fromString(oj);
            case LONG_TYPE:
                return Long.parseLong(oj);
            case TIME_UUID_TYPE:
                return UUID.fromString(oj);

            /*
             * } else if (val.equals("DynamicCompositeType") { } else if
             * (val.equals("UUIDType") {
             */
            default:
                throw new UnsupportedOperationException("Unsupported ");
        }
    }

    /**
     * Return an Aeneas model's type from a java Class Object
     *
     * @param cla
     * @return The Aeneas' type
     */
    public static Type getTypeFromClass(Class<?> cla) {
        StandardType st = getStandardTypeFromClass(cla);
        Type t = new Type();
        if (st != null) {
            t.setStandardType(st);
            return t;
        } else {
            t.setCustomType(cla.getCanonicalName());
            return t;
        }
    }

    public static StandardType getStandardTypeFromClass(Class<?> cla) {
        if (checkNotNull(cla).equals(String.class)) {
            return StandardType.UTF_8_TYPE;
        } else if (cla.equals(Integer.class) || cla.equals(int.class)) {
            return StandardType.INT_32_TYPE;
        } else if (cla.equals(BigInteger.class)) {
            return StandardType.BIG_INTEGER_TYPE;
        } else if (cla.equals(UUID.class)) {
            return StandardType.TIME_UUID_TYPE;
        } else if (cla.equals((Double.class)) || cla.equals(double.class)) {
            return StandardType.DOUBLE_TYPE;
        } else if (cla.equals(Long.class) || cla.equals(long.class)) {
            return StandardType.LONG_TYPE;
        } else {
            return null;
        }
    }
}
