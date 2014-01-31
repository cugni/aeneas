//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.14 at 12:06:13 PM CET 
//


package es.bsc.aeneas.model.gen;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StrategyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StrategyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SimpleStrategy"/>
 *     &lt;enumeration value="NetworkTopologyStrategy"/>
 *     &lt;enumeration value="OldNetworkTopologyStrategy"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StrategyType", namespace = "http://aeneas.bsc.es/CassandraModel")
@XmlEnum
public enum StrategyType {

    @XmlEnumValue("SimpleStrategy")
    SIMPLE_STRATEGY("SimpleStrategy"),
    @XmlEnumValue("NetworkTopologyStrategy")
    NETWORK_TOPOLOGY_STRATEGY("NetworkTopologyStrategy"),
    @XmlEnumValue("OldNetworkTopologyStrategy")
    OLD_NETWORK_TOPOLOGY_STRATEGY("OldNetworkTopologyStrategy");
    private final String value;

    StrategyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StrategyType fromValue(String v) {
        for (StrategyType c: StrategyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}