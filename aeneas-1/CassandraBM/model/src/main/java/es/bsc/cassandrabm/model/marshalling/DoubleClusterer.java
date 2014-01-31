/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.model.marshalling;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 *
 * @author cesare
 */
public class DoubleClusterer extends TypeClusterer<Double> {

    private double xmul, xmin, ncluster;
    /*
     * This class is not multithread. The method setGrouping should be called
     * only on the instantialization of the
     */
    @Override
    public void setGrouping(Double from, Double to, Integer intervals) {
        ncluster = intervals;
        xmin = Math.abs(Math.abs(from) > Math.abs(to) ? to : from);
        xmul = scale(from, to);


    }

    private double scale(double from, double to) {
        if (from > to) {
            return ncluster / Math.abs(from / to);
        } else {
            return ncluster / Math.abs(to / from);
        }
    }

    @Override
    public Double getGroup(Double value) {
        return Math.floor(value / xmin * xmul);
    }

    @Override
    public List<Double> getGroupsInterval(Double from, Double to) {
        Double gf = getGroup(from);
        Double gt = getGroup(to);
        int x = (int) Math.round(gt - gf);

        int adde = x >= 0 ? 1 : -1;
        ImmutableList.Builder<Double> l = ImmutableList.builder();
        for (int ix = 0; ix <= x * adde; ix++) {
            l.add(gf + ix * adde);
        }
        return l.build();
    }

    @Override
    public Double parseFromString(String s) {
        return Double.parseDouble(s);
    }
}
