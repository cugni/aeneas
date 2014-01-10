package es.bsc.aeneas.loader;

import es.bsc.aeneas.loader.exceptions.InvalidGetRequest;
import es.bsc.aeneas.loader.exceptions.NotSupportedQuery;

public interface DBGetter extends DBSource{
	public Object testGet(Object value,Object... path)
			throws InvalidGetRequest,NotSupportedQuery;
}
