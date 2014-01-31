/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.commons;

/**
 *
 * @author ccugnasc
 */
public class MemMax<T extends Comparable> {

    T max = null;
    private final T resetVal;
    public MemMax(T resetVal){
        this.resetVal=resetVal;
        max=resetVal;
    }

    public T add(T val) {
        if (max == null || max.compareTo(val) < 0) {
            max = val;
        }

        return val;
    }

    public T getMax() {
        return max;
    }
    public void reset(){
        max=resetVal;
    }
}
