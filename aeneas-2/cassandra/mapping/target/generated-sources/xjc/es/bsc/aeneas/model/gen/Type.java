//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.14 at 12:06:13 PM CET 
//


package es.bsc.aeneas.model.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="standardType" type="{http://aeneas.bsc.es/CommonTypes}standardType"/>
 *         &lt;element name="customType" type="{http://aeneas.bsc.es/CommonTypes}customType"/>
 *       &lt;/choice>
 *       &lt;attribute name="collectionType" type="{http://aeneas.bsc.es/CommonTypes}CollectionType" default="SIMPLE_TYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Type", namespace = "http://aeneas.bsc.es/CommonTypes", propOrder = {
    "standardType",
    "customType"
})
@XmlSeeAlso({
    NamedType.class
})
public class Type
    implements Equals, HashCode, ToString
{

    protected StandardType standardType;
    protected String customType;
    @XmlAttribute(name = "collectionType")
    protected CollectionType collectionType;

    /**
     * Gets the value of the standardType property.
     * 
     * @return
     *     possible object is
     *     {@link StandardType }
     *     
     */
    public StandardType getStandardType() {
        return standardType;
    }

    /**
     * Sets the value of the standardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link StandardType }
     *     
     */
    public void setStandardType(StandardType value) {
        this.standardType = value;
    }

    /**
     * Gets the value of the customType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomType() {
        return customType;
    }

    /**
     * Sets the value of the customType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomType(String value) {
        this.customType = value;
    }

    /**
     * Gets the value of the collectionType property.
     * 
     * @return
     *     possible object is
     *     {@link CollectionType }
     *     
     */
    public CollectionType getCollectionType() {
        if (collectionType == null) {
            return CollectionType.SIMPLE_TYPE;
        } else {
            return collectionType;
        }
    }

    /**
     * Sets the value of the collectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectionType }
     *     
     */
    public void setCollectionType(CollectionType value) {
        this.collectionType = value;
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            StandardType theStandardType;
            theStandardType = this.getStandardType();
            strategy.appendField(locator, this, "standardType", buffer, theStandardType);
        }
        {
            String theCustomType;
            theCustomType = this.getCustomType();
            strategy.appendField(locator, this, "customType", buffer, theCustomType);
        }
        {
            CollectionType theCollectionType;
            theCollectionType = this.getCollectionType();
            strategy.appendField(locator, this, "collectionType", buffer, theCollectionType);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof Type)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Type that = ((Type) object);
        {
            StandardType lhsStandardType;
            lhsStandardType = this.getStandardType();
            StandardType rhsStandardType;
            rhsStandardType = that.getStandardType();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "standardType", lhsStandardType), LocatorUtils.property(thatLocator, "standardType", rhsStandardType), lhsStandardType, rhsStandardType)) {
                return false;
            }
        }
        {
            String lhsCustomType;
            lhsCustomType = this.getCustomType();
            String rhsCustomType;
            rhsCustomType = that.getCustomType();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "customType", lhsCustomType), LocatorUtils.property(thatLocator, "customType", rhsCustomType), lhsCustomType, rhsCustomType)) {
                return false;
            }
        }
        {
            CollectionType lhsCollectionType;
            lhsCollectionType = this.getCollectionType();
            CollectionType rhsCollectionType;
            rhsCollectionType = that.getCollectionType();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "collectionType", lhsCollectionType), LocatorUtils.property(thatLocator, "collectionType", rhsCollectionType), lhsCollectionType, rhsCollectionType)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            StandardType theStandardType;
            theStandardType = this.getStandardType();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "standardType", theStandardType), currentHashCode, theStandardType);
        }
        {
            String theCustomType;
            theCustomType = this.getCustomType();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "customType", theCustomType), currentHashCode, theCustomType);
        }
        {
            CollectionType theCollectionType;
            theCollectionType = this.getCollectionType();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "collectionType", theCollectionType), currentHashCode, theCollectionType);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
