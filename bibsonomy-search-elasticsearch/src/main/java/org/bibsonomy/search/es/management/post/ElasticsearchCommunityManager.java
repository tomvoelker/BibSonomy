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
package org.bibsonomy.search.es.management.post;

import java.net.URI;
import java.util.Date;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.management.database.SearchDBInterface;

/**
 * special class that manages community posts
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchCommunityManager<R extends Resource> extends ElasticsearchPostManager<R> {

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param entityInformationProvider
	 * @param inputLogic
	 */
	public ElasticsearchCommunityManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Post<R>> generator, EntityInformationProvider<Post<R>> entityInformationProvider, SearchDBInterface<R> inputLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, entityInformationProvider, inputLogic);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.post.ElasticsearchPostManager#updatePredictions(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	protected void updatePredictions(String indexName, Date lastPredictionChangeDate, Date currentLastPreditionChangeDate) {
		// noop no user related content
	}
}
