/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.distributions;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author cesare
 */
public class SequenceLongGenerator extends NumberDistribution<Long>{
    AtomicLong current;
    public SequenceLongGenerator(Long from,Long to){
        super(Math.abs(from),Math.abs(to));
        current=new AtomicLong(this.from);
    }
    public synchronized void reset() {
      current=new AtomicLong(from);
    }

    public Long next() {
        if(current.get()==to){
        reset();
        return to;
        }
        return setLast(current.getAndIncrement());
    }

    @Override
    public Long nextInRange(Long from, Long maxwide) {
      return from+maxwide;
    }

   
}
