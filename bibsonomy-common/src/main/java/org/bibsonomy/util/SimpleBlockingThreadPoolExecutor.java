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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A very basic executor (similar to {@link Executor})  that keeps track of
 * scheduled and running tasks in a type-safe way and can be easily
 * configured and shut down from spring
 * 
 * @author jensi
 * @param <R>
 */
@SuppressWarnings("javadoc")
public class SimpleBlockingThreadPoolExecutor<R extends Callable<?>> {
	private final String name;
	private BlockingThreadPoolExecutor executor;
	private int minThreadPoolSize = 0;
	private int maxThreadPoolSize = 1;
	private int queueLength = Integer.MAX_VALUE;
	private long secondsBeforeClosingUnusedThreads = 20;
	private boolean shutdown = false;

	/**
	 * @param name
	 *            name prefix for all the threads of this executor
	 */
	public SimpleBlockingThreadPoolExecutor(final String name) {
		this.name = name;
	}

	private BlockingThreadPoolExecutor getExecutor() {
		if (this.executor == null) {
			synchronized (this) {
				if (shutdown == true) {
					throw new IllegalStateException("already shut down");
				}
				if (this.executor == null) {
					this.executor = new BlockingThreadPoolExecutor(this.name, minThreadPoolSize, maxThreadPoolSize, secondsBeforeClosingUnusedThreads, TimeUnit.SECONDS, queueLength);
				}
			}
		}
		return this.executor;
	}

	/**
	 * @param task
	 *            the task to be scheduled for execution. It will be executed as
	 *            soon as a thread in the thread pool becomes available
	 */
	public void scheduleTaskForExecution(R task) {
		this.scheduleTaskForExecution(task, false);
	}

	/**
	 * @param task
	 *            the task to be scheduled for execution. It will be executed as
	 *            soon as a thread in the thread pool becomes available
	 * @param sync
	 *            if true, this method does not return before the given task has
	 *            been executed.
	 */
	public void scheduleTaskForExecution(final R task, boolean sync) {
		try {
			final ExecutorService exec = getExecutor();
			if (sync) {
				exec.submit((Callable<?>) task).get();
			} else {
				exec.submit((Callable<?>) task);
			}
		} catch (InterruptedException e) {
			Thread.interrupted();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " "  + this.name + " " + executor;
	}

	/**
	 * 
	 */
	public void shutdownNow() {
		synchronized (this) {
			this.shutdown = true;
			if (executor != null) {
				executor.shutdownNow();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<R> getWaitingTasks() {
		return (List<R>) getExecutor().getWaitingTasks();
	}

	@SuppressWarnings("unchecked")
	public List<R> getRunningTasks() {
		return (List<R>) getExecutor().getRunningTasks();
	}
	
	public List<R> getUnfinishedTasks() {
		List<R> rVal = (List<R>) getExecutor().getWaitingTasks();
		rVal.addAll((List<R>) getExecutor().getRunningTasks());
		return rVal;
	}

	public void setMinThreadPoolSize(int minThreadPoolSize) {
		this.minThreadPoolSize = minThreadPoolSize;
	}

	public void setMaxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	/**
	 * @param queueSizeBeforeBlockingTaskSubmission the maximum allowed queue length. Tasks submissions which would cause the queue length to exceed this number are blocked until the queue length decreased.
	 */
	public void setQueueLength(int queueSizeBeforeBlockingTaskSubmission) {
		this.queueLength = queueSizeBeforeBlockingTaskSubmission;
	}
	
	public void setSecondsBeforeClosingUnusedThreads(long secondsBeforeClosingUnusedThreads) {
		this.secondsBeforeClosingUnusedThreads = secondsBeforeClosingUnusedThreads;
	}
}
