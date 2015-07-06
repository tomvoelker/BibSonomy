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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.BooleanQuery;
import org.bibsonomy.lucene.database.LuceneInfoLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
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
public class EsResourceSearch<R extends Resource> extends ESQueryBuilder{

	private String resourceType;

	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;

	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private LuceneInfoLogic dbLogic;
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
	 * get tag cloud for given search query for the Shared Resource System
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexkey 
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param limit
	 * @param offset
	 * @return returns the list of tags for the tag cloud
	 */
	public List<Tag> getTags(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexkey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, final int limit, final int offset) {
		final BoolQueryBuilder query= this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, bibtexkey, tagIndex, year, firstYear, lastYear, negatedTags);
		final Map<Tag, Integer> tagCounter = new HashMap<Tag, Integer>();
		boolean lockAcquired = false;
		try {  
			lockAcquired = this.esClient.getReadLock(this.resourceType).tryLock();
			if (lockAcquired) {
				SearchRequestBuilder searchRequestBuilder = esClient
						.getClient().prepareSearch(
								ESConstants.getGlobalAliasForResource(resourceType, true));
				searchRequestBuilder.setTypes(resourceType);
				searchRequestBuilder.setSearchType(SearchType.DEFAULT);
				searchRequestBuilder.setQuery(query);
				searchRequestBuilder.addSort(LuceneFieldNames.DATE,
						SortOrder.DESC);
				searchRequestBuilder.setFrom(offset).setSize(limit)
						.setExplain(true);
				final SearchResponse response = searchRequestBuilder.execute()
						.actionGet();
				if (response != null) {
					SearchHits hits = response.getHits();
					log.info("Current Search results for '" + searchTerms
							+ "': " + response.getHits().getTotalHits());
					for (int i = 0; i < Math.min(limit, hits.getTotalHits()
							- offset); ++i) {
						SearchHit hit = hits.getAt(i);
						Map<String, Object> result = hit.getSource();
						final Post<R> post = this.resourceConverter
								.writePost(result);
						// set tag count
						if (present(post.getTags())) {
							for (final Tag tag : post.getTags()) {
								/*
								 * we remove the requested tags because we assume
								 * that related tags are requested
								 */
								if (present(tagIndex)
										&& tagIndex.contains(tag.getName())) {
									continue;
								}
								Integer oldCnt = tagCounter.get(tag);
								if (!present(oldCnt)) {
									oldCnt = 1;
								} else {
									oldCnt += 1;
								}
								tagCounter.put(tag, oldCnt);
							}
						}
					}
				}
			}
		} catch (IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		}finally{
			if(lockAcquired){
				this.esClient.getReadLock(this.resourceType).unlock();
			}
		}
		
		
		final List<Tag> tags = new LinkedList<Tag>();
		// extract all tags
		for (final Map.Entry<Tag, Integer> entry : tagCounter.entrySet()) {
			final Tag tag = entry.getKey();
			tag.setUsercount(entry.getValue());
			tag.setGlobalcount(entry.getValue()); // FIXME: we set user==global count
			tags.add(tag);
		}
		log.debug("Done calculating tag statistics");
		
		// all done.
		return tags;
	}
	

	/**
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param requestedRelationNames
	 * @param allowedGroups
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param bibtexKey 
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param negatedTags
	 * @param order
	 * @param limit
	 * @param offset
	 * @return returns the list of posts
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset) throws CorruptIndexException, IOException {
		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		boolean lockAcquired = false;
		try {  
			lockAcquired = this.esClient.getReadLock(this.resourceType).tryLock(15, TimeUnit.SECONDS);  
			if (lockAcquired) {
				final BoolQueryBuilder query = this.buildQuery(userName,
						requestedUserName, requestedGroupName,
						requestedRelationNames, allowedGroups, searchTerms,
						titleSearchTerms, authorSearchTerms, bibtexKey,
						tagIndex, year, firstYear, lastYear, negatedTags);
				final SearchRequestBuilder searchRequestBuilder = this.esClient
						.getClient().prepareSearch(
								ESConstants.getGlobalAliasForResource(resourceType, true));
				searchRequestBuilder.setTypes(this.resourceType);
				searchRequestBuilder.setSearchType(SearchType.DEFAULT);
				searchRequestBuilder.setQuery(query);
				if (order != Order.RANK) {
					searchRequestBuilder.addSort(LuceneFieldNames.DATE,
							SortOrder.DESC);
				}
				searchRequestBuilder.setFrom(offset).setSize(limit)
						.setExplain(true);
				final SearchResponse response = searchRequestBuilder.execute()
						.actionGet();
				if (response != null) {
					final SearchHits hits = response.getHits();
					postList.setTotalCount((int) hits.getTotalHits());

					log.info("Current Search results for '" + searchTerms
							+ "': " + response.getHits().getTotalHits());
					for (final SearchHit hit : hits) {
						postList.add(this.resourceConverter.writePost(hit
								.getSource()));
					}
				}
			}
		} catch (final IndexMissingException e) {
			log.error("IndexMissingException: " + e);
		} catch (InterruptedException e) {
			log.error("unable to get the read lock on index: "+ this.resourceType, e);
		}finally{
			if(lockAcquired){
				this.esClient.getReadLock(this.resourceType).unlock();
			}
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
	 * @return the resourceType
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(final String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the dbLogic
	 */
	public LuceneInfoLogic getDbLogic() {
		return this.dbLogic;
	}

	/**
	 * @param dbLogic the dbLogic to set
	 */
	public void setDbLogic(LuceneInfoLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

}
