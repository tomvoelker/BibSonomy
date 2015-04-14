package org.bibsonomy.lucene.index;

import java.io.Closeable;
import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;


/**
 * A session for doing lucene queries that should be used for only one transaction.
 *
 * @author jil
 */
public class LuceneSession implements Closeable {
	
	private final LuceneResourceIndex<?> index;

	/**
	 * Constructs a lucene session. Only meant to be called by the ResourceIndex
	 * @param index
	 */
	protected LuceneSession(final LuceneResourceIndex<?> index) {
		this.index = index;
	}
	
	
	/**
	 * All Queries to a lucene index should be done using this method
	 * 
	 * @param op the operation to be performed on this index
	 * @return the result object returned by the operation argument
	 * @throws IOException 
	 */
	public <T, E extends Exception> T execute(LuceneSessionOperation<T, E> op) throws IOException, E {
		IndexSearcher searcher = null;
		try {
			searcher = this.index.acquireIndexSearcher();
			return op.doOperation(searcher);
		} finally {
			if (searcher != null) {
				this.index.releaseIndexSearcher(searcher);
			}
		}
	}
	
	/**
	 * close and release the {@link LuceneSession}
	 */
	@Override
	public void close() {
		index.closeSession(this);
	}
}
