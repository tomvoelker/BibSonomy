/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class BlockingQueueExecutorTest {
	private static final int SLEEP_MILLIS = 50;
	
	
	@Test
	public void testWaitingTaskQueue() throws InterruptedException {
		final Semaphore sem1 = new Semaphore(0);
		final Semaphore sem2 = new Semaphore(0);
		final Semaphore sem3 = new Semaphore(0);
		final SimpleBlockingThreadPoolExecutor<Callable<Void>> executor = new SimpleBlockingThreadPoolExecutor<Callable<Void>>("test2");
		final boolean[] doneContainer = new boolean[] {false };
		Assert.assertEquals(0, executor.getWaitingTasks().size());
		Assert.assertEquals(0, executor.getRunningTasks().size());
		executor.scheduleTaskForExecution(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				sem1.release();
				sem2.acquire();
				sem3.release();
				doneContainer[0] = true;
				return null;
			}
		});
		
		// wait until thread started
		sem1.acquire();
		Assert.assertEquals(0, executor.getWaitingTasks().size());
		Assert.assertEquals(1, executor.getRunningTasks().size());
		Assert.assertEquals(false, doneContainer[0]);
		// allow the thread to stop
		sem2.release();
		// wait until thread is about to stop
		sem3.acquire();
		Thread.sleep(100);
		Assert.assertEquals(true, doneContainer[0]);
		Assert.assertEquals(0, executor.getWaitingTasks().size());
		Assert.assertEquals(0, executor.getRunningTasks().size());
	}
	
	/**
	 * checks times to find out whether the task submission is blocked correctly
	 */
	@Test
	public void testIt() {
		SimpleBlockingThreadPoolExecutor<Callable<Void>> executor = new SimpleBlockingThreadPoolExecutor<Callable<Void>>("test");
		executor.setQueueLength(1);
		final int numTasks = 4;
		final long[] submissionTimes = new long[numTasks];
		final long[] startTimes = new long[numTasks];
		final long[] endTimes = new long[numTasks];
		final String[] names = new String[numTasks];
		final long timeBefore = System.nanoTime();
		for (int i = 0; i < numTasks; ++i) {
			final int finali = i;
			executor.scheduleTaskForExecution(new Callable<Void>() {

				/* (non-Javadoc)
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					return "task" + finali;
				}

				@Override
				public Void call() throws Exception {
					startTimes[finali] = System.nanoTime();
					names[finali] = Thread.currentThread().getName();
					try {
						Thread.sleep(SLEEP_MILLIS);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					endTimes[finali] = System.nanoTime();
					return null;
				}
			});
			submissionTimes[finali] = System.nanoTime();
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}
		System.out.println();
		System.out.println(submissionTimes[0] - timeBefore);
		for (int i = 1; i < submissionTimes.length; ++i) {
			System.out.println(submissionTimes[i] - submissionTimes[i-1]);
		}
		System.out.println();
		for (int i = 1; i < submissionTimes.length; ++i) {
			System.out.println(startTimes[i] - startTimes[i-1]);
		}
		System.out.println();
		for (int i = 0; i < submissionTimes.length; ++i) {
			System.out.println(endTimes[i] - startTimes[i]);
		}
		for (int i = 0; i < submissionTimes.length; ++i) {
			Assert.assertEquals("test-thread-1", names[i]);
		}
		assertNonParallel(startTimes, endTimes);
		
		Assert.assertTrue((submissionTimes[1] - submissionTimes[0]) < SLEEP_MILLIS * 1000 * 1000);
		Assert.assertTrue((submissionTimes[2] - timeBefore) > SLEEP_MILLIS * 1000 * 1000); // first finished, second in execution -> third can be submitted
		Assert.assertTrue((submissionTimes[3] - timeBefore) > 2 * SLEEP_MILLIS * 1000 * 1000); // first and second finished, third in execution -> fourth can be submitted
		Assert.assertTrue((submissionTimes[3] - submissionTimes[2]) < SLEEP_MILLIS * 1100 * 1000);
		assertBlockingOfTaskSubmission(submissionTimes);
		executor.shutdownNow();
		
	}


	private void assertNonParallel(final long[] startTimes, final long[] endTimes) {
		for (int i = 1; i < endTimes.length; ++i) {
			Assert.assertTrue(endTimes[i-1] < startTimes[i]);
		}
	}


	private void assertBlockingOfTaskSubmission(final long[] submissionTimes) {
		// one task in execution, one in the queue so submission should block after the second submission
		Assert.assertTrue((submissionTimes[1] - submissionTimes[0]) < (submissionTimes[2] - submissionTimes[1]));
		Assert.assertTrue((submissionTimes[2] - submissionTimes[1]) > SLEEP_MILLIS * 1000 * 1000);
	}
}
