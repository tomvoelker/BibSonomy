package org.bibsonomy.es;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.generator.LuceneGenerateResourceIndex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.ESClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is responsible for generating the index for Shared Resources
 *
 * @author lutful
 */
public class SharedResourceIndexGenerator extends LuceneGenerateResourceIndex<Resource>{
	
	private final String indexName = ESConstants.INDEX_NAME;
	private String indexType;
	private final String systemUrlFieldName = "systemUrl";

	// ElasticSearch client
	private ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(SharedResourceIndexGenerator.class);
		
	/**
	 * @param systemHome
	 */
	public SharedResourceIndexGenerator(final String systemHome) {
		this.systemHome = systemHome;
	}

	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "boxing" })
	@Override
	public void createIndexFromDatabase() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");

		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);

		// number of post entries to calculate progress
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate = this.dbLogic.getLastLogDate();

		if (lastLogDate == null) {
			lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}

		log.info("Start writing data to shared index");
        
		//Add mapping here depending on the resource type which is here indexType
		ESResourceMapping resourceMapping = new ESResourceMapping(indexType, esClient);
		resourceMapping.doMapping();
		
		//Indexing system information for the specific index type
		SystemInformation systemInfo =  new SystemInformation();
		systemInfo.setPostType(indexType);
		systemInfo.setLast_log_date(lastLogDate);
		systemInfo.setLast_tas_id(lastTasId);
		systemInfo.setSystemUrl(systemHome);
		ObjectMapper mapper = new ObjectMapper();
		String jsonDocumentForSystemInfo = mapper.writeValueAsString(systemInfo);
		esClient.getClient().prepareIndex(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, systemHome+indexType)
							.setSource(jsonDocumentForSystemInfo).setRefresh(true).execute().actionGet();
		
		// read block wise all posts
		List<LucenePost<Resource>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;

		do {
			postList = this.dbLogic.getPostEntries(lastContenId, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<Resource> post : postList) {
				post.setLastLogDate(lastLogDate);
				post.setLastTasId(lastTasId);
				if (this.isNotSpammer(post)) {
					Map<String, Object> jsonDocument = new HashMap<String, Object>();
					jsonDocument = (Map<String, Object>) this.resourceConverter.readPost(post, this.searchType);
					jsonDocument.put(this.systemUrlFieldName, systemHome);
					long indexID = (systemHome.hashCode() << 32) + Long.parseLong(post.getContentId().toString());
					esClient.getClient()
							.prepareIndex(indexName, indexType, String.valueOf(indexID))
							.setSource(jsonDocument).execute().actionGet();
					log.info("post has been indexed.");
					
					this.importedPost(post);
				}
			}

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId();
			}
			
		} while (postListSize == SQL_BLOCKSIZE);

		try {
			executor.shutdown();
			executor.awaitTermination(18, TimeUnit.HOURS);

		} catch (final InterruptedException e) {
			log.error("Generating shared index did not finish in 18 hours", e);
		}
	}
	
	

	/** Run the index-generation in a thread. */
	@Override
	public void run() {
		try {
			// Allow only one index-generation at a time.
			if (this.isRunning) {
				return;
			}

			this.isRunning = true;

			log.warn("Generating index for "+ this.indexType+"...");
			// generate index
			SharedResourceIndexGenerator.this.createIndexFromDatabase();

			this.isRunning = false;
		} catch (final Exception e) {
			log.error("Failed to generate index!", e);
		}finally{
			try {
				this.shutdown();
			} catch (final Exception e) {
				log.error("Failed to close index-writer!", e);
			}
			log.warn("Finished generating index for "+ this.indexType);
		}
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}
	
	/**
	 * @return the indexType
	 */
	public String getIndexType() {
		return this.indexType;
	}

	/**
	 * @param indexType the indexType to set
	 */
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	/**
	 * @return the esClient
	 */
	public ESClient getEsClient() {
		return this.esClient;
	}

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(ESClient esClient) {
		this.esClient = esClient;
	}
}
