//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.29 at 04:50:08 PM CET 
//


package es.bsc.aeneas.core.model.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.bsc.aeneas.core.model.gen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Env_QNAME = new QName("http://aeneas.bsc.es/MatchingModel", "env");
    private final static QName _Root_QNAME = new QName("http://aeneas.bsc.es/ReferenceModel", "root");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.bsc.aeneas.core.model.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MatchType }
     * 
     */
    public MatchType createMatchType() {
        return new MatchType();
    }

    /**
     * Create an instance of {@link EnvType }
     * 
     */
    public EnvType createEnvType() {
        return new EnvType();
    }

    /**
     * Create an instance of {@link FixedDest }
     * 
     */
    public FixedDest createFixedDest() {
        return new FixedDest();
    }

    /**
     * Create an instance of {@link LevelType }
     * 
     */
    public LevelType createLevelType() {
        return new LevelType();
    }

    /**
     * Create an instance of {@link ClType }
     * 
     */
    public ClType createClType() {
        return new ClType();
    }

    /**
     * Create an instance of {@link TransformType }
     * 
     */
    public TransformType createTransformType() {
        return new TransformType();
    }

    /**
     * Create an instance of {@link RefPathType }
     * 
     */
    public RefPathType createRefPathType() {
        return new RefPathType();
    }

    /**
     * Create an instance of {@link ClustererType }
     * 
     */
    public ClustererType createClustererType() {
        return new ClustererType();
    }

    /**
     * Create an instance of {@link NamedType }
     * 
     */
    public NamedType createNamedType() {
        return new NamedType();
    }

    /**
     * Create an instance of {@link Type }
     * 
     */
    public Type createType() {
        return new Type();
    }

    /**
     * Create an instance of {@link SuperType }
     * 
     */
    public SuperType createSuperType() {
        return new SuperType();
    }

    /**
     * Create an instance of {@link FixedType }
     * 
     */
    public FixedType createFixedType() {
        return new FixedType();
    }

    /**
     * Create an instance of {@link RootType }
     * 
     */
    public RootType createRootType() {
        return new RootType();
    }

    /**
     * Create an instance of {@link ComplexValueType }
     * 
     */
    public ComplexValueType createComplexValueType() {
        return new ComplexValueType();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link EntityType }
     * 
     */
    public EntityType createEntityType() {
        return new EntityType();
    }

    /**
     * Create an instance of {@link es.bsc.aeneas.core.model.gen.ClusterType.Matches }
     * 
     */
    public es.bsc.aeneas.core.model.gen.ClusterType.Matches createClusterTypeMatches() {
        return new es.bsc.aeneas.core.model.gen.ClusterType.Matches();
    }

    /**
     * Create an instance of {@link MatchType.FixedDests }
     * 
     */
    public MatchType.FixedDests createMatchTypeFixedDests() {
        return new MatchType.FixedDests();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://aeneas.bsc.es/MatchingModel", name = "env")
    public JAXBElement<EnvType> createEnv(EnvType value) {
        return new JAXBElement<EnvType>(_Env_QNAME, EnvType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RootType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://aeneas.bsc.es/ReferenceModel", name = "root")
    public JAXBElement<RootType> createRoot(RootType value) {
        return new JAXBElement<RootType>(_Root_QNAME, RootType.class, null, value);
    }

}