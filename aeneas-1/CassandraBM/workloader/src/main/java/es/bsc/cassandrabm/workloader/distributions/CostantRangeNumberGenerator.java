/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import es.bsc.cassandrabm.workloader.controller.Range;

/**
 *
 * @author cesare
 */
public class CostantRangeNumberGenerator<T extends Number & Comparable>
        implements RangeGenerator<T> {

    private final T from;
    private final T to;

    public CostantRangeNumberGenerator(T from, T to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Range<T> getNextRange() {
        return new Range(from, to);
    }
}
