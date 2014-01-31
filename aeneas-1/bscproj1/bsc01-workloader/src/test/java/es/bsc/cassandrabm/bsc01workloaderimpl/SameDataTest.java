/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

import com.google.common.collect.Table;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.model.marshalling.PointType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ccugnasc
 */
public class SameDataTest {

    final int ATOMS_NUMBER = CUtils.getInt("atomsnumber", 99637);
    final int FRAMES_NUMBER = CUtils.getInt("framesnumber", 1001);

    //10x10
    @Test
    public void sameDataFrame10pc() {
        sameDataFrame(10);
    }

    private void sameDataFrame(int ncases) {
        AFsemiclustered32Test clus32 = new AFsemiclustered32Test();
        AFsemiclustered64Test clus64 = new AFsemiclustered64Test();
        AFsemiclustered128Test clus128 = new AFsemiclustered128Test();
        AidXFidTest af = new AidXFidTest();
        FidXAidTest fa = new FidXAidTest();
        int frameRandom = CUtils.random().nextInt(FRAMES_NUMBER - ncases);
        int atomsRandom = CUtils.random().nextInt(ATOMS_NUMBER - ncases);
        Table<Integer, Integer, PointType> cpoints32 = clus32.testGetPoints(frameRandom, frameRandom + ncases, atomsRandom, atomsRandom + ncases);
        Table<Integer, Integer, PointType> cpoints64 = clus64.testGetPoints(frameRandom, frameRandom + ncases, atomsRandom, atomsRandom + ncases);
        Table<Integer, Integer, PointType> cpoints128 = clus128.testGetPoints(frameRandom, frameRandom + ncases, atomsRandom, atomsRandom + ncases);

        Table<Integer, Integer, PointType> afpoints = af.testGetPoints(frameRandom, frameRandom + ncases, atomsRandom, atomsRandom + ncases);
        Table<Integer, Integer, PointType> fapoints = fa.testGetPoints(frameRandom, frameRandom + ncases, atomsRandom, atomsRandom + ncases);
        assertEquals("clus af,same column set", cpoints32.columnKeySet(), afpoints.columnKeySet());
        assertEquals("fa af,same column set", fapoints.columnKeySet(), afpoints.columnKeySet());
        assertEquals("clus af,same columnkey  set", cpoints32.rowKeySet(), afpoints.rowKeySet());
        assertEquals("fa af,same row key set", fapoints.rowKeySet(), afpoints.rowKeySet());

        for (int f : cpoints32.rowKeySet()) {
            for (int a : cpoints32.columnKeySet()) {
                assertNotNull("point not foun in clus32", cpoints32.get(f, a));
                assertNotNull("point not foun in clus64", cpoints64.get(f, a));
                assertNotNull("point not foun in clus128", cpoints128.get(f, a));

                assertNotNull("point not foun in af", afpoints.get(f, a));
                assertNotNull("point not foun in fa", fapoints.get(f, a));
                assertEquals("clus differs from af", cpoints32.get(f, a), afpoints.get(f, a));
                assertEquals("fa differs from af", fapoints.get(f, a), afpoints.get(f, a));

            }
        }
    }

  
}
