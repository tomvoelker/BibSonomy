package org.bibsonomy.search.es.generator;

import java.io.IOException;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.management.ElasticSearchIndex;
import org.bibsonomy.search.generator.SearchIndexGeneratorTask;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;

/**
 * TODO: add documentation to this class
 * 
 * @author lutful
 * @author jil
 * @author dzo
 * @param <R> 
 */
public class ElasticSearchIndexGeneratorTask<R extends Resource> extends SearchIndexGeneratorTask<R, ElasticSearchIndex<R>> {
	private SearchIndexUpdater<R> updater;

	/**
	 * @param inputLogic
	 * @param searchIndex
	 * @param client 
	 */
	public ElasticSearchIndexGeneratorTask(SearchDBInterface<R> inputLogic, ElasticSearchIndex<R> searchIndex) {
		super(inputLogic, searchIndex);
		this.updater = this.searchIndex.getContainer().createUpdaterForIndex(this.searchIndex);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.generator.SearchIndexGeneratorTask#writeMetaInfo(org.bibsonomy.search.update.SearchIndexState)
	 */
	@Override
	protected void writeMetaInfo(SearchIndexState newState) {
		this.updater.updateIndexState(newState);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.generator.SearchIndexGeneratorTask#addPostToIndex(org.bibsonomy.search.SearchPost)
	 */
	@Override
	protected void addPostToIndex(SearchPost<R> post) {
		this.updater.insertPost(post);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.generator.SearchIndexGeneratorTask#createEmptyIndex()
	 */
	@Override
	protected void createEmptyIndex() throws IOException {
		this.updater.createEmptyIndex();
	}

}
