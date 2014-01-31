//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.27 at 01:34:48 PM CET 
//


package es.bsc.aeneas.core.model.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 * <p>Java class for CassandraMatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CassandraMatchType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://aeneas.bsc.es/MatchingModel}MatchType">
 *       &lt;attribute name="columnFamilyName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="keyspaceName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CassandraMatchType", namespace = "http://aeneas.bsc.es/CassandraModel")
public class CassandraMatchType
    extends MatchType
    implements Equals, HashCode, ToString
{

    @XmlAttribute(name = "columnFamilyName")
    protected String columnFamilyName;
    @XmlAttribute(name = "keyspaceName")
    protected String keyspaceName;
    @XmlAttribute(name = "index")
    protected Boolean index;

    /**
     * Gets the value of the columnFamilyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    /**
     * Sets the value of the columnFamilyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnFamilyName(String value) {
        this.columnFamilyName = value;
    }

    /**
     * Gets the value of the keyspaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyspaceName() {
        return keyspaceName;
    }

    /**
     * Sets the value of the keyspaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyspaceName(String value) {
        this.keyspaceName = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIndex() {
        if (index == null) {
            return false;
        } else {
            return index;
        }
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndex(Boolean value) {
        this.index = value;
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
        super.appendFields(locator, buffer, strategy);
        {
            String theColumnFamilyName;
            theColumnFamilyName = this.getColumnFamilyName();
            strategy.appendField(locator, this, "columnFamilyName", buffer, theColumnFamilyName);
        }
        {
            String theKeyspaceName;
            theKeyspaceName = this.getKeyspaceName();
            strategy.appendField(locator, this, "keyspaceName", buffer, theKeyspaceName);
        }
        {
            boolean theIndex;
            theIndex = ((this.index!= null)?this.isIndex():false);
            strategy.appendField(locator, this, "index", buffer, theIndex);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof CassandraMatchType)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!super.equals(thisLocator, thatLocator, object, strategy)) {
            return false;
        }
        final CassandraMatchType that = ((CassandraMatchType) object);
        {
            String lhsColumnFamilyName;
            lhsColumnFamilyName = this.getColumnFamilyName();
            String rhsColumnFamilyName;
            rhsColumnFamilyName = that.getColumnFamilyName();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "columnFamilyName", lhsColumnFamilyName), LocatorUtils.property(thatLocator, "columnFamilyName", rhsColumnFamilyName), lhsColumnFamilyName, rhsColumnFamilyName)) {
                return false;
            }
        }
        {
            String lhsKeyspaceName;
            lhsKeyspaceName = this.getKeyspaceName();
            String rhsKeyspaceName;
            rhsKeyspaceName = that.getKeyspaceName();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "keyspaceName", lhsKeyspaceName), LocatorUtils.property(thatLocator, "keyspaceName", rhsKeyspaceName), lhsKeyspaceName, rhsKeyspaceName)) {
                return false;
            }
        }
        {
            boolean lhsIndex;
            lhsIndex = ((this.index!= null)?this.isIndex():false);
            boolean rhsIndex;
            rhsIndex = ((that.index!= null)?that.isIndex():false);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "index", lhsIndex), LocatorUtils.property(thatLocator, "index", rhsIndex), lhsIndex, rhsIndex)) {
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
        int currentHashCode = super.hashCode(locator, strategy);
        {
            String theColumnFamilyName;
            theColumnFamilyName = this.getColumnFamilyName();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "columnFamilyName", theColumnFamilyName), currentHashCode, theColumnFamilyName);
        }
        {
            String theKeyspaceName;
            theKeyspaceName = this.getKeyspaceName();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "keyspaceName", theKeyspaceName), currentHashCode, theKeyspaceName);
        }
        {
            boolean theIndex;
            theIndex = ((this.index!= null)?this.isIndex():false);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "index", theIndex), currentHashCode, theIndex);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}