/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

/**
 *
 * @author cesare
 */
public class ZipfianLongGenerator extends NumberGenerator<Long> {
     private final ZipfianGenerator zipfianGenerator;

    public ZipfianLongGenerator(Long from, Long to) {
        zipfianGenerator = new ZipfianGenerator(from, to);


    }
  

    public Long next() {
        return setLast(zipfianGenerator.nextLong());
    }
    private ZipfianLongGenerator wide=null;
    @Override
    public Long nextInRange(Long from, Long maxwide) {
        if(wide==null){
            wide= new ZipfianLongGenerator(0L,maxwide);
        }
       return from+wide.next();
    }
    
}
