package es.bsc.cassandrabm.workloader.distributions;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author cesare
 *This class generates sequence of positive numbers
 */
public class SequenceIntegerGenerator extends NumberGenerator<Integer> {

    AtomicInteger current;
    private final Integer from;
    private final Integer to;

    public SequenceIntegerGenerator(Integer from, Integer to) {
        this.from=Math.abs(from);
        this.to=Math.abs(to);              
        current = new AtomicInteger(this.from);
    }

    public synchronized void reset() {
        current = new AtomicInteger(from);

    }

    @Override
    public Integer next() {
        if (current.get() == to) {
            reset();
            return to;
        }
        return setLast(current.getAndIncrement());
    }

    @Override
    public Integer nextInRange(Integer from, Integer maxwide) {
        return from+maxwide;
    }

     
    
}
