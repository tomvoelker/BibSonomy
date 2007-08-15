package recommender.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;


/** 
 * Implementiert intern einen Producer-Thread für einen Puffer für einen Iterator,
 * damit mehrere Threads parallel einen jeweils einen eigenen Iterator durchlaufen können
 */ 
public class MultiThreadIterableAdapter<T> implements Iterable<T> {
	private static final Logger log = Logger.getLogger(MultiThreadIterableAdapter.class);
	
	private static class BufferProducer<T> extends Thread {
		private CountDownLatch[] latches = new CountDownLatch[3];
		private List<T>[] buffers = new ArrayList[3]; // TODO: kann man das überhaupt auch ohne Warnung hinkriegen??
		private int curBufferIndex = 0;
		private boolean isComplete;
		private Iterator<T> iterator;
		private int threadCount;
		private int dividedBufferSize;
		private boolean disfunc = false;
		private boolean stop = false;
		
		public BufferProducer(int threadCount, int dividedBufferSize, Iterator<T> iterator) {
			for (int i=0; i<3; ++i) {
				this.latches[i] = new CountDownLatch(0);
				this.buffers[i] = new ArrayList<T>(dividedBufferSize);
			}
			this.iterator = iterator;
			this.isComplete = !iterator.hasNext();
			this.threadCount = threadCount;
			this.dividedBufferSize = dividedBufferSize;
		}
		
		public void run() {
			while (iterator.hasNext()) {
				List<T> buf = nextProducerBuffer();
				if ((disfunc == true) || (stop == true)) {
					break;
				}
				int addedCount = 0;
				while (iterator.hasNext() && (addedCount < dividedBufferSize)) {
					buf.add(iterator.next());
					++addedCount;
				}
				synchronized(this) {
					this.isComplete = !iterator.hasNext();
					++curBufferIndex;
					this.notifyAll();
				}
			}
		}
		
		private List<T> nextProducerBuffer() {
			try {
				log.debug("waiting for nextProducerBuffer " + System.identityHashCode(this) + " curBufferIndex=" + curBufferIndex);
				latches[curBufferIndex % 3].await();
				/*if (latches[curBufferIndex % 3].await(120,TimeUnit.SECONDS) == false) {
					log.error("Consumer-threads too slow or dead - giving up");
					this.iterator = null;
					synchronized(this) {
						this.disfunc = true;
						this.notifyAll();
					}
					System.gc();
				}*/
			} catch (InterruptedException e) {
				log.warn(e);
			}
			synchronized(this) {
				latches[curBufferIndex % 3] = new CountDownLatch(threadCount);
			}
			buffers[curBufferIndex % 3].clear();
			log.debug("got nextProducerBuffer curBufferIndex=" + curBufferIndex);
			return buffers[curBufferIndex % 3]; 
		}
		
		public static int getNextConsumerBufferIndex(int oldBufferIndex) {
			return oldBufferIndex + 1;
		}
		
		public synchronized List<T> getConsumerBuffer(int bufferIndex) {
			//log.debug("getConsumerBuffer("+bufferIndex+") on " + System.identityHashCode(this) + " start");
			if (bufferIndex > curBufferIndex) {
				try {
					log.debug("waiting for action and consumerbuffer from " + System.identityHashCode(this) + " index=" + bufferIndex + " curBufferIndex=" + curBufferIndex);
					this.wait();
				} catch (InterruptedException e) {
					log.warn(e.getClass().getSimpleName() + " while waiting for buffer before ConsumerIndex to be filled");
				}
			}
			if ((bufferIndex - 1) >= 0) {
				latches[(bufferIndex - 1) % 3].countDown();
			}
			if (bufferIndex == curBufferIndex) {
				if (isComplete == true) {
					log.debug("complete");
					return null;
				}
				try {
					if (disfunc) {
						throw new RuntimeException("Consumer-threads too slow or dead");
					}
					log.debug("waiting for consumerbuffer from " + System.identityHashCode(this) + " index=" + bufferIndex + " curBufferIndex=" + curBufferIndex);
					this.wait();
					log.debug("got consumerbuffer index=" + bufferIndex);
				} catch (InterruptedException e) {
					log.warn(e.getClass().getSimpleName() + " while waiting for new buffer to be filled");
				} finally {
					if (disfunc) {
						throw new RuntimeException("Consumer-threads too slow or dead");
					}
				}
			}
			log.debug("getConsumerBuffer("+bufferIndex+") on " + System.identityHashCode(this) + " end");
			return this.buffers[bufferIndex % 3];
		}
	}
	
	private BufferProducer<T> producer;
	private int registeredThreads = 0;
	
	public void stop() {
		producer.stop = true;
		producer.interrupt();
	}
	
	public MultiThreadIterableAdapter(int threadCount, int bufferSize, Iterator<T> iterator) {
		int dividedBufferSize = (int) Math.ceil( ((double)bufferSize) / 3d );
		this.producer = new BufferProducer<T>(threadCount,dividedBufferSize,iterator);
		producer.start();
	}
	
	
	public synchronized Iterator<T> iterator() {
		if (registeredThreads < producer.threadCount) {
			++registeredThreads;
			return new BufferIterator<T>(producer);
		}
		log.error("too many iterators reached");
		throw new RuntimeException("too many iterators reached");
	}
	
	/** Der Consumer-Iterator */
	private static class BufferIterator<T> implements Iterator<T> {
		private BufferProducer<T> producer;
		private int nextBufferIndex = 0;
		private int curIndex = 0;
		private List<T> buf;
		
		public BufferIterator(BufferProducer<T> producer) {
			this.producer = producer;
		}
		
		public boolean hasNext() {
			if ((buf == null) || (curIndex >= buf.size())) {
				//if (producer.isComplete == false) {
					buf = producer.getConsumerBuffer(nextBufferIndex);
					if (buf != null) {
						nextBufferIndex = BufferProducer.getNextConsumerBufferIndex(nextBufferIndex);
						curIndex = 0;
						return true;
					}
					return false;
				/*} else {
					return false;
				}*/
			}
			return (buf.size() > curIndex);
		}

		public T next() {
			if ((buf == null) || (curIndex >= buf.size())) {
				//if (producer.isComplete == false) {
					buf = producer.getConsumerBuffer(nextBufferIndex);
					if (buf != null) {
						nextBufferIndex = BufferProducer.getNextConsumerBufferIndex(nextBufferIndex);
						curIndex = 0;
					} else {
						throw new NoSuchElementException();
					}
				/*} else {
					throw new NoSuchElementException();
				}*/
			}
			T rVal = buf.get(curIndex);
			curIndex++;
			return rVal;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
