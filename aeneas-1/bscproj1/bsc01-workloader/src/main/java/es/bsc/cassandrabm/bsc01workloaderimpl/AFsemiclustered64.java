/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

/**
 *
 * @author ccugnasc
 */
public class AFsemiclustered64 extends AFsemiclustered  implements QueryInterfaceImpl{

    public AFsemiclustered64() {
        super("AFsemiclustered64",64);
    }
      public AFsemiclustered64(  int framestoget, 
              int atomsnumber, boolean saveresults, 
              int framesnumber, int rowswindow) {
        super("AFsemiclustered64",64,
                framestoget, atomsnumber, saveresults, framesnumber, rowswindow);
    }
    
}
