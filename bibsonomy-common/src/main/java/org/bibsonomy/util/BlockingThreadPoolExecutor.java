/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ThreadPoolExecutor} extension that blocks task submission as long as
 * a given queue-length is exceeded. This {@link Executor}-implementation
 * accepts a name prefix which is set for all its threads. It also Keeps track
 * of scheduled and running tasks.
 * 
 * @author jensi
 */
public class BlockingThreadPoolExecutor extends ThreadPoolExecutor {
	/**
	 * A {@link ThreadFactory} which uses the default settings but customizes
	 * the name of the threads.
	 * 
	 * @author jensi
	 */
	private static class NamingDefaultThreadFactory implements ThreadFactory {

		private final String threadPoolName;

		/**
		 * @param threadPoolName
		 */
		public NamingDefaultThreadFactory(String threadPoolName) {
			this.threadPoolName = threadPoolName;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread rVal = Executors.defaultThreadFactory().newThread(r);
			String oldName = rVal.getName();
			int idx = oldName.indexOf("-thread-");
			if (idx > 0) {
				rVal.setName(threadPoolName + oldName.substring(idx));
			} else {
				rVal.setName(threadPoolName + "-" + oldName);
			}
			return rVal;
		}

	}

	/**
	 * A {@link FutureTask} which keeps track of threads changing state from
	 * queued to running and from running to finished. Also takes care of
	 * blocking internal task wrapping if the queue length is exceeded.
	 * 
	 * @author jensi
	 */
	protected final class FutureTaskExt<T> extends FutureTask<T> {
		private final Callable<T> callable;

		/**
		 * @param callable
		 */
		private FutureTaskExt(Callable<T> callable) {
			super(callable);
			this.callable = callable;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.FutureTask#run()
		 */
		@Override
		public void run() {
			synchronized (runningTasks) {
				runningTasks.add(this.callable);
			}
			semaphore.release();
			try {
				super.run();
			} finally {
				synchronized (runningTasks) {
					runningTasks.remove(this.callable);
				}
			}
		}

		public Callable<T> getCallable() {
			return this.callable;
		}
	}

	private final Semaphore semaphore;

	private final List<Callable<?>> runningTasks = new ArrayList<>();

	/**
	 * @param threadPoolName
	 * @param minPoolSize
	 * @param maxPoolSize
	 * @param keepAliveTime
	 * @param keepAliveTimeUnit
	 * @param queueLength
	 */
	public BlockingThreadPoolExecutor(final String threadPoolName, int minPoolSize, int maxPoolSize, long keepAliveTime, TimeUnit keepAliveTimeUnit, int queueLength) {
		super(minPoolSize, maxPoolSize, keepAliveTime, keepAliveTimeUnit, new LinkedBlockingDeque<Runnable>(queueLength), new NamingDefaultThreadFactory(threadPoolName));
		this.semaphore = new Semaphore(queueLength);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T result) {
		waitForTaskCapacity();
		return new FutureTaskExt<T>(Executors.callable(runnable, result));
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		waitForTaskCapacity();
		return new FutureTaskExt<T>(callable);
	}

	private void waitForTaskCapacity() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * @return a thread safe copy of the task-queue
	 */
	public List<Callable<?>> getWaitingTasks() {
		final BlockingQueue<Runnable> waitingTasks = getQueue();
		List<Callable<?>> rVal = new ArrayList<>();
		synchronized (waitingTasks) {
			for (Runnable runnable : waitingTasks) {
				rVal.add(((FutureTaskExt<?>) runnable).getCallable());
			}
		}
		return rVal;
	}

	/**
	 * @return a thread safe copy of the list of running tasks
	 */
	public List<Callable<?>> getRunningTasks() {
		synchronized (runningTasks) {
			return new ArrayList<>(runningTasks);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + semaphore.availablePermits() + " " + super.toString();
	}
}
