/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

import es.bsc.aeneas.workloader.controller.Range;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author cesare //TODO better this implementaion
 */
public class SequenceRangeNumberGenerator<T extends Number & Comparable>
        implements RangeGenerator<T> {

    private final T from;
    private final T to;
    private final T delta;
   private final RangeGenerator gen;
   // private final Random r = CUtils.random();

    public SequenceRangeNumberGenerator(T from, T to, T delta) {
        checkArgument(from.getClass().equals(to.getClass()));
        this.from = from;
        this.to = to;
        this.delta = delta;
        if (from instanceof Integer) {
            gen = new SequenceInteger();
        } else if (from instanceof Long) {
            gen = new SequenceLong();
        } else if (from instanceof Double) {
            gen = new SequenceDouble();
        } else {
            throw new IllegalArgumentException("Not supported type: " + from.getClass().getName());
        }

    }

    @Override
    public Range<T> getNextRange() {
        return gen.getNextRange();
    }

    private class SequenceInteger implements RangeGenerator<Integer> {

        private final Integer ifrom;
        private final Integer ito;
        private final SequenceIntegerGenerator generator;

        SequenceInteger() {
            this.ifrom = (Integer) from;
            this.ito = (Integer) to;
            generator = new SequenceIntegerGenerator(ifrom, ito);
        }

        @Override
        public Range<Integer> getNextRange() {
            int n = generator.next();
            return new Range(n, ifrom - ito + n);
        }
    }

    private class SequenceLong implements RangeGenerator<Long> {

        private final Long lfrom;
        private final Long lto;
        private final SequenceLongGenerator generator;

        SequenceLong() {
            this.lfrom = (Long) from;
            this.lto = (Long) to;
            generator = new SequenceLongGenerator(lfrom, lto);

        }

        @Override
        public Range<Long> getNextRange() {
            long n = generator.next();
            return new Range(n, lfrom - lto + n);
        }
    }

    private class SequenceDouble implements RangeGenerator<Double> {

        private final Double lfrom;
        private final Double lto;
        private final SequenceDoubleGenerator generator;

        SequenceDouble() {
            this.lfrom = (Double) from;
            this.lto = (Double) to;
            generator = new SequenceDoubleGenerator(lfrom, lto,(Double)delta);

        }

        @Override
        public Range<Double> getNextRange() {
            double n = generator.next();
            return new Range(n, lfrom - lto + n);
        }
    }
}
