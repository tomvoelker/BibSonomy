/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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