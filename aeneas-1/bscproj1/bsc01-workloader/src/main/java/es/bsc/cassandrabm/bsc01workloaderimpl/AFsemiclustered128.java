/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

/**
 *
 * @author ccugnasc
 */
public class AFsemiclustered128 extends AFsemiclustered  implements QueryInterfaceImpl {

    public AFsemiclustered128() {
        super("AFsemiclustered128",128);
    }

    public AFsemiclustered128(  int framestoget, int atomsnumber, boolean saveresults, int framesnumber, int rowswindow) {
        super("AFsemiclustered128",128, framestoget, atomsnumber, saveresults, framesnumber, rowswindow);
    }
    
}
