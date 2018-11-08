package org.bibsonomy.search.es.management.generation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.management.ElasticsearchManager;

import java.util.concurrent.Callable;

/**
 * abstract index generation task
 * @param <T>
 *
 * @author dzo
 */
public abstract class AbstractSearchIndexGenerationTask<T> implements Callable<Void> {
	private static final Log LOG = LogFactory.getLog(AbstractSearchIndexGenerationTask.class);

	/** the manager that is started the recommendation task */
	protected final ElasticsearchManager<T, ?> manager;
	/** the generator to use */
	private final ElasticsearchIndexGenerator<T, ?> generator;
	/** the name of the index to generate */
	private final String newIndexName;

	/**
	 * @param manager
	 * @param generator
	 * @param newIndexName
	 */
	public AbstractSearchIndexGenerationTask(ElasticsearchManager<T, ?> manager, ElasticsearchIndexGenerator<T, ?> generator, final String newIndexName) {
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
			this.generator.generateIndex(this.newIndexName);
			this.indexGenerated(this.newIndexName);
		} catch (final Exception e) {
			LOG.error("error while generating index", e);
			throw new RuntimeException(e);
		} finally {
			this.manager.generatedIndex();
		}

		return null;
	}

	/**
	 * @param indexName
	 */
	protected abstract void indexGenerated(final String indexName);
}