package org.bibsonomy.search.management;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.generator.SearchIndexGeneratorTask;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.util.ResourceConverter;

/**
 * container for one kind of search index
 *
 * @author dzo
 * 
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public abstract class SearchIndexContainer<R extends Resource, T, I extends SearchIndex<R, T, I>> {
	private boolean enabled; // TODO: use this property? TODODZO
	
	private final String id;
	protected final Class<R> resourceClass;
	protected I activeIndex;
	protected I inactiveIndex;
	private final ResourceConverter<R, T> converter;
	
	/**
	 * @param resourceClass
	 * @param id
	 * @param converter
	 */
	public SearchIndexContainer(final Class<R> resourceClass, String id, ResourceConverter<R, T> converter) {
		super();
		this.id = id;
		this.converter = converter;
		this.resourceClass = resourceClass;
	}

	/**
	 * @return
	 */
	public SearchIndex<R, T, I> getIndexToUpdate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param indexToUpdate
	 * @return
	 */
	public SearchIndexState getUpdaterStateForIndex(SearchIndex<R, T, I> indexToUpdate) {
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
	public void activateIndex(SearchIndex<R, T, I> index) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the converter
	 */
	public ResourceConverter<R, T> getConverter() {
		return this.converter;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param indexId
	 * @param inputLogic 
	 * @return the task to execute
	 */
	public abstract SearchIndexGeneratorTask<R, I> createRegeneratorTaskForIndex(String indexId, final SearchDBInterface<R> inputLogic);
}
