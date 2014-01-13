/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.codegenerator.query;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author cesare
 */
public class MetricsList<T> extends ArrayList<T> {

    private final String queryname;
     private void set() {
 
        Metrics.newGauge(MetricsList.class, queryname, varname, new Gauge<Integer>() {

            @Override
            public Integer value() {
                return size();
            }
        });
    }
    private final String varname;

    public MetricsList(String queryname, String varname, Collection<? extends T> c) {
        super(c);
        this.queryname = queryname;
        this.varname = varname;
        set();
    }

    public MetricsList(String queryname, String varname) {
        this.queryname = queryname;
        this.varname = varname;
        set();
    }

    public MetricsList(String queryname, String varname, int initialCapacity) {
        super(initialCapacity);
        this.queryname = queryname;
        this.varname = varname;
        set();
    }
}
