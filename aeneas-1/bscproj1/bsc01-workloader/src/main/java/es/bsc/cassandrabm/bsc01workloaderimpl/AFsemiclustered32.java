/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

/**
 *
 * @author ccugnasc
 */
public class AFsemiclustered32 extends AFsemiclustered  implements QueryInterfaceImpl {

    public AFsemiclustered32() {
        super("AFsemiclustered32",32);
    }
    public AFsemiclustered32(  int framestoget, int atomsnumber, boolean saveresults, int framesnumber, int rowswindow) {
        super("AFsemiclustered32",32, framestoget, atomsnumber, saveresults, framesnumber, rowswindow);
    }
}
