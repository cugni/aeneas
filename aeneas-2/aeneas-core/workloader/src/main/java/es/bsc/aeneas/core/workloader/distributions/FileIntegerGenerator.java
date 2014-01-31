/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.distributions;

/**
 *
 * @author cesare
 */
public class FileIntegerGenerator extends NumberGenerator<Integer>{
    private final FileGenerator fileGenerator;
    FileIntegerGenerator(String filename){
        this.fileGenerator=new FileGenerator(filename);
    }
    public Integer next() {
        return setLast(Integer.parseInt(fileGenerator.next()));
    }

    @Override
    public Integer nextInRange(Integer from, Integer maxwide) {
       return from+maxwide;
    }
 
    
}
