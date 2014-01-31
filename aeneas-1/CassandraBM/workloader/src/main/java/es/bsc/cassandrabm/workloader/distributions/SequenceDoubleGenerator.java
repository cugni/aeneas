/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import com.google.common.util.concurrent.AtomicDouble;



/**
 *
 * @author cesare
 */
public class SequenceDoubleGenerator extends NumberDistribution<Double>{
    AtomicDouble current;
    final double delta;
    public SequenceDoubleGenerator(Double from,Double to,Double delta){
        super(from,to);
        this.delta=delta;
        current=new AtomicDouble(this.from);
    }
    public synchronized void reset() {
      current=new AtomicDouble(from);
    }

    @Override
    public Double next() {
        if(current.get()==to){
        reset();
        return to;
        }
        return setLast(current.getAndAdd(delta));
    }

    @Override
    public Double nextInRange(Double from, Double maxwide) {
       return from+maxwide;
    }

   
}
