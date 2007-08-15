/*
 * Created on 05.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package recommender.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class MultiThreadIteratorAdapterTest extends TestCase {

	
	public void testSomething() {
		List<Integer> srcList = Arrays.asList(new Integer[] {1,2,3,4,5,6,7,8,9,0});
		final Iterable<Integer> mtIterable = new MultiThreadIterableAdapter<Integer>( 3, 3, srcList.iterator() );
		
		final boolean[][] hasNext = new boolean[3][3];
		final boolean[] noSuchElementException = new boolean[3];
		final List<Integer>[] lists = new ArrayList[3];
		Thread thread0 = new Thread(new Runnable() {
			public void run() {
				lists[0] = new ArrayList<Integer>(10);
				Iterator<Integer> it = mtIterable.iterator();
				hasNext[0][0] = it.hasNext();
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				hasNext[0][1] = it.hasNext();
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				lists[0].add(it.next());
				hasNext[0][2] = it.hasNext();
				try {
					it.next();
					noSuchElementException[0] = false;
				} catch (NoSuchElementException e) {
					noSuchElementException[0] = true;
				}
			}
		});
		
		thread0.start();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		assertEquals(3, lists[0].size());
		
		Thread thread1 = new Thread(new Runnable() {
			public void run() {
				lists[1] = new ArrayList<Integer>(10);
				Iterator<Integer> it = mtIterable.iterator();
				hasNext[1][0] = it.hasNext();
				hasNext[1][1] = it.hasNext();
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				lists[1].add(it.next());
				hasNext[1][2] = it.hasNext();
				try {
					it.next();
					noSuchElementException[1] = false;
				} catch (NoSuchElementException e) {
					noSuchElementException[1] = true;
				}
			}
		});
		
		thread1.start();
		
		Thread thread2 = new Thread(new Runnable() {
			public void run() {
				lists[2] = new ArrayList<Integer>(10);
				Iterator<Integer> it = mtIterable.iterator();
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				hasNext[2][0] = it.hasNext();
				hasNext[2][1] = it.hasNext();
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				lists[2].add(it.next());
				hasNext[2][2] = it.hasNext();
				try {
					it.next();
					noSuchElementException[2] = false;
				} catch (NoSuchElementException e) {
					noSuchElementException[2] = true;
				}
			}
		});
		thread2.start();
		
		try {
			thread0.join();
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		
		for (int j=1; j<3; ++j) {
			assertEquals(srcList.size(), lists[j].size());
			for (int i=0; i<10; ++i) {
				assertEquals(srcList.get(i), lists[j].get(i));
			}
		}
		
		for (int i=0; i<3; ++i) {
			assertTrue("hasNext[" + i + "][0] should be true", hasNext[i][0]);
			assertTrue("hasNext[" + i + "][1] should be true", hasNext[i][1]);
			assertFalse("hasNext[" + i + "][2] should be false", hasNext[i][2]);
			assertTrue("noSuchElementException[" + i + "] should be true", noSuchElementException[i]);
		}
		
		try {
			mtIterable.iterator();
			fail("no RuntimeException thrown");
		} catch (RuntimeException e) {
			
		}
	}
	
	
	public void testLoop() {
		final int CONSUMER_THREADS = 30;
		final long lastTime[] = new long[CONSUMER_THREADS];
		final Thread[] threads = new Thread[CONSUMER_THREADS];
		final boolean stop[] = new boolean[CONSUMER_THREADS];
		final boolean ok[] = new boolean[CONSUMER_THREADS];
		
		Iterator<Integer> it = new Iterator<Integer>() {
			int i = 0;
			public boolean hasNext() {
				try {
					Thread.sleep((long)(Math.random() * 50d));
				} catch (InterruptedException e) {
				}
				return true;
			}

			public Integer next() {
				try {
					Thread.sleep((long)(Math.random() * 50d));
				} catch (InterruptedException e) {
				}
				return i++;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		MultiThreadIterableAdapter<Integer> adapter = new MultiThreadIterableAdapter<Integer>(CONSUMER_THREADS, 9, it);
		
		for (int i = 0; i < CONSUMER_THREADS; ++i) {
			final int me = i;
			final Iterator<Integer> myIt = adapter.iterator();
			stop[i] = false;
			ok[i] = true;
			threads[i] = new Thread() {

				public void run() {
					int last = -1;
					while (myIt.hasNext() && !stop[me]) {
						int nr = myIt.next();
						boolean okThisTime = ((last + 1) == nr);
						if (okThisTime == false) {
							System.out.println(nr + " instead of " + (last + 1));
							ok[me] = false;
						}
						lastTime[me] = System.currentTimeMillis();
						try {
							sleep((long)(Math.random() * 50d));
						} catch (InterruptedException e) {
							break;
						}
						last = nr;
					}
				}
				
			};
			threads[i].start();
		}
		
		try {
			Thread.sleep(4 * CONSUMER_THREADS * 150);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		for (int i = 0; i < CONSUMER_THREADS; ++i) {
			stop[i] = true;
			threads[i].interrupt();
			assertTrue(ok[i]);
			assertTrue( (System.currentTimeMillis() < (lastTime[i]  + CONSUMER_THREADS * 150)) );
		}
		adapter.stop();
	}

}
