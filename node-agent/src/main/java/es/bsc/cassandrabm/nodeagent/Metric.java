/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ccugnasc
 */
public class Metric {
    private static final Logger LOG = Logger.getLogger(Metric.class.getName());
    

    public static MetricGroup Metric(String group) {
        return new MetricGroup(group);
    }
     public static MetricGroup Metric(String group,List<Metric> l) {
        return new MetricGroup(group,l);
    }

    private final String group;
    private final String name;
    private final Object value;

    public Metric(String group, String name, Object value) {
        this.group = group;
        this.name = name;
        this.value = value;
    }

    public Metric(String name, Object value) {
        this.group = null;
        this.name = name;
        this.value = value;
    }
    

    public String getName() {
        return name;
    }
     public Object getValue() {
        return value;
    }

    public String getGroup() {
        return group;
    }
    @Override
    public String toString(){
        return group+"."+name;
    }
    public boolean isGrouped() {
        return group != null;
    }

    public static class MetricGroup {

        private final List<Metric> l;
        private final String group;

        MetricGroup(String group) {
            this.group = group;
            l = new ArrayList<Metric>();
        }

        MetricGroup(String group, List<Metric> list) {
            this.group = group;
            l = list;
        }

        public MetricGroup addMetric(String name, Object value) {
            l.add(new Metric(group, name, value));
            return this;
        }

        public List<Metric> getList() {
            return l;
        }
    }
}
