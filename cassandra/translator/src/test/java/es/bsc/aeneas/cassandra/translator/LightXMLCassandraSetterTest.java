/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.translator;

import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.Mapping;
import es.bsc.aeneas.core.rosetta.Result;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import javax.inject.Inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LightXMLCassandraSetterTest {

    private static final Logger LOG = Logger.getLogger(LightXMLCassandraSetterTest.class.getName());

    @Inject
    private LightXMLCassandraSetter se;
    @Inject
    private ClusterType clusterType;
    

    @Test
    public void testInit() throws Exception {
         se.init(clusterType);
    }

    @Test
    public void testClose() {
        System.out.println("close");
        LightXMLCassandraSetter instance = new LightXMLCassandraSetter();
        instance.close();
        fail("The test case is a prototype.");
    }

    @Test
    public void testQuery() {
        System.out.println("query");
        CrudType ct = null;
        String matchid = "";
        ImmutableList<Mapping> match = null;
        LightXMLCassandraSetter instance = new LightXMLCassandraSetter();
        Callable<Result> expResult = null;
        Callable<Result> result = instance.query(ct, matchid, match);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

}
