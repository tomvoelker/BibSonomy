/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.es;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.Order;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * This class performs a search in the Shared Resource Indices based on the
 * search term
 * 
 * @author lutful
 * @param <R>
 */
public class EsResourceSearch<R extends Resource> {

	private String resourceType;

	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;

	/**
	 * 
	 */
	protected static final Log log = LogFactory.getLog(EsResourceSearch.class);

	private ESClient esClient;

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(final ESClient esClient) {
		this.esClient = esClient;
	}

	/**
	 * @return the ElasticSearch Client
	 */
	public ESClient getEsClient() {
		return this.esClient;
	}

	/**
	 * @param searchTerms
	 * @param order
	 * @param offset
	 * @param limit
	 * @return postList
	 * @throws IOException
	 * @throws CorruptIndexException
	 * 
	 */
	public ResultList<Post<R>> fullTextSearch(final String searchTerms, final Order order, final int limit, final int offset) throws CorruptIndexException, IOException {

		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			final QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			final SearchRequestBuilder searchRequestBuilder = this.esClient.getClient().prepareSearch(ESConstants.INDEX_NAME);
			searchRequestBuilder.setTypes(this.resourceType);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
			if (order != Order.RANK) {
				searchRequestBuilder.addSort(LuceneFieldNames.DATE, SortOrder.DESC);
			}
			searchRequestBuilder.setFrom(offset).setSize(limit).setExplain(true);

			final SearchResponse response = searchRequestBuilder.execute().actionGet();

			if (response != null) {
				final SearchHits hits = response.getHits();
				postList.setTotalCount((int) hits.getTotalHits());

				log.info("Current Search results for '" + searchTerms + "': " + response.getHits().getTotalHits());
				for (final SearchHit hit : hits) {
					postList.add(this.resourceConverter.writePost(hit.getSource()));
				}
			}
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}

		return postList;
	}

	/**
	 * @return the resourceConverter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return this.resourceConverter;
	}

	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @return the INDEX_TYPE
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType
	 */
	public void setResourceType(final String resourceType) {
		this.resourceType = resourceType;
	}

}
