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
public class NumberRangeGenerator<T extends Number & Comparable> implements RangeGenerator<T> {

    private final NumberGenerator<T> single;
    protected final T maxwide;
    public NumberRangeGenerator(NumberGenerator<T> single,T maxwide) {
        this.single = single;
        this.maxwide=maxwide;

    }

    @Override
    public Range<T> getNextRange() {
        T t1 = (T) single.next();
        if(maxwide==null){
        T t2 = (T) single.next();
        if (t1.compareTo(t2) < 0) {
            return new Range(t1, t2);
        } else if (t1.compareTo(t2) > 0) {
            return new Range(t2, t1);
        } else {
            t2 = (T) single.next();
            if (t1.compareTo(t2) < 0) {
                return new Range(t1, t2);
            } else if (t1.compareTo(t2) > 0) {
                return new Range(t2, t1);
            } else {
               if(t1 instanceof Integer){
                   return new Range(t1,(Integer)t1+1);
               }
               if(t1 instanceof Long){
                   return new Range(t1,(Long)t1+1);
               }
                throw new IllegalArgumentException("Impossible generates a number interval with "+single.getClass().getName());
            }
        }
        }else{
            T t2 = single.nextInRange(t1, maxwide);
              return new Range(t1, t2);
            
        }

    }
}
