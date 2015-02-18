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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * The Class for building queries for Shared Resource Search base on Elasticsearch.
 *
 * @author lutful
 * 
 */

public abstract class ESQueryBuilder {

    private Log log = LogFactory.getLog(ESQueryBuilder.class);

    /** The s esclient. */
    private Client esClient;


	/**
	 * Query builder.
	 *
	 * @param indexName the index name
	 * @param indexType the index type
	 * @param queryBuilder the query builder
	 * @param fields the fields
	 * @param skipValue the skip value
	 * @param limit the limit
	 * @param orderByList the order by list
	 * @return the search response
	 */
	public final SearchResponse queryBuilder(final String indexName, final String indexType, final FilteredQueryBuilder queryBuilder,
			final List<String> fields, final int skipValue, final int limit, final List<SortBuilder> orderByList) {
		SearchRequestBuilder query;
		log.info("Entertin into queryBuilder in generic search with parameters :" + " indexName:" + indexName
				+ " indexType:" + indexType + " fields:" + fields + " skipValue:" + skipValue + " limit:" + limit
				+ " orderByList:" + orderByList);

		try {
			if (indexName != null) {
				query = esClient.prepareSearch(indexName);
				if (indexType != null) {
					query = query.setTypes(indexType);
				}
			} 
			else {
				query = esClient.prepareSearch();
			}

			if (queryBuilder != null) {
				query.setQuery(queryBuilder);
			}
			if (fields != null) {
				final String[] fieldS = fields.toArray(new String[fields.size()]);
				query.addFields(fieldS);
			}
			if (orderByList != null && orderByList.size() > 0) {
				for (SortBuilder orderBy : orderByList) {
					query.addSort(orderBy);
				}
			}
			if (skipValue != 0) {
				query.setFrom(skipValue);
			}
			if (limit != 0) {
				query.setSize(limit);
			}
			else {
				query.setSize(ESConstants.BATCHSIZE);
			}
			if (log.isDebugEnabled()) {
				log.info("Query:" + query);
			}
		
			final SearchResponse searchResponse = query.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setScroll(new TimeValue(ESConstants.BATCHSIZE)).execute().actionGet();
			log.info("End of queryBuilder in generic search");
			return searchResponse;
		} 
		catch (Exception e) {
			log.error("Es_Error", e);
		}
		return null;

	}
	
	
	/**
	 * Fetchfields from es.
	 * @param builder FilteredQueryBuilder
	 * @param fields to be featched the fields
	 * @param from the from
	 * @param size the size
	 * @param sortBuildList the sort build list
	 * @param indexName the index name
	 * @param indexTypeName the index type name
	 * @return the listOfMap
	 */
	public final List<Map<String, Object>> fetchFieldsFromES(final FilteredQueryBuilder builder, final List<String> fields, final int from, final int size,
			final List<SortBuilder> sortBuildList,final String indexName,final String indexTypeName) {
		SearchResponse response = queryBuilder(indexName,indexTypeName, builder,
				fields, from, size, sortBuildList);
		final List<Map<String,Object>> listOfMap= new ArrayList<Map<String,Object>>();
		final Map<String,Object> keyValuePair= new HashMap<String,Object>();
		Object tempString;
		if (response != null) {
			while (true) {
				for (SearchHit hit : response.getHits()) {
					final Map<String, SearchHitField> fieldsMap = hit.getFields();
					if (fieldsMap != null) {
						for(String field:fields)
						{
							if (fieldsMap.get(field) != null) {
								tempString = fieldsMap.get(field).getValue();
								keyValuePair.clear();
								keyValuePair.put(field, tempString);
							}
						}
						listOfMap.add(keyValuePair);
					}
				}
				if (response.getHits().getHits().length == 0) {
					break;
				}
				if (log.isDebugEnabled()) {
					log.debug("If matched documents are more than 30000 then it will fetch docmument in batches size(30000): "
							+ builder);
				}
				response = esClient.prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(ESConstants.BATCHSIZE)).execute().actionGet();
			}

		}
		return listOfMap;
	}


	/**
	 * Fetch documents es.
	 * @param builder the FilteredQueryBuilder
	 * @param from the from
	 * @param size the size
	 * @param sortBuildList the sort build list
	 * @param indexName the index name
	 * @param indexTypeName the index type name
	 * @return the Array of documents
	 */
	public final List<String> fetchDocumentsES(final FilteredQueryBuilder builder, final int from, final int size,
			final List<SortBuilder> sortBuildList,final String indexName,final String indexTypeName) {
		SearchResponse response = queryBuilder(indexName,indexTypeName, builder,
				null, from, size, sortBuildList);
		List<String> listOfDocuments= new ArrayList<String>();
		if (response != null) {
			while (true) {
				for (SearchHit hit : response.getHits()) {
					listOfDocuments.add(hit.sourceAsString());					
				}
				if (response.getHits().getHits().length == 0) {
					break;
				}
				if (log.isDebugEnabled()) {
					log.debug("If matched documents are more than 30000 then it will fetch docmument in batches size(30000): "
							+ builder);
				}
				response = esClient.prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(ESConstants.BATCHSIZE)).execute().actionGet();
			}

		}
		return listOfDocuments;
	}
}
