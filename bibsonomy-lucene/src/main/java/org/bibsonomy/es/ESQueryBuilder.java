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

import static org.bibsonomy.lucene.util.LuceneBase.CFG_LUCENE_FIELD_SPECIFIER;
import static org.bibsonomy.util.ValidationUtils.present;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.database.LuceneInfoLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.Tag;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.IndexQueryParserService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.support.QueryParsers;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * The Class for building queries for Shared Resource Search based on Elasticsearch.
 *
 * @author lutful
 * 
 */

public abstract class ESQueryBuilder {

    private Log log = LogFactory.getLog(ESQueryBuilder.class);

    /** The esclient. */
    private Client esClient;
    
	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private LuceneInfoLogic dbLogic;


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
	 * 
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
	 * 
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
	
	/**
	 * build the overall elasticsearch query term
	 * 
	 * @param userName
	 * @param requestedUserName
	 *            restrict the resulting posts to those which are owned by this
	 *            user name
	 * @param requestedGroupName
	 *            restrict the resulting posts to those which are owned this
	 *            group
	 * @param requestedRelationNames
	 * 				expand the search in the post of users which are defined by the given 
	 * 				relation names
	 * @param allowedGroups 
	 * @param searchTerms
	 * @param titleSearchTerms 
	 * @param authorSearchTerms 
	 * @param tagIndex 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param negatedTags
	 * @return overall elasticsearch query
	 */
	protected BoolQueryBuilder buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags) {

		BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();

		// --------------------------------------------------------------------
		// build the query
		// --------------------------------------------------------------------
		// the resulting main query
		if (present(searchTerms)) {
			QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			mainQueryBuilder.must(queryBuilder);
		}

		if (present(titleSearchTerms)) {
			//TODO
		}
		
		if (present(authorSearchTerms)) {
			//TODO
		}
		
		// Add the requested tags
		if (present(tagIndex) || present(negatedTags)) {
			addTagQuerries(tagIndex, negatedTags, mainQueryBuilder);
		}

		// restrict result to given group
		if (present(requestedGroupName)) {
			//TODO	
		}

		// restricting access to posts visible to the user

		// --------------------------------------------------------------------
		// post owned by user 
		// Use this restriction iff there is no user relation
		// --------------------------------------------------------------------
		if (present(requestedUserName) && !present(requestedRelationNames)) {
			//TODO
		}
		// If there is at once one relation then restrict the results only 
		// to the users in the given relations (inclduing posts of the logged in users)
		else if (present(requestedRelationNames)) {
			// for all relations: TODO

		}
		
		// all done
		log.debug("[Full text] Search query: " + mainQueryBuilder.toString());

		return mainQueryBuilder;
	}
	
	private void addTagQuerries(final Collection<String> tagIndex, final Collection<String> negatedTags, final BoolQueryBuilder mainQuery) {
		/*
		 * Process normal tags
		 */
		if (present(tagIndex)) {
			for (final String tag : tagIndex) {
				// Is the tag string a concept name?
				if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
					final String conceptTag = tag.substring(2);
					// get related tags:
					final BoolQueryBuilder conceptTags = new BoolQueryBuilder();
					QueryBuilder termQuery = termQuery(LuceneFieldNames.TAS, conceptTag);
					conceptTags.must(termQuery);
					for (final String t : this.dbLogic.getSubTagsForConceptTag(conceptTag)) {
						conceptTags.should(termQuery(LuceneFieldNames.TAS, t));
					}
					mainQuery.must(conceptTags);
				} else {
					mainQuery.must(termQuery(LuceneFieldNames.TAS, tag));
				}
			}
		}
		/*
		 * Process negated Tags
		 */
		
		if (present(negatedTags)) {
			for (final String negatedTag : negatedTags) {
				final QueryBuilder negatedSearchQuery = termQuery(LuceneFieldNames.TAS, negatedTag);
				mainQuery.mustNot(negatedSearchQuery);
			}
		}

	}


//	/**
//	 * @param fieldName
//	 * @param searchTerms
//	 * @return
//	 */
//	private QueryBuilder parseSearchQuery(String fieldName, String searchTerms) {
//		// parse search terms for handling phrase search
//		 IndexQueryParserService queryParser =new  ;
//		 QueryParser qp = QueryParsers.
//		 try {
//				// disallow field specification in search query
//				searchTerms = searchTerms.replace(CFG_LUCENE_FIELD_SPECIFIER, "\\" + CFG_LUCENE_FIELD_SPECIFIER);
//		 return  parsedQuery = queryParser.parse(queryBuilder)(queryStringQuery(searchTerms).defaultField(fieldName).phraseSlop(1)).query();
//;
//		} catch (final ParseException e) {
//			return termQuery(fieldName, searchTerms);
//		}
//	}
	
	
}
