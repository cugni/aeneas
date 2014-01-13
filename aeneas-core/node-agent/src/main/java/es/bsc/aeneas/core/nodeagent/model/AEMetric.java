/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.model;

import javax.annotation.Resource;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author ccugnasc
 */
@Resource
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AEMetric {
    private String context;
    private String name;
   

    public AEMetric(String context, String name) {
        this.context = context;
        this.name = name;
        
    }

    public AEMetric() {
    }
 

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
 
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.context != null ? this.context.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AEMetric other = (AEMetric) obj;
        if ((this.context == null) ? (other.context != null) : !this.context.equals(other.context)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return context + ":" + name ;
    }

    
}
