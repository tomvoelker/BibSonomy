package org.bibsonomy.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * {@link AutoCloseable} wrapper for {@link Lock}s
 *
 * @author jensi
 */
public class LockAutoCloseable implements AutoCloseable {
	private final Lock lock;
	private boolean unlocked = false;
	
	public static class LockFailedException extends Exception {
	}
	
	/**
	 * aquires a lock and unlocks in {@link #close()} method
	 * @param lock
	 */
	public LockAutoCloseable(final Lock lock) {
		this.lock = lock;
		this.lock.lock();
	}
	
	public LockAutoCloseable(final Lock lock, long maxWaitForLock, TimeUnit maxWaitForLockUnit) throws LockFailedException {
		this.lock = lock;
		boolean lockAquired = false;
		try {
			lockAquired = this.lock.tryLock(maxWaitForLock, maxWaitForLockUnit);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (!lockAquired) {
			throw new LockFailedException();
		}
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
