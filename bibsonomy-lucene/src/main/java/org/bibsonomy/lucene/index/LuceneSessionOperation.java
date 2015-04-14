package org.bibsonomy.lucene.index;

import org.apache.lucene.search.IndexSearcher;

/**
 * Callback interface in which all lucene operations should be done (typically using anonymous implementation classes).
 *
 * @author jil
 * @param <T> return type of the operation.
 * @param <E> the exceptions that may be thrown by the operation
 */
public interface LuceneSessionOperation<T, E extends Exception> {
	/**
	 * All Queries to a lucene index requiring access to an {@link IndexSearcher} should be done using an implementation of this method.
	 * 
	 * @param searcher on which the operation may operate
	 * @return the result of the operation.
	 * @throws E 
	 */
	public T doOperation(IndexSearcher searcher) throws E;
}
