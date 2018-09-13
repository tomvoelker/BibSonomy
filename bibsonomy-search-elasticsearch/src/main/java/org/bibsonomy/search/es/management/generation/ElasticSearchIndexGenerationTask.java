package org.bibsonomy.search.es.management.generation;

import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.ElasticsearchManager;

/**
 * task for index generation
 * @author dzo
 */
public class ElasticSearchIndexGenerationTask<T> extends AbstractSearchIndexGenerationTask<T> {
	private boolean activateIndexAfterGeneration;

	/**
	 * @param manager
	 * @param generator
	 * @param newIndex
	 * @param activateIndexAfterGeneration
	 */
	public ElasticSearchIndexGenerationTask(final ElasticsearchManager<T> manager, ElasticsearchIndexGenerator<T> generator, String newIndex, boolean activateIndexAfterGeneration) {
		super(manager, generator, newIndex);
		this.activateIndexAfterGeneration = activateIndexAfterGeneration;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.post.ElasticsearchPostManager.AbstractSearchIndexGenerationTask#indexGenerated(org.bibsonomy.search.es.management.ElasticsearchIndex)
	 */
	@Override
	protected void indexGenerated(final String generatedIndex) {
		if (this.activateIndexAfterGeneration) {
			this.manager.activateNewIndex(generatedIndex, null);
		}
	}
}