package org.bibsonomy.search.management;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.bibsonomy.model.Resource;
import org.bibsonomy.util.LockAutoCloseable;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 * @author dzo
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public class IndexLock<R extends Resource, T, I extends SearchIndex<R, T, I>> extends LockAutoCloseable {
	
	private final SearchIndex<R, T, I> searchIndex;
	
	/**
	 * @param searchIndex 
	 * @param lock
	 */
	public IndexLock(final SearchIndex<R, T, I> searchIndex, Lock lock) {
		super(lock);
		this.searchIndex = searchIndex;
	}

	/**
	 * @param searchIndex 
	 * @param lock
	 * @param maxWaitForLock
	 * @param maxWaitForLockUnit
	 * @throws LockFailedException
	 */
	public IndexLock(final SearchIndex<R, T, I> searchIndex, Lock lock, long maxWaitForLock, TimeUnit maxWaitForLockUnit) throws LockFailedException {
		super(lock, maxWaitForLock, maxWaitForLockUnit);
		this.searchIndex = searchIndex;
	}

	/**
	 * @return the searchIndex
	 */
	public SearchIndex<R, T, I> getSearchIndex() {
		return this.searchIndex;
	}
}
