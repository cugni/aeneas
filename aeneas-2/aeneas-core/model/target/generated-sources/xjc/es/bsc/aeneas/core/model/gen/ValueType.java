//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.29 at 04:50:08 PM CET 
//


package es.bsc.aeneas.core.model.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 * <p>Java class for valueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="valueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="simpleValue" type="{http://aeneas.bsc.es/CommonTypes}Type"/>
 *         &lt;element name="nodeValue" type="{http://aeneas.bsc.es/ReferenceModel}complexValueType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueType", namespace = "http://aeneas.bsc.es/ReferenceModel", propOrder = {
    "simpleValue",
    "nodeValue"
})
public class ValueType
    implements Equals, HashCode, ToString
{

    protected Type simpleValue;
    protected ComplexValueType nodeValue;

    /**
     * Gets the value of the simpleValue property.
     * 
     * @return
     *     possible object is
     *     {@link Type }
     *     
     */
    public Type getSimpleValue() {
        return simpleValue;
    }

    /**
     * Sets the value of the simpleValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Type }
     *     
     */
    public void setSimpleValue(Type value) {
        this.simpleValue = value;
    }

    /**
     * Gets the value of the nodeValue property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexValueType }
     *     
     */
    public ComplexValueType getNodeValue() {
        return nodeValue;
    }

    /**
     * Sets the value of the nodeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexValueType }
     *     
     */
    public void setNodeValue(ComplexValueType value) {
        this.nodeValue = value;
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
            Type theSimpleValue;
            theSimpleValue = this.getSimpleValue();
            strategy.appendField(locator, this, "simpleValue", buffer, theSimpleValue);
        }
        {
            ComplexValueType theNodeValue;
            theNodeValue = this.getNodeValue();
            strategy.appendField(locator, this, "nodeValue", buffer, theNodeValue);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof ValueType)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final ValueType that = ((ValueType) object);
        {
            Type lhsSimpleValue;
            lhsSimpleValue = this.getSimpleValue();
            Type rhsSimpleValue;
            rhsSimpleValue = that.getSimpleValue();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "simpleValue", lhsSimpleValue), LocatorUtils.property(thatLocator, "simpleValue", rhsSimpleValue), lhsSimpleValue, rhsSimpleValue)) {
                return false;
            }
        }
        {
            ComplexValueType lhsNodeValue;
            lhsNodeValue = this.getNodeValue();
            ComplexValueType rhsNodeValue;
            rhsNodeValue = that.getNodeValue();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "nodeValue", lhsNodeValue), LocatorUtils.property(thatLocator, "nodeValue", rhsNodeValue), lhsNodeValue, rhsNodeValue)) {
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
            Type theSimpleValue;
            theSimpleValue = this.getSimpleValue();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "simpleValue", theSimpleValue), currentHashCode, theSimpleValue);
        }
        {
            ComplexValueType theNodeValue;
            theNodeValue = this.getNodeValue();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "nodeValue", theNodeValue), currentHashCode, theNodeValue);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
