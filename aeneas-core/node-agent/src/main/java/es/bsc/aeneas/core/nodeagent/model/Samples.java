/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.model;

import java.util.List;
import javax.annotation.Resource;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ccugnasc
 */
@Resource
public class Samples {

    @JsonProperty
    private String test;
    @JsonProperty
    private AEMetric metric;
    @JsonProperty
    private String node;
    @JsonProperty
    private List<Long> dates;
    @JsonProperty
    private List values;

    public Samples() {
    }

    public Samples(String test, AEMetric metric, String nodes, List<Long> dates, List values) {
        this.test = test;
        this.metric = metric;
        this.node = nodes;
        this.dates = dates;
        this.values = values;
    }

    public Samples(String test, String context, String metric, String node, List<Long> dates, List values) {
        this.test = test;
        this.metric = new AEMetric(context, metric);
        this.node = node;
        this.dates = dates;
        this.values = values;
    }

    public Samples(String test, AEMetric metric, String node) {
        this.test = test;
        this.metric = metric;
        this.node = node;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public AEMetric getMetric() {
        return metric;
    }

    public void setMetric(AEMetric metric) {
        this.metric = metric;
    }

    public List<Long> getDates() {
        return dates;
    }

    public void setDates(List<Long> dates) {
        this.dates = dates;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Samples{" + "test=" + test + ", metric=" + metric + ", nodes=" + node + ", dates=" + dates + ", values=" + values + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.test != null ? this.test.hashCode() : 0);
        hash = 67 * hash + (this.metric != null ? this.metric.hashCode() : 0);
        hash = 67 * hash + (this.node != null ? this.node.hashCode() : 0);
        hash = 67 * hash + (this.dates != null ? this.dates.hashCode() : 0);
        hash = 67 * hash + (this.values != null ? this.values.hashCode() : 0);
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
        final Samples other = (Samples) obj;
        if ((this.test == null) ? (other.test != null) : !this.test.equals(other.test)) {
            return false;
        }
        if (this.metric != other.metric && (this.metric == null || !this.metric.equals(other.metric))) {
            return false;
        }
        if ((this.node == null) ? (other.node != null) : !this.node.equals(other.node)) {
            return false;
        }
        if (this.dates != other.dates && (this.dates == null || !this.dates.equals(other.dates))) {
            return false;
        }
        return true;
    }
}