/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.core.rosetta.exceptions.InvalidPutRequest;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.ClusterHandler;
import es.bsc.aeneas.core.rosetta.Mapping;
import es.bsc.aeneas.core.rosetta.Result;
import java.util.concurrent.Callable;


/**
 *
 * @author cesare
 */
public class IOTestDB implements ClusterHandler {

    private final static Logger log = Logger.getLogger(IOTestDB.class.getName());
    private ClusterHandler totest;
    public AtomicInteger totalRows = new AtomicInteger(0);
    public AtomicInteger testedRows = new AtomicInteger(0);
    public AtomicInteger untestedRows = new AtomicInteger(0);
    public AtomicInteger failedRows = new AtomicInteger(0);

    public IOTestDB(ClusterHandler totest) {
        this.totest = totest;
    }

 

    @Override
    public void close() {
        totest.close();
        log.log(Level.INFO, "Tested {0} rows on {1} with {2} unsupported query .{3} failed",
                new Object[]{testedRows.get(), totalRows.get(), untestedRows.get(), failedRows.get()});

    }

   
   
 

    @Override
    public void init(ClusterType clusterType) throws UnreachableClusterException {
      totest.init(clusterType);
    }

    @Override
    public Callable<Result> query(CrudType ct, String matchid, ImmutableList<Mapping> match) {
        /**
         * TODO : here the code have to read a value and check if is equal 
         * to the one provided. 
         */
        throw new UnsupportedOperationException("TO be implemented");
//           try {
//            totalRows.incrementAndGet();
//            Object o = totest.testGet(value, path);  
//            checkArgument(o.equals(value));
//            log.log(Level.FINE, "Tested value {0}", 0);
//            testedRows.incrementAndGet();
//         
//        } catch (Exception e) {
//            failedRows.incrementAndGet();
//            log.log(Level.WARNING, "Invalid get requestfor value {0} with path{1} : {2}",
//                    new Object[]{value, Arrays.toString(path), e.getMessage()});
//            log.log(Level.SEVERE, "Invalid get Request", e);
//          
//        }
    }
}
