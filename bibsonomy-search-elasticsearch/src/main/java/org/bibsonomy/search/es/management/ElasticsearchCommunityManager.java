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
package org.bibsonomy.search.es.management;

import java.util.Date;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.management.database.SearchDBInterface;

/**
 * special class that manages community posts
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchCommunityManager<R extends Resource> extends ElasticsearchManager<R> {
	
	/**
	 * @param updateEnabled
	 * @param disabledIndexing
	 * @param client
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticsearchCommunityManager(boolean updateEnabled, boolean disabledIndexing, ESClient client, SearchDBInterface<R> inputLogic, ElasticsearchIndexTools<R> tools) {
		super(updateEnabled, disabledIndexing, client, inputLogic, tools);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.ElasticsearchManager#updatePredictions(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	protected void updatePredictions(String indexName, Date lastPredictionChangeDate, Date currentLastPreditionChangeDate) {
		// noop no user related content
	}
}
