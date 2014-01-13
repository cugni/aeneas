package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.core.loader.exceptions.InvalidGetRequest;
import es.bsc.aeneas.core.loader.exceptions.NotSupportedQuery;

public interface DBGetter extends DBSource{
	public Object testGet(Object value,Object... path)
			throws InvalidGetRequest,NotSupportedQuery;
}
