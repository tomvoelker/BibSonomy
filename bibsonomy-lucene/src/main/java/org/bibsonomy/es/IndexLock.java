package org.bibsonomy.es;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.bibsonomy.util.LockAutoCloseable;

/**
 * Access to the index should be done by first acquiring an {@link IndexLock}. The index name returned by {@link #getIndexName()} is then guaranteed to be the one that has been locked.
 *
 * @author jensi
 */
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
