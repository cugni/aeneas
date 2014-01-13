package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.core.loader.exceptions.UnreachableClusterException;


public interface DBSource {
	public abstract void open(String clusterName, String location) 
                throws UnreachableClusterException;
        /*
         * It is important to close the source after have completed an completed
         * insertion to free the resources.
         *  If there are pending  tasks  it could block until their termination.
         */
	public abstract void close();
	
}