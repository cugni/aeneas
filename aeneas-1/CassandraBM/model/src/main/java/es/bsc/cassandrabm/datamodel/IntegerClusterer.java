/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.datamodel;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 *
 * @author cesare
 */
public class IntegerClusterer extends TypeClusterer<Integer> {

    private int nmask, pmask;
    private final static float ln2 = (float) Math.log(2);
    /*
     * This class is not multithread. The method setGrouping should be called
     * only on the instantialization of the
     */

    @Override
    public void setGrouping(Integer from, Integer to, Integer intervals) {
        if (Math.abs(from - to) < intervals) {
            throw new IllegalArgumentException("The interval must be minor than |from-to|");
        }

        int size_int = Math.abs(to - from) / intervals;
        int pleft = (int) Math.round((Math.log(size_int) / ln2) + 0.5);
        pmask = (1 << pleft);
        nmask = ~(pmask - 1);




    }

    @Override
    public Integer getGroup(Integer value) {
        return value & nmask;
    }

    @Override
    public List<Integer> getGroupsInterval(Integer from, Integer to) {
        Integer gf = getGroup(from);
        Integer gt = getGroup(to);
        ImmutableList.Builder<Integer> l = ImmutableList.builder();
        for (int ix = gf; ix <=gt ; ix+=pmask) {
            l.add(ix);
        }
        return l.build();
    }

    @Override
    public Integer parseFromString(String s) {
        return Integer.parseInt(s);
    }
}
