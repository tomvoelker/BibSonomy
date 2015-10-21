package org.bibsonomy.search.management;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.generator.SearchIndexGeneratorTask;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.util.MappingBuilder;
import org.bibsonomy.search.util.ResourceConverter;

/**
 * container for one kind of search index
 *
 * @author dzo
 * 
 * @param <R> 
 * @param <T> 
 * @param <I> 
 * @param <M> 
 */
public abstract class SearchIndexContainer<R extends Resource, T, I extends SearchIndex<R, T, I, M>, M> {
	private static final Log log = LogFactory.getLog(SearchIndexContainer.class);
	
	private boolean enabled; // TODO: use this property? TODODZO
	
	private final String id;
	private final Class<R> resourceType;
	
	private final ConcurrentMap<I, ReadWriteLock> locksByIndex = new ConcurrentHashMap<>();
	
	protected I activeIndex;
	protected I inactiveIndex;
	
	private final ResourceConverter<R, T> converter;
	private final MappingBuilder<M> mappingBuilder;
	
	private final Semaphore generatorLock;
	private final ExecutorService generatorExecutorService;
	
	/**
	 * @param resourceType
	 * @param id
	 * @param converter
	 */
	public SearchIndexContainer(final Class<R> resourceType, final String id, final ResourceConverter<R, T> converter, final MappingBuilder<M> mappingBuilder) {
		this.id = id;
		this.converter = converter;
		this.mappingBuilder = mappingBuilder;
		
		this.resourceType = resourceType;
		
		this.generatorLock = new Semaphore(1);
		this.generatorExecutorService = Executors.newFixedThreadPool(1);
	}

	/**
	 * @return
	 */
	public SearchIndex<R, T, I, M> getIndexToUpdate() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param indexToUpdate
	 * @return
	 */
	public SearchIndexState getUpdaterStateForIndex(SearchIndex<R, T, I, M> indexToUpdate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public abstract SearchIndexUpdater<R> createUpdaterForIndex(I index);
	
	/**
	 * @param index
	 */
	public void activateIndex(final I index) {
		if (this.activeIndex != null) {
			try (final IndexLock<R, T, I, M> lock = this.acquireWriteLockForIndex(this.activeIndex)) {
				this.lockAndSwitchIndices(index);
			}
		} else {
			this.lockAndSwitchIndices(index);
		}
	}

	/**
	 * @param index
	 */
	private void lockAndSwitchIndices(final I index) {
		try (final IndexLock<R, T, I, M> newIndexLock = this.acquireWriteLockForIndex(index)) {
			this.doSwitchIndex(this.activeIndex, index, this.inactiveIndex);
			this.inactiveIndex = this.activeIndex;
			this.activeIndex = index;
		}
	}
	
	/**
	 * @param oldActiveIndex 
	 * @param newActiveIndex 
	 * @param inactiveIndex 
	 */
	protected abstract void doSwitchIndex(final I oldActiveIndex, final I newActiveIndex, final I inactiveIndex);
	
	/**
	 * replaces the oldindex with the new index
	 * @param oldIndex
	 * @param newIndex
	 */
	public abstract void replaceOldIndexWithNewOne(final I oldIndex, final I newIndex);
	
	/**
	 * @param index
	 */
	public void deletedIndex(final I index) {
		// clean up the locks
		this.locksByIndex.remove(index);
	}
	
	/**
	 * @param indexId
	 * @param inputLogic
	 * @return the task to execute
	 */
	public abstract SearchIndexGeneratorTask<R, I> createRegeneratorTaskForIndex(String indexId, final SearchDBInterface<R> inputLogic);
	
	
	public IndexLock<R, T, I, M> acquireWriteLockForIndex(final I index) {
		final ReadWriteLock lock = getLockForIndex(index);
		final Lock writeLock = lock.writeLock();
		return new IndexLock<R, T, I, M>(index, writeLock);
	}
	
	public IndexLock<R, T, I, M> acquireReadLockForIndex(final I index) {
		final ReadWriteLock lock = getLockForIndex(index);
		final Lock readLock = lock.readLock();
		return new IndexLock<R, T, I, M>(index, readLock);
	}

	/**
	 * @param index
	 * @return
	 */
	private ReadWriteLock getLockForIndex(final I index) {
		ReadWriteLock lock = this.locksByIndex.get(index);
		if (lock == null) {
			final ReadWriteLock newLock = new ReentrantReadWriteLock();
			lock = this.locksByIndex.putIfAbsent(index, newLock);
			if (lock == null) {
				return newLock;
			}
		}
		return lock;
	}
	
	/**
	 * @param indexId
	 * @param searchDB 
	 */
	public void generateIndex(String indexId, final SearchDBInterface<R> searchDB) {
		if (this.generatorLock.tryAcquire()) {
			final SearchIndexGeneratorTask<R, I> generatorTask = this.createRegeneratorTaskForIndex(indexId, searchDB);
			log.info("starting generation task.");
			// TODO: get status of generating task
			this.generatorExecutorService.submit(new Callable<Void>() {
				
				/* (non-Javadoc)
				 * @see java.util.concurrent.Callable#call()
				 */
				@Override
				public Void call() throws Exception {
					try {
						generatorTask.call();
						// if the generation was successful activate index
						if (generatorTask.isFinishedSuccessfully()) {
							replaceOldIndexWithNewOne(generatorTask.getOldSearchIndex(), generatorTask.getSearchIndex());
						}
					} catch (Exception e) {
						log.error("error while generating index.", e);
					}
					
					// release the lock
					SearchIndexContainer.this.generatorLock.release();
					
					return null;
				}
			});
			log.info("finished generation task");
			
		} else {
			log.warn("can't acquire lock for index generation,");
		}
	}

	/**
	 * 
	 */
	public void shutdown() {
		this.generatorExecutorService.shutdownNow();
	}

	/**
	 * @return the converter
	 */
	public ResourceConverter<R, T> getConverter() {
		return this.converter;
	}
	
	/**
	 * @return the mappingBuilder
	 */
	public MappingBuilder<M> getMappingBuilder() {
		return this.mappingBuilder;
	}
	
	/**
	 * @return the resourceType
	 */
	public Class<R> getResourceType() {
		return this.resourceType;
	}
	
	/**
	 * @return the resource type as string
	 */
	public String getResourceTypeAsString() {
		return ResourceFactory.getResourceName(this.resourceType);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
}
