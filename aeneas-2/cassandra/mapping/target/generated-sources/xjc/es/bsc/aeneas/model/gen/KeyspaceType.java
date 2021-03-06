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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 * <p>Java class for KeyspaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KeyspaceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="strategy" type="{http://aeneas.bsc.es/CassandraModel}StrategyType"/>
 *         &lt;element name="replicationFactor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="columnFamilies">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="columnFamily" type="{http://aeneas.bsc.es/CassandraModel}ColumnFamilyType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyspaceType", namespace = "http://aeneas.bsc.es/CassandraModel", propOrder = {
    "strategy",
    "replicationFactor",
    "columnFamilies"
})
public class KeyspaceType
    implements Equals, HashCode, ToString
{

    @XmlElement(required = true)
    protected StrategyType strategy;
    protected int replicationFactor;
    @XmlElement(required = true)
    protected KeyspaceType.ColumnFamilies columnFamilies;
    @XmlAttribute(name = "name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

    /**
     * Gets the value of the strategy property.
     * 
     * @return
     *     possible object is
     *     {@link StrategyType }
     *     
     */
    public StrategyType getStrategy() {
        return strategy;
    }

    /**
     * Sets the value of the strategy property.
     * 
     * @param value
     *     allowed object is
     *     {@link StrategyType }
     *     
     */
    public void setStrategy(StrategyType value) {
        this.strategy = value;
    }

    /**
     * Gets the value of the replicationFactor property.
     * 
     */
    public int getReplicationFactor() {
        return replicationFactor;
    }

    /**
     * Sets the value of the replicationFactor property.
     * 
     */
    public void setReplicationFactor(int value) {
        this.replicationFactor = value;
    }

    /**
     * Gets the value of the columnFamilies property.
     * 
     * @return
     *     possible object is
     *     {@link KeyspaceType.ColumnFamilies }
     *     
     */
    public KeyspaceType.ColumnFamilies getColumnFamilies() {
        return columnFamilies;
    }

    /**
     * Sets the value of the columnFamilies property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyspaceType.ColumnFamilies }
     *     
     */
    public void setColumnFamilies(KeyspaceType.ColumnFamilies value) {
        this.columnFamilies = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
            StrategyType theStrategy;
            theStrategy = this.getStrategy();
            strategy.appendField(locator, this, "strategy", buffer, theStrategy);
        }
        {
            int theReplicationFactor;
            theReplicationFactor = (true?this.getReplicationFactor(): 0);
            strategy.appendField(locator, this, "replicationFactor", buffer, theReplicationFactor);
        }
        {
            KeyspaceType.ColumnFamilies theColumnFamilies;
            theColumnFamilies = this.getColumnFamilies();
            strategy.appendField(locator, this, "columnFamilies", buffer, theColumnFamilies);
        }
        {
            String theName;
            theName = this.getName();
            strategy.appendField(locator, this, "name", buffer, theName);
        }
        return buffer;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof KeyspaceType)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final KeyspaceType that = ((KeyspaceType) object);
        {
            StrategyType lhsStrategy;
            lhsStrategy = this.getStrategy();
            StrategyType rhsStrategy;
            rhsStrategy = that.getStrategy();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "strategy", lhsStrategy), LocatorUtils.property(thatLocator, "strategy", rhsStrategy), lhsStrategy, rhsStrategy)) {
                return false;
            }
        }
        {
            int lhsReplicationFactor;
            lhsReplicationFactor = (true?this.getReplicationFactor(): 0);
            int rhsReplicationFactor;
            rhsReplicationFactor = (true?that.getReplicationFactor(): 0);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "replicationFactor", lhsReplicationFactor), LocatorUtils.property(thatLocator, "replicationFactor", rhsReplicationFactor), lhsReplicationFactor, rhsReplicationFactor)) {
                return false;
            }
        }
        {
            KeyspaceType.ColumnFamilies lhsColumnFamilies;
            lhsColumnFamilies = this.getColumnFamilies();
            KeyspaceType.ColumnFamilies rhsColumnFamilies;
            rhsColumnFamilies = that.getColumnFamilies();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "columnFamilies", lhsColumnFamilies), LocatorUtils.property(thatLocator, "columnFamilies", rhsColumnFamilies), lhsColumnFamilies, rhsColumnFamilies)) {
                return false;
            }
        }
        {
            String lhsName;
            lhsName = this.getName();
            String rhsName;
            rhsName = that.getName();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "name", lhsName), LocatorUtils.property(thatLocator, "name", rhsName), lhsName, rhsName)) {
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
            StrategyType theStrategy;
            theStrategy = this.getStrategy();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "strategy", theStrategy), currentHashCode, theStrategy);
        }
        {
            int theReplicationFactor;
            theReplicationFactor = (true?this.getReplicationFactor(): 0);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "replicationFactor", theReplicationFactor), currentHashCode, theReplicationFactor);
        }
        {
            KeyspaceType.ColumnFamilies theColumnFamilies;
            theColumnFamilies = this.getColumnFamilies();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "columnFamilies", theColumnFamilies), currentHashCode, theColumnFamilies);
        }
        {
            String theName;
            theName = this.getName();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "name", theName), currentHashCode, theName);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="columnFamily" type="{http://aeneas.bsc.es/CassandraModel}ColumnFamilyType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "columnFamily"
    })
    public static class ColumnFamilies
        implements Equals, HashCode, ToString
    {

        @XmlElement(namespace = "http://aeneas.bsc.es/CassandraModel", required = true)
        protected List<ColumnFamilyType> columnFamily;

        /**
         * Gets the value of the columnFamily property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the columnFamily property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getColumnFamily().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ColumnFamilyType }
         * 
         * 
         */
        public List<ColumnFamilyType> getColumnFamily() {
            if (columnFamily == null) {
                columnFamily = new ArrayList<ColumnFamilyType>();
            }
            return this.columnFamily;
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
                List<ColumnFamilyType> theColumnFamily;
                theColumnFamily = (((this.columnFamily!= null)&&(!this.columnFamily.isEmpty()))?this.getColumnFamily():null);
                strategy.appendField(locator, this, "columnFamily", buffer, theColumnFamily);
            }
            return buffer;
        }

        public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
            if (!(object instanceof KeyspaceType.ColumnFamilies)) {
                return false;
            }
            if (this == object) {
                return true;
            }
            final KeyspaceType.ColumnFamilies that = ((KeyspaceType.ColumnFamilies) object);
            {
                List<ColumnFamilyType> lhsColumnFamily;
                lhsColumnFamily = (((this.columnFamily!= null)&&(!this.columnFamily.isEmpty()))?this.getColumnFamily():null);
                List<ColumnFamilyType> rhsColumnFamily;
                rhsColumnFamily = (((that.columnFamily!= null)&&(!that.columnFamily.isEmpty()))?that.getColumnFamily():null);
                if (!strategy.equals(LocatorUtils.property(thisLocator, "columnFamily", lhsColumnFamily), LocatorUtils.property(thatLocator, "columnFamily", rhsColumnFamily), lhsColumnFamily, rhsColumnFamily)) {
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
                List<ColumnFamilyType> theColumnFamily;
                theColumnFamily = (((this.columnFamily!= null)&&(!this.columnFamily.isEmpty()))?this.getColumnFamily():null);
                currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "columnFamily", theColumnFamily), currentHashCode, theColumnFamily);
            }
            return currentHashCode;
        }

        public int hashCode() {
            final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
            return this.hashCode(null, strategy);
        }

    }

}
