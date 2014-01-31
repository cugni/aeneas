/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.controller;

import es.bsc.aeneas.codegenerator.query.QueryInterface;
import es.bsc.aeneas.codegenerator.query.annotations.InputVars;
import es.bsc.aeneas.codegenerator.query.annotations.ReturnVars;
import es.bsc.aeneas.codegenerator.query.annotations.TestInterface;
import es.bsc.aeneas.codegenerator.query.annotations.Var;
import es.bsc.aeneas.core.workloader.controller.Client.Distribution;
import es.bsc.aeneas.core.workloader.controller.TestImpl.FailedTestException;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ccugnasc
 */
public class TestImplTest {

    public TestImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class TestImpl.
     */
    @Test
    public void testRunNormally() throws Exception {

        System.out.println("testRunNormally");
        T1 qi = new T1();
        TestImpl impl = new TestImpl(qi, Distribution.UNIFORM, 20, 1, false, false);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();
        impl.run();
        assertEquals("Conc 1", (Integer) 20, (Integer) qi.ncall.get());
        assertEquals("internal completed", 20, impl.completedQueries.count());
        assertEquals("internal failed", 0, impl.failedQueries.count());
        assertEquals("internal timeout", 0, impl.timeoutQueries.count());
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();
        qi = new T1();
        impl = new TestImpl(qi, Distribution.UNIFORM, 20, 10, false, false);
        impl.run();
        assertEquals("Conc 10", (Integer) 20, (Integer) qi.ncall.get());
        assertEquals("internal completed", 20, impl.completedQueries.count());
        assertEquals("internal failed", 0, impl.failedQueries.count());
        assertEquals("internal timeout", 0, impl.timeoutQueries.count());
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();
        qi = new T1();
        impl = new TestImpl(qi, Distribution.UNIFORM, 200000, 1000, false, false);
        impl.run();
        assertEquals("Conc 1000", (Integer) 200000, (Integer) qi.ncall.get());
        assertEquals("internal completed", 200000, impl.completedQueries.count());
        assertEquals("internal failed", 0, impl.failedQueries.count());
        assertEquals("internal timeout", 0, impl.timeoutQueries.count());
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();

    }

    /*
     * Testing about catching the excepions
     */
    @Test
    public void testRunException() throws Exception {

        System.out.println("testRunException");
        System.out.println("exception single thread");
        Texp qi = new Texp(5);
        TestImpl impl = new TestImpl(qi, Distribution.UNIFORM, 20, 1, false, false);
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        //this test is too much restrictive for concurrent threads
        assertTrue("It doesn't fail", 5 <= qi.ncall.get());
        assertTrue("internal completed", 4 <= impl.completedQueries.count());
        assertEquals("internal failed", 1, impl.failedQueries.count());
        assertEquals("internal timeout", 0, impl.timeoutQueries.count());
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.completedQueries.clear();

        System.out.println("exception multi thread");

        //In concurrency
        qi = new Texp(5);
        impl = new TestImpl(qi, Distribution.UNIFORM, 20, 10, false, false);
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        assertTrue("It doesn't fail", 5 <= qi.ncall.get());
        assertTrue("internal completed", 4 <= impl.completedQueries.count());
        assertEquals("internal failed", 1, impl.failedQueries.count(), 10);
        assertEquals("internal timeout", 0, impl.timeoutQueries.count(), 10);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.completedQueries.clear();


        System.out.println("exception multi thread big number");

        //In concurrency
        qi = new Texp(1000);
        impl = new TestImpl(qi, Distribution.UNIFORM, 20000, 10, false, false);
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        assertTrue("It doesn't fail", 1000 <= qi.ncall.get());
        assertTrue("internal completed", 999 <= impl.completedQueries.count());
        assertEquals("internal failed", 1, impl.failedQueries.count(), 10);
        assertEquals("internal timeout", 0, impl.timeoutQueries.count(), 10);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.completedQueries.clear();

        System.out.println("Continue on exception");
        qi = new Texp(10);
        impl = new TestImpl(qi, Distribution.UNIFORM, 100000, 100, true, false);
        impl.run();
        assertTrue("Conc 1000", 100000 <= qi.ncall.get());
        assertTrue("internal completed", 90000 <= impl.completedQueries.count());
        assertEquals("internal failed", 10000, impl.failedQueries.count());
        assertEquals("internal timeout", 0, impl.timeoutQueries.count());

    }

    @Test
    public void testRunTimeout() throws Exception {

        System.out.println("testRunTimeout");
        System.out.println("timeout single thread");
        Ttout qi = new Ttout(5);

        TestImpl impl = new TestImpl(qi, Distribution.UNIFORM, 20, 1, false, false);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        //this test is too much restrictive for concurrent threads
        assertTrue("It doesn't fail", 5 <= qi.ncall.get());
        assertTrue("internal completed", 4 <= impl.completedQueries.count());
        assertEquals("internal failed", 1, impl.failedQueries.count());
        assertEquals("internal timeout", 1, impl.timeoutQueries.count());
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();

        System.out.println("exception multi thread");

        //In concurrency
        qi = new Ttout(5);
        impl = new TestImpl(qi, Distribution.UNIFORM, 20, 10, false, false);
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        assertTrue("It doesn't fail", 5 <= qi.ncall.get());
        assertTrue("internal completed", 4 <= impl.completedQueries.count());
        assertEquals("internal failed", 4, impl.failedQueries.count(), 10);
        assertEquals("internal timeout", 1, impl.timeoutQueries.count(), 10);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.timeoutQueries.clear();

        System.out.println("exception multi thread big number");

        //In concurrency
        qi = new Ttout(1000);
        impl = new TestImpl(qi, Distribution.UNIFORM, 20000, 10, false, false);
        try {
            impl.run();
            fail("Exception not raised");
        } catch (FailedTestException e) {
        }
        assertTrue("It doesn't fail", 1000 <= qi.ncall.get());
        assertTrue("internal completed", 999 <= impl.completedQueries.count());
        assertEquals("internal failed", 1, impl.failedQueries.count(), 10);
        assertEquals("internal timeout", 0, impl.timeoutQueries.count(), 10);
        impl.completedQueries.clear();
        impl.failedQueries.clear();
        impl.completedQueries.clear();
        impl.timeoutQueries.clear();
        System.out.println("Continue on exception");
        qi = new Ttout(10);
        impl = new TestImpl(qi, Distribution.UNIFORM, 100000, 100, false, true);
        impl.run();
        assertTrue("Conc 1000", 100000 <= qi.ncall.get());
        assertTrue("internal completed", 90000 <= impl.completedQueries.count());
        assertEquals("internal failed", 10000, impl.failedQueries.count());
        assertEquals("internal timeout", 10000, impl.timeoutQueries.count());

    }

    private class T1 implements TestClass {

        AtomicInteger ncall = new AtomicInteger(0);

        @Override
        public Map call() {
            Map m = new HashMap();
            m.put("ncall", ncall.incrementAndGet());
            return m;
        }
    }

    private class Ttout implements TestClass {

        private final int exp_rate;

        Ttout(int exp_rate) {
            this.exp_rate = exp_rate;
        }
        AtomicInteger ncall = new AtomicInteger(0);

        @Override
        public Map call() {
            Map m = new HashMap();
            int n = ncall.incrementAndGet();
            if (n % exp_rate == 0) {
                throw new me.prettyprint.hector.api.exceptions.HTimedOutException("scheduled timeout");
            }
            m.put("ncall", n);
            return m;
        }
    }

    private class Texp implements TestClass {

        private final int exp_rate;

        Texp(int exp_rate) {
            this.exp_rate = exp_rate;
        }
        AtomicInteger ncall = new AtomicInteger(0);

        @Override
        public Map call() {
            Map m = new HashMap();
            int n = ncall.incrementAndGet();
            if (n % exp_rate == 0) {
                throw new RuntimeException("scheduled exception");
            }
            m.put("ncall", n);
            return m;
        }
    }

    @TestInterface
    public interface TestClass extends QueryInterface {

        @ReturnVars({
            @Var(name = "ncall",
                    returnType = Integer.class)
        })
        @InputVars({})
        public Map call();
    }

    /**
     * Test of run method, of class TestImpl.
     */
    @Test
    public void testFilterMethodsRun() throws Exception {
        //no filter
        FilterTest qi = mock(FilterTest.class);
        TestImpl instance = new TestImpl(qi, Distribution.UNIFORM, 1, 1, true, true);
        instance.run();
        verify(qi, times(1)).getFrames();
        verify(qi, times(1)).getAtoms();

        //filtered one method
        qi = mock(FilterTest.class);
        System.setProperty("methodfilter", "getFrames");
        instance = new TestImpl(qi, Distribution.UNIFORM, 1, 1, true, true);
        instance.run();
        verify(qi, only()).getFrames();

        //filtered one method
        qi = mock(FilterTest.class);
        System.setProperty("methodfilter", "getAto.*");
        instance = new TestImpl(qi, Distribution.UNIFORM, 1, 1, true, true);
        instance.run();
        verify(qi, only()).getAtoms();

        //filtered one method
        qi = mock(FilterTest.class);
        System.setProperty("methodfilter", "get(Atoms|Frames)");
        instance = new TestImpl(qi, Distribution.UNIFORM, 1, 1, true, true);
        instance.run();
        verify(qi, times(1)).getFrames();
        verify(qi, times(1)).getAtoms();



    }

    @TestInterface
    private static interface FilterTest extends QueryInterface {

        @InputVars({})
        public Map getFrames();

        @InputVars({})
        public Map getAtoms();
    }
}
