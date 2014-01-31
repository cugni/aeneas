package es.bsc.bsc01loaderimpl.sourceloader;

import es.bsc.cassandrabm.loader.AbstractCassandraDB;
import es.bsc.cassandrabm.loader.DBSetter;
import es.bsc.cassandrabm.loader.IOTestDB;
import es.bsc.cassandrabm.loader.Loader;
import es.bsc.cassandrabm.loader.SourceReader;
import es.bsc.cassandrabm.loader.XMLCassandraSetter;
import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;
import es.bsc.cassandrabm.model.marshalling.BoxType;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import static org.junit.Assert.*;
import org.junit.Test;

public class SourceAndSetterTestEXT {

    private final static Logger log = Logger.getLogger(SourceAndSetterTestEXT.class.getName());
    private static String clusterName = Loader.getInstance().clustername;
    private static String clusterAdd = Loader.getInstance().clusterlocation;

    @Test
    public void EmbeddedSourceTest() throws FileNotFoundException {
        log.info("EmbeddedSourceTest");
        DBSetter db = new TestDBSetter();
        SourceReader source = new EmbeddedSource();
        source.setDBSetter(db);
        File f = new File("test.txt");
        if (!f.exists()) {
            fail("test source file not found");
        }
        InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
        source.open(isr);

        try {
            source.call();
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            log.log(Level.WARNING, "Exception ", ex);
            fail("Error occurs" + ex.getMessage());
        }
        CorrectnessTestFrame0(((TestDBSetter) db).getMap());
        CorrectnessTestFrame1(((TestDBSetter) db).getMap());
    }
    //public String fileName="CesareTrajectory.txt";
    public String fileName = "test.txt";

    //   @Test
    /*
     * Test removed because it fails always for a Cassandra bug 
     * https://issues.apache.org/jira/browse/CASSANDRA-4468
     */
    public void XMLSetterCassandraTest1NoBatched() {
        log.info("EmbeddedToCassandra2Test No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("test1");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraTestNoBatched() {
        log.info("EmbeddedToCassandra2Test No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("test");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraTestBatched() {
        log.info("EmbeddedToCassandra2Test batched");
        System.setProperty("batched", "true");
        AbstractCassandraDB db = new XMLCassandraSetter("test");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraKeyCompositeNoBatched() {
        log.info("EmbeddedToCassandraComposite No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("keyComposite");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraKeyCompositeBatched() {
        log.info("EmbeddedToCassandraComposite batched");
        System.setProperty("batched", "true");
        AbstractCassandraDB db = new XMLCassandraSetter("keyComposite");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraDOP2LevelPerfectNoBatched() {
        log.info("EmbeddedToCassandraComposite batched");
        System.setProperty("batched", "false");

        AbstractCassandraDB db = new XMLCassandraSetter("DOP2LevelPerfect");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    public void XMLSetterCassandraRP2NormLevelPerfectNoBatched() {
        log.info("EmbeddedToCassandraComposite batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("RP2NormLevelPerfect");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraColumnCompositeFxPNoBatched() {
        log.info("EmbeddedToCassandraColumnCompositeFxP No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("columnCompositeFxP");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraColumnCompositeFxPBatched() {
        log.info("EmbeddedToCassandraColumnCompositeFxP batched");
        System.setProperty("batched", "true");
        AbstractCassandraDB db = new XMLCassandraSetter("columnCompositeFxP");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraColumnCompositePxFNoBatched() {
        log.info("EmbeddedToCassandraColumnCompositePxF No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("columnCompositePxF");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraColumnCompositePxFBatched() {
        log.info("EmbeddedToCassandraColumnCompositePxF batched");
        System.setProperty("batched", "true");
        AbstractCassandraDB db = new XMLCassandraSetter("columnCompositePxF");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraPointxBOPNoBatched() {
        log.info("EmbeddedToCassandraPointxBOP No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("pointsBOP");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterCassandraPointxBOPBatched() {
        log.info("EmbeddedToCassandraPointxBOP batched");
        System.setProperty("batched", "true");
        AbstractCassandraDB db = new XMLCassandraSetter("pointsBOP");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }

    @Test
    public void XMLSetterClusteredKyes() {
        log.info("EmbeddedToCassandra2Test No batched");
        System.setProperty("batched", "false");
        AbstractCassandraDB db = new XMLCassandraSetter("clusteringTest");

        abstractCassandraTest(
                fileName /*
                 * "CesareTrajectory.txt"
                 */, db, new EmbeddedSource());
    }
    //commentend for performance 
//     @Test
//    public void XMLSetterCassandraTestNoBatchedBF() {
//        log.info("EmbeddedToCassandra2Test No batched BF");
//        System.setProperty("batched", "false");
//        AbstractCassandraDB db = new XMLCassandraSetter("test");
//
//        abstractCassandraTest(
//               "bigfile.txt"
//                , db, new EmbeddedSource());
//    }
//    @Test
//    public void XMLSetterCassandraTestBatchedBF() {
//        log.info("EmbeddedToCassandra2Test batched BF");
//        System.setProperty("batched", "true");
//        AbstractCassandraDB db = new XMLCassandraSetter("test");
//
//        abstractCassandraTest(
//                   "bigfile.txt" /*
//                 * "CesareTrajectory.txt"
//                 */, db, new EmbeddedSource());
//    }
//     @Test
//    public void XMLSetterCassandraCompositeNoBatchedBF() {
//        log.info("EmbeddedToCassandraComposite No batched BF");
//        System.setProperty("batched", "false");
//        AbstractCassandraDB db = new XMLCassandraSetter("composite");
//
//        abstractCassandraTest(
//                   "bigfile.txt" /*
//                 * "CesareTrajectory.txt"
//                 */, db, new EmbeddedSource());
//    }
//    @Test
//    public void XMLSetterCassandraCompositeBatchedBF() {
//        log.info("EmbeddedToCassandraComposite batched BF");
//        System.setProperty("batched", "true");
//        AbstractCassandraDB db = new XMLCassandraSetter("composite");
//
//        abstractCassandraTest(
//                   "bigfile.txt" /*
//                 * "CesareTrajectory.txt"
//                 */, db, new EmbeddedSource());
//    }

    public static void abstractCassandraTest(String fileName, AbstractCassandraDB db, final SourceReader source) {
        try {
            log.log(Level.INFO, "Testing dbsource {0} reading from the file {1}\n", new Object[]{db.getClass().getSimpleName(), fileName});
            try {
                db.open(clusterName, clusterAdd);
            } catch (UnreachableClusterException ex) {
                Logger.getLogger(SourceAndSetterTestEXT.class.getName()).log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            }
            db.configure( );
            source.setDBSetter(db);

            File file = new File(fileName);
            if (!file.exists()) {
                fail("test source file not found");
            }
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(new FileInputStream(file));
                source.open(isr);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SourceAndSetterTestEXT.class.getName()).log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            }


            try {
                source.call();
            } catch (ExecutionException ex) {
                log.log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
                throw new IllegalArgumentException(ex);
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
                throw new IllegalArgumentException(ex);
            }

            log.log(Level.INFO, "db filling completed , read {0} lines", source.lineRead());
            Thread.currentThread().sleep(1000);
            log.log(Level.INFO, "db filling testing");
            try {
                isr = new InputStreamReader(new FileInputStream(file));
                source.open(isr);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SourceAndSetterTestEXT.class.getName()).log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            }
            IOTestDB testDB = new IOTestDB(db);
            source.setDBSetter(testDB);
            source.open(isr);
            log.info("Testing data");


            try {
                source.call();
            } catch (ExecutionException ex) {
                log.log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            }
            testDB.close();
            log.info("Testing completed");
            assertEquals("Failed rows", 0, testDB.failedRows.get());
            assertEquals("untested rows", 0, testDB.untestedRows.get());

            db.close();
        } catch (InterruptedException ex) {
            Logger.getLogger(SourceAndSetterTestEXT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void CorrectnessTestFrame0(Map<Object, Object> mat) {

        //test frame 0
        Object o = mat.get(0);
        assertNotNull(o);
        assertTrue((o instanceof Map));
        Map m = (Map) o;
        testMeta(m, new Object[]{20, (int) 0, Double.doubleToLongBits(2.0e+00), 1000});


        Object[][] boxVals = new Object[][]{
            {1.09374e+01, 0.00000e+00, 0.00000e+00},
            {3.64581e+00, 1.03119e+01, 0.00000e+00},
            {-3.64581e+00, 5.15596e+00, 9.04137e+00}
        };
        Object[][] pos = new Object[][]{
            {5.26100e+00, 7.70100e+00, 8.28700e+00}, {5.20300e+00, 7.63200e+00, 8.24100e+00},
            {5.30500e+00, 7.65300e+00, 8.36400e+00}, {5.20900e+00, 7.77500e+00, 8.33200e+00},
            {5.35600e+00, 7.77300e+00, 8.20200e+00}, {5.41200e+00, 7.69900e+00, 8.14500e+00},
            {5.28800e+00, 7.87300e+00, 8.10900e+00}, {5.25600e+00, 7.95600e+00, 8.17200e+00},
            {5.20400e+00, 7.82400e+00, 8.06000e+00}, {5.38400e+00, 7.92300e+00, 8.00100e+00},
            {5.48900e+00, 7.86100e+00, 7.98000e+00}, {5.33900e+00, 8.02200e+00, 7.93800e+00},
            {5.45600e+00, 7.84300e+00, 8.29400e+00}, {5.42700e+00, 7.88700e+00, 8.40500e+00},
            {5.58000e+00, 7.84000e+00, 8.24600e+00}, {5.58800e+00, 7.81600e+00, 8.14700e+00},
            {5.69700e+00, 7.89300e+00, 8.31300e+00}, {5.65600e+00, 7.95300e+00, 8.39400e+00},
            {5.77500e+00, 7.77500e+00, 8.36900e+00}, {5.81300e+00, 7.71100e+00, 8.28900e+00}
        };
        testBox(m, boxVals);
        testPoints(m, pos);
    }

    private static void CorrectnessTestFrame1(Map<Object, Object> mat) {

        //test frame 0
        Object o = mat.get(1);
        assertNotNull(o);
        assertTrue((o instanceof Map));
        Map m = (Map) o;
        testMeta(m, new Object[]{20, (int) 0, Double.doubleToLongBits(2.0e+00), 1000});
        Object[][] boxVals = new Object[][]{
            {1.09374e+01, 0.00000e+00, 0.00000e+00},
            {3.64581e+00, 1.03119e+01, 0.00000e+00},
            {-3.64581e+00, 5.15596e+00, 9.04137e+00}
        };
        Object[][] pos = new Object[][]{
            {5.26100e+00, 7.70100e+00, 8.28700e+00},
            {5.20300e+00, 7.63200e+00, 8.24100e+00},
            {5.30500e+00, 7.65300e+00, 8.36400e+00},
            {5.20900e+00, 7.77500e+00, 8.33200e+00},
            {5.35600e+00, 7.77300e+00, 8.20200e+00},
            {5.41200e+00, 7.69900e+00, 8.14500e+00},
            {5.28800e+00, 7.87300e+00, 8.10900e+00},
            {5.25600e+00, 7.95600e+00, 8.17200e+00},
            {5.20400e+00, 7.82400e+00, 8.06000e+00},
            {5.38400e+00, 7.92300e+00, 8.00100e+00},
            {5.48900e+00, 7.86100e+00, 7.98000e+00},
            {5.33900e+00, 8.02200e+00, 7.93800e+00},
            {5.45600e+00, 7.84300e+00, 8.29400e+00},
            {5.42700e+00, 7.88700e+00, 8.40500e+00},
            {5.58000e+00, 7.84000e+00, 8.24600e+00},
            {5.58800e+00, 7.81600e+00, 8.14700e+00},
            {5.69700e+00, 7.89300e+00, 8.31300e+00},
            {5.65600e+00, 7.95300e+00, 8.39400e+00},
            {5.77500e+00, 7.77500e+00, 8.36900e+00},
            {5.81300e+00, 7.71100e+00, 8.28900e+00}
        };
        testBox(m, boxVals);
        testPoints(m, pos);
    }

    private static void testMeta(Map m, Object[] ob) {
        assertEquals(m.get("natoms"), ob[0]);
        assertEquals(m.get("step"), ob[1]);
        assertEquals(m.get("time"), ob[2]);
        assertEquals(m.get("prec"), ob[3]);
        assertTrue((m.get("box") instanceof BoxType));

    }

    private static void testPoints(Map m, Object[][] pos) {
        assertTrue("x list is a list", (m.get("points") instanceof Map));
        Map<Integer, PointType> p = (Map<Integer, PointType>) m.get("points");
        for (int j = 0; j < p.size(); j++) {
            assertEquals("Differnt point x", p.get(j).getX(), pos[j][0]);
            assertEquals("Differnt point y", p.get(j).getY(), pos[j][1]);
            assertEquals("Differnt point z", p.get(j).getZ(), pos[j][2]);
        }
    }

    private static void testBox(Map m, Object[][] vals) {
        assertTrue("x list is a list", (m.get("box") instanceof BoxType));
        BoxType box = (BoxType) m.get("box");
        List<PointType> p = box.getPoints();
        for (int j = 0; j < p.size(); j++) {
            assertEquals("Differnt point x", p.get(j).getX(), vals[j][0]);
            assertEquals("Differnt point y", p.get(j).getY(), vals[j][1]);
            assertEquals("Differnt point z", p.get(j).getZ(), vals[j][2]);
        }
    }

    private class TestDBSetter implements DBSetter {

        private HashMap<Object, Object> mat = new HashMap();

        @Override
        public void close() {
            // TODO Auto-generated method stub
        }

        public Map<Object, Object> getMap() {
            return mat;
        }

        @Override
        public void configure( ) {
            //nothing to config for test purpouse
        }

        @Override
        public void put(Object value, Object... path)
                throws InvalidPutRequest {
            HashMap<Object, Object> map = mat;
            StringBuilder sb = new StringBuilder(100);
            Object lastKey = path[path.length - 1];
            for (int i = 0; i < path.length - 1; i++) {
                Object o = path[i];
                sb.append("[").append(o.toString()).append("{").append(o.getClass().getSimpleName()).append("}" + "]");
                if (map.containsKey(o)) {
                    if (map.get(o) instanceof HashMap) {
                        map = (HashMap<Object, Object>) map.get(o);
                    } else {
                        fail("Invalid state in the map");
                    }
                } else {

                    map.put(o, new HashMap(0));
                    map = (HashMap<Object, Object>) map.get(o);
                }
            }
            map.put(lastKey, value);
            sb.append("[").append(lastKey.toString()).append("{").append(lastKey.getClass().getSimpleName()).append("}" + "]=").append(value).append("{").append(value.getClass().getSimpleName()).append("}");

            log.log(Level.FINE, "result:{0}", sb.toString());
        }

        @Override
        public void open(String ClusterName, String location) {
            // TODO Auto-generated method stub
        }
    };
}