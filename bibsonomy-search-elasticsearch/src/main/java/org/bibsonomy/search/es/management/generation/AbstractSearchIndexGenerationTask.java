package org.bibsonomy.search.es.management.generation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;

import java.util.concurrent.Callable;

/**
 * abstract index generation task
 * @param <T>
 *
 * @author dzo
 */
public abstract class AbstractSearchIndexGenerationTask<T> implements Callable<Void> {
	private static final Log LOG = LogFactory.getLog(AbstractSearchIndexGenerationTask.class);


	protected final ElasticsearchManager<T> manager;
	private final ElasticsearchIndexGenerator<T> generator;
	private final String newIndexName;

	/**
	 * @param manager
	 * @param generator
	 * @param newIndexName
	 */
	public AbstractSearchIndexGenerationTask(ElasticsearchManager<T> manager, ElasticsearchIndexGenerator<T> generator, final String newIndexName) {
		this.manager = manager;
		this.generator = generator;
		this.newIndexName = newIndexName;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public final Void call() {
		try {
			this.manager.generatingIndex(this.generator);
			this.generator.generateIndex();
			this.indexGenerated(this.newIndexName);
		} catch (final Exception e) {
			LOG.error("error while generating index", e);
		} finally {
			this.manager.generatedIndex(this.generator);
		}

		return null;
	}

	/**
	 * @param indexName
	 */
	protected abstract void indexGenerated(final String indexName);
}