package org.bibsonomy.search.es.management.generation;

import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.ElasticsearchManager;

/**
 * task for index regeneration
 * @author dzo
 */
public final class ElasticSearchIndexRegenerationTask<T> extends AbstractSearchIndexGenerationTask<T> {
	private String toRegenerateIndexName;

	/**
	 * @param manager
	 * @param generator
	 * @param newIndexName
	 * @param toRegenerateIndexName
	 */
	public ElasticSearchIndexRegenerationTask(final ElasticsearchManager<T> manager, ElasticsearchIndexGenerator<T> generator, final String newIndexName, final String toRegenerateIndexName) {
		super(manager, generator, newIndexName);
		this.toRegenerateIndexName = toRegenerateIndexName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.post.ElasticsearchPostManager.ElasticSearchIndexGenerationTask#indexGenerated(org.bibsonomy.search.es.management.ElasticsearchIndex)
	 */
	@Override
	protected void indexGenerated(final String indexName) {
		this.manager.activateNewIndex(indexName, this.toRegenerateIndexName);
	}
}