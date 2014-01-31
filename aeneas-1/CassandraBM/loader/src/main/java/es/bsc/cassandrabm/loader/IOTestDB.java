/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.loader;

import es.bsc.cassandrabm.loader.exceptions.InvalidGetRequest;
import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import es.bsc.cassandrabm.loader.exceptions.NotSupportedQuery;
import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;
import org.apache.commons.configuration.Configuration;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;


/**
 *
 * @author cesare
 */
public class IOTestDB implements DBSetter {

    private final static Logger log = Logger.getLogger(IOTestDB.class.getName());
    private AbstractCassandraDB totest;
    public AtomicInteger totalRows = new AtomicInteger(0);
    public AtomicInteger testedRows = new AtomicInteger(0);
    public AtomicInteger untestedRows = new AtomicInteger(0);
    public AtomicInteger failedRows = new AtomicInteger(0);

    public IOTestDB(AbstractCassandraDB totest) {
        this.totest = totest;
    }

    @Override
    public void open(String ClusterName, String location) throws UnreachableClusterException {
       totest.open(ClusterName, location);
    }

    @Override
    public void close() {
        totest.close();
        log.log(Level.INFO, "Tested {0} rows on {1} with {2} unsupported query .{3} failed",
                new Object[]{testedRows.get(), totalRows.get(), untestedRows.get(), failedRows.get()});

    }

    @Override
    public void configure() {
        totest.configure();
    }
   

    @Override
    public void put(Object value, Object... path) throws InvalidPutRequest {

        try {
            totalRows.incrementAndGet();
            Object o = totest.testGet(value, path);  
            checkArgument(o.equals(value));
            log.log(Level.FINE, "Tested value {0}", 0);
            testedRows.incrementAndGet();
        } catch (NotSupportedQuery nsq) {
            log.log(Level.WARNING, "Not supportable query for value {0} with path{1} : {2}",
                    new Object[]{value, Arrays.toString(path), nsq.getMessage()});
            untestedRows.incrementAndGet();
        } catch (InvalidGetRequest e) {
            failedRows.incrementAndGet();
            log.log(Level.WARNING, "Invalid get requestfor value {0} with path{1} : {2}",
                    new Object[]{value, Arrays.toString(path), e.getMessage()});
            log.log(Level.SEVERE, "Invalid get Request", e);
          
        }


    }
}
