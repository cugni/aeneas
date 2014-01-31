/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

/**
 *
 * @author cesare
 */
public abstract class NumberDistribution<T extends Number & Comparable> extends NumberGenerator<T> {

    protected final T from;
    protected final T to;

    public NumberDistribution(T from, T to) {
        this.from =from;
        this.to = to;

    }
    private T last;

    protected T setLast(T last) {
        this.last = last;
        return last;
    }

    @Override
    public T last() {
        return last;
    }
}
