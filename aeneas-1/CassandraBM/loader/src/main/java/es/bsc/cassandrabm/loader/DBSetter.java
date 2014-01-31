package es.bsc.cassandrabm.loader;

import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import org.apache.commons.configuration.Configuration;
/*
 * This interface provide an generic Api for
 * configure and fill a database source
 */
public interface DBSetter extends DBSource{
	/*
	 * Opens a connections with the given cluster server
	 */
	
	/*
	 * Configure the data model and the Cassandra settings.
	 */
	public void configure( );
        /*
         * Method for insert value in Cassandra 
         * @param value
         *      the leaf of the three
         * @param path
         *      the sequence of node passed to reach the leaf
         */
	public void put(Object value,Object... path)
			throws InvalidPutRequest;
}
