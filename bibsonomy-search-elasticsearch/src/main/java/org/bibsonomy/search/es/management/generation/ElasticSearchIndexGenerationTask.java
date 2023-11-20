/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
	public ElasticSearchIndexGenerationTask(final ElasticsearchManager<T, ?> manager, ElasticsearchIndexGenerator<T, ?> generator, String newIndex, boolean activateIndexAfterGeneration) {
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