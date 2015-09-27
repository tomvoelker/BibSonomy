package org.bibsonomy.search.util;import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 */
public interface ResourceConverter<R extends Resource, T> {

	public T convert(final Post<R> post);
	
	public Post<R> convert(final T source);
}
