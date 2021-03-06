//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.14 at 12:06:13 PM CET 
//


package es.bsc.aeneas.model.gen;

import java.util.ArrayList;
import java.util.List;
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
 * <p>Java class for SuperType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="singleType" type="{http://aeneas.bsc.es/CommonTypes}Type"/>
 *         &lt;element name="multiType" type="{http://aeneas.bsc.es/CommonTypes}Type" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperType", namespace = "http://aeneas.bsc.es/CommonTypes", propOrder = {
    "singleType",
    "multiType"
})
public class SuperType
    implements Equals, HashCode, ToString
{

    protected Type singleType;
    protected List<Type> multiType;

    /**
     * Gets the value of the singleType property.
     * 
     * @return
     *     possible object is
     *     {@link Type }
     *     
     */
    public Type getSingleType() {
        return singleType;
    }

    /**
     * Sets the value of the singleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Type }
     *     
     */
    public void setSingleType(Type value) {
        this.singleType = value;
    }

    /**
     * Gets the value of the multiType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multiType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultiType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Type }
     * 
     * 
     */
    public List<Type> getMultiType() {
        if (multiType == null) {
            multiType = new ArrayList<Type>();
        }
        return this.multiType;
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
            Type theSingleType;
            theSingleType = this.getSingleType();
            strategy.appendField(locator, this, "singleType", buffer, theSingleType);
        }
        {
            List<Type> theMultiType;
            theMultiType = (((this.multiType!= null)&&(!this.multiType.isEmpty()))?this.getMultiType():null);
            strategy.appendField(locator, this, "multiType", buffer, theMultiType);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof SuperType)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final SuperType that = ((SuperType) object);
        {
            Type lhsSingleType;
            lhsSingleType = this.getSingleType();
            Type rhsSingleType;
            rhsSingleType = that.getSingleType();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "singleType", lhsSingleType), LocatorUtils.property(thatLocator, "singleType", rhsSingleType), lhsSingleType, rhsSingleType)) {
                return false;
            }
        }
        {
            List<Type> lhsMultiType;
            lhsMultiType = (((this.multiType!= null)&&(!this.multiType.isEmpty()))?this.getMultiType():null);
            List<Type> rhsMultiType;
            rhsMultiType = (((that.multiType!= null)&&(!that.multiType.isEmpty()))?that.getMultiType():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "multiType", lhsMultiType), LocatorUtils.property(thatLocator, "multiType", rhsMultiType), lhsMultiType, rhsMultiType)) {
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
            Type theSingleType;
            theSingleType = this.getSingleType();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "singleType", theSingleType), currentHashCode, theSingleType);
        }
        {
            List<Type> theMultiType;
            theMultiType = (((this.multiType!= null)&&(!this.multiType.isEmpty()))?this.getMultiType():null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "multiType", theMultiType), currentHashCode, theMultiType);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
