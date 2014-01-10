/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

/**
 *
 * @author cesare
 */
public class ZipfianDoubleGenerator extends NumberGenerator<Double> {

    private final ZipfianGenerator zipfianGenerator;
    double from, step;
    long steps;

    public ZipfianDoubleGenerator(Double from, Double to, Double step) {
        if (step > from || to < from) {
            throw new IllegalArgumentException("The provided range and step are inconsistent");
        }
        this.from = from;
        this.step = step;
        steps = (long) ((to - from) / step);
        zipfianGenerator = new ZipfianGenerator(0L, steps);


    }

    @Override
    public Double next() {
        long nstep = zipfianGenerator.nextLong();

        return setLast(nstep * step + from);
    }
    ZipfianDoubleGenerator wide=null;
    @Override
    public Double nextInRange(Double from, Double maxwide) {
        if(wide==null){
            wide=new ZipfianDoubleGenerator(0.0,maxwide,step);
        }
        return from+wide.next();
    }
}
