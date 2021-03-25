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
	public ElasticSearchIndexRegenerationTask(final ElasticsearchManager<T, ?> manager, ElasticsearchIndexGenerator<T, ?> generator, final String newIndexName, final String toRegenerateIndexName) {
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