/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
