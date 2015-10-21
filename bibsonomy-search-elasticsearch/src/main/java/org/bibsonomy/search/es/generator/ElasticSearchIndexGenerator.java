package org.bibsonomy.search.es.generator;

import java.util.Collections;
import java.util.Date;
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
import org.bibsonomy.search.es.management.ElasticSearchIndex;
import org.bibsonomy.search.es.management.ElasticSearchIndexTools;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.util.Mapping;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticSearchIndexGenerator<R extends Resource> {
	private static final Log log = LogFactory.getLog(ElasticSearchIndexGenerator.class);
	
	
	private final ElasticSearchIndex<R> index;
	
	private final SearchDBInterface<R> inputLogic;
	
	private final ESClient client;
	
	private final ElasticSearchIndexTools<R> tools;


	private int writtenPosts = 0;
	private int numberOfPosts;
	
	
	/**
	 * @param index
	 * @param inputLogic
	 * @param client
	 * @param tools
	 */
	public ElasticSearchIndexGenerator(ElasticSearchIndex<R> index, SearchDBInterface<R> inputLogic, ESClient client, ElasticSearchIndexTools<R> tools) {
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
	 * 
	 */
	private void fillIndexWithPosts() {
		log.info("Filling index with database post entries.");

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.inputLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final SearchIndexState newState = this.inputLogic.getDbState();

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
					this.addPostToIndex(post);
					this.writtenPosts++;
				}
			}

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId().intValue();
			}
		} while (postListSize == SearchDBInterface.SQL_BLOCKSIZE);
		
		this.writeMetaInfo(newState);
	}

	/**
	 * @param newState
	 */
	private void writeMetaInfo(SearchIndexState newState) {
		final String indexName = this.index.getIndexName();
		final Map<String, Object> values = ElasticSearchUtils.serializeSearchIndexState(newState);
		
		final boolean inserted = this.client.insertNewDocument(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, ESConstants.SYSTEM_INFO_INDEX_TYPE, values);
		if (!inserted) {
			throw new RuntimeException("failed to save systeminformation for index " + indexName);
		}
		
		log.info("updated systeminformation of index " + indexName + " to " + values);
	}

	/**
	 * @param post
	 */
	private void addPostToIndex(SearchPost<R> post) {
		this.client.waitForReadyState();
		final Map<String, Object> convertedPost = this.tools.getConverter().convert(post);
		
		final String indexId = ElasticSearchUtils.createElasticSearchId(post.getContentId().intValue());
		this.insertPostDocument(convertedPost, indexId);
	}
	
	private void insertPostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.client.insertNewDocument(index.getIndexName(), this.tools.getResourceTypeAsString(), indexIdStr, jsonDocument);
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
		log.info("index not existing - generating a new one with mapping");
		
		final boolean created = this.client.createIndex(indexName, Collections.singleton(mapping));
		if (!created) {
			throw new RuntimeException("can not create index '" + indexName + "'"); // TODO: use specific exception
		}
		
		// FIXME: use system url TODODZO
		this.client.createAlias(indexName, ElasticSearchUtils.getTempAliasForResource(this.tools.getResourceType()));
	}
	
	/**
	 * 
	 */
	private void indexCreated() {
		// FIXME: use system url TODODZO
		this.client.deleteAlias(this.index.getIndexName(), ElasticSearchUtils.getTempAliasForResource(this.tools.getResourceType()));
	}

}
