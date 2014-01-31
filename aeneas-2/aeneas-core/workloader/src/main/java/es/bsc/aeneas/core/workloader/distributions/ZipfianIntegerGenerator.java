/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.distributions;

/**
 *
 * @author cesare
 */
public class ZipfianIntegerGenerator extends NumberGenerator<Integer> {

    private final ZipfianGenerator zipfianGenerator;

    public ZipfianIntegerGenerator(Integer from, Integer to) {
        zipfianGenerator = new ZipfianGenerator(from, to);


    }


    public Integer next() {
        return setLast(zipfianGenerator.nextInt());
    }
    private ZipfianIntegerGenerator wide=null;
    @Override
    public Integer nextInRange(Integer from, Integer maxwide) {
        if(wide==null){
            wide=new ZipfianIntegerGenerator(0,maxwide);
        }
       return from+wide.next();
    }
}
