package org.bibsonomy.util;

import java.util.concurrent.locks.Lock;

/**
 * {@link AutoCloseable} wrapper for {@link Lock}s
 *
 * @author jensi
 */
public class LockAutoCloseable implements AutoCloseable {
	private final Lock lock;
	private boolean unlocked = false;
	
	/**
	 * aquires a lock and unlocks in {@link #close()} method
	 * @param lock
	 */
	public LockAutoCloseable(final Lock lock) {
		this.lock = lock;
		this.lock.lock();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() {
		if (!unlocked) {
			lock.unlock();
			unlocked = true;
		}
	}
}
