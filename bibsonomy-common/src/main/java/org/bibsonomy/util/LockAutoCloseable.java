/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
