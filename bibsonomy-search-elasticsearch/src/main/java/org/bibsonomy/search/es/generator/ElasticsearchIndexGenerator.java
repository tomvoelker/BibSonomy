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
package org.bibsonomy.search.es.generator;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.ElasticsearchIndex;
import org.bibsonomy.search.es.management.ElasticsearchIndexTools;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.util.BasicUtils;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchIndexGenerator<R extends Resource> {
	private static final Log log = LogFactory.getLog(ElasticsearchIndexGenerator.class);
	
	
	private final ElasticsearchIndex<R> index;
	
	private final SearchDBInterface<R> inputLogic;
	
	private final ESClient client;
	
	private final ElasticsearchIndexTools<R> tools;

	private int writtenPosts = 0;
	private int numberOfPosts;
	
	/**
	 * @param index
	 * @param inputLogic
	 * @param client
	 * @param tools
	 */
	public ElasticsearchIndexGenerator(ElasticsearchIndex<R> index, SearchDBInterface<R> inputLogic, ESClient client, ElasticsearchIndexTools<R> tools) {
		super();
		this.index = index;
		this.inputLogic = inputLogic;
		this.client = client;
		this.tools = tools;
	}
	
	/**
	 * method that generates a new ElasticSearch index
	 */
	public void generateIndex() {
		this.createNewIndex();
		this.fillIndexWithPosts();
		this.indexCreated();
	}
	
	/**
	 * inserts the posts form the database into the index
	 */
	private void fillIndexWithPosts() {
		log.info("Filling index with database post entries.");

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.inputLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final SearchIndexSyncState newState = this.inputLogic.getDbState();
		newState.setMappingVersion(BasicUtils.VERSION);
		if (newState.getLast_log_date() == null) {
			newState.setLast_log_date(new Date(System.currentTimeMillis() - 1000));
		}
		
		log.info("Start writing posts to index");
		
		// read block wise all posts
		List<SearchPost<R>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;
		do {
			postList = this.inputLogic.getPostEntries(lastContenId, SearchDBInterface.SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");
			final Map<String, Map<String, Object>> docsToWrite = new HashMap<>();
			// cycle through all posts of currently read block
			for (final SearchPost<R> post : postList) {
				post.setLastLogDate(newState.getLast_log_date());
				if (post.getLastTasId() == null) {
					post.setLastTasId(newState.getLast_tas_id());
				} else {
					if (post.getLastTasId().intValue() < post.getLastTasId().intValue()) {
						post.setLastTasId(post.getLastTasId());
					}
				}
				
				if (isNotSpammer(post)) {
					final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
					docsToWrite.put(ElasticsearchUtils.createElasticSearchId(post.getContentId().intValue()), convertedPost);
				}
				
				if (docsToWrite.size() > ESConstants.BULK_INSERT_SIZE) {
					this.clearQueue(docsToWrite);
				}
			}
			
			this.clearQueue(docsToWrite);

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId().intValue();
			}
		} while (postListSize == SearchDBInterface.SQL_BLOCKSIZE);
		
		this.writeMetaInfo(newState);
	}

	/**
	 * @param docsToWrite
	 */
	private void clearQueue(final Map<String, Map<String, Object>> docsToWrite) {
		if (present(docsToWrite)) {
			this.client.insertNewDocuments(this.index.getIndexName(), this.tools.getResourceTypeAsString(), docsToWrite);
			this.writtenPosts += docsToWrite.size();
			docsToWrite.clear();
		}
	}

	/**
	 * @param newState
	 */
	private void writeMetaInfo(SearchIndexSyncState newState) {
		final String indexName = this.index.getIndexName();
		final Map<String, Object> values = ElasticsearchUtils.serializeSearchIndexState(newState);
		
		final boolean inserted = this.client.insertNewDocument(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, ESConstants.SYSTEM_INFO_INDEX_TYPE, values);
		if (!inserted) {
			throw new RuntimeException("failed to save systeminformation for index " + indexName);
		}
		
		log.info("updated systeminformation of index " + indexName + " to " + values);
	}

	/**
	 * @param post
	 * @return
	 */
	private static boolean isNotSpammer(final Post<? extends Resource> post) {
		for (final Group group : post.getGroups()) {
			if (group.getGroupId() < 0) {
				// spammer group found => user is spammer
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	private void createNewIndex() {
		this.client.waitForReadyState();
		final String indexName = this.index.getIndexName();
		
		// check if the index already exists if not, it creates empty index
		final boolean indexExists = this.client.existsIndexWithName(indexName);
		if (indexExists) {
			throw new IllegalStateException("index '" + indexName + "' already exists while generating an index");
		}
		
		
		final Mapping<String> mapping = this.tools.getMappingBuilder().getMapping();
		log.info("index not existing - generating a new one ('" + indexName + "')");
		
		final boolean created = this.client.createIndex(indexName, Collections.singleton(mapping), ESConstants.SETTINGS);
		if (!created) {
			throw new RuntimeException("can not create index '" + indexName + "'"); // TODO: use specific exception
		}
		
		this.client.createAlias(indexName, ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.tools.getSystemURI(), SearchIndexState.GENERATING));
	}
	
	/**
	 * 
	 */
	private void indexCreated() {
		this.client.deleteAlias(this.index.getIndexName(), ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.tools.getSystemURI(), SearchIndexState.GENERATING));
		this.client.createAlias(this.index.getIndexName(), ElasticsearchUtils.getLocalAliasForResource(this.tools.getResourceType(), this.tools.getSystemURI(), SearchIndexState.STANDBY));
	}

	/**
	 * @return the index
	 */
	public ElasticsearchIndex<R> getIndex() {
		return this.index;
	}

	/**
	 * @return the progress
	 */
	public double getProgress() {
		if (this.numberOfPosts == 0) {
			return 0;
		}
		return this.writtenPosts / (double) this.numberOfPosts;
	}

}
