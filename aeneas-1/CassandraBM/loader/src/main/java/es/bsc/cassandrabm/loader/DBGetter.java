package es.bsc.cassandrabm.loader;

import es.bsc.cassandrabm.loader.exceptions.InvalidGetRequest;
import es.bsc.cassandrabm.loader.exceptions.NotSupportedQuery;

public interface DBGetter extends DBSource{
	public Object testGet(Object value,Object... path)
			throws InvalidGetRequest,NotSupportedQuery;
}
