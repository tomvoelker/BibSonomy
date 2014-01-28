package org.bibsonomy.lucene.util.generator;

import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author bsc
 * @param <R> 
 */
public interface GenerateIndexCallback<R extends Resource> {
	
	/**
	 * called when generating of index is done
	 * @param index the generated index
	 */
	public void generatedIndex(final LuceneResourceIndex<R> index);
}
