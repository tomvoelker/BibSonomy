/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
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
package org.bibsonomy.search.es.management;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.bibsonomy.util.LockAutoCloseable;

/**
 * Access to the index should be done by first acquiring an {@link IndexLock}.
 * The index name returned by {@link #getIndexName()} is then guaranteed to be
 * the one that has been locked.
 *
 * @author jensi
 */
@Deprecated // use IndexLock in search module instead TODODZO
public class IndexLock extends LockAutoCloseable {

	private final String indexName;

	/**
	 * @param indexName the name of the index that is to be locked
	 * @param lock
	 */
	public IndexLock(final String indexName, final Lock lock) {
		super(lock);
		this.indexName = indexName;
	}
	
	/**
	 * constructor with max wait for lock to acquire index
	 * @param indexName
	 * @param lock
	 * @param maxWaitForLock
	 * @param maxWaitForLockUnit
	 * @throws LockFailedException
	 */
	public IndexLock(final String indexName, final Lock lock, long maxWaitForLock, TimeUnit maxWaitForLockUnit) throws LockFailedException {
		super(lock, maxWaitForLock, maxWaitForLockUnit);
		this.indexName = indexName;
	}

	/**
	 * @return the name of the index that has been locked
	 */
	public String getIndexName() {
		return this.indexName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + ": " + this.indexName + "]"; 
	}
}
