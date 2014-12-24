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

/**
 * TODO: add documentation to this class
 *
 * @author lka
 */
public class SharedResourceIndexGenerator extends LuceneGenerateResourceIndex<Resource>{
	
	private final String INDEX_NAME = ESConstants.INDEX_NAME;
	private String INDEX_TYPE;
	private final String systemUrlFieldName = "systemUrl";
	//	private final ESClient esClient = new ESNodeClient();
	// ElasticSearch Transport client
	private static ESClient esClient;
	private static String systemtHome;
	private static final Log log = LogFactory.getLog(SharedResourceIndexGenerator.class);
		
	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "boxing" })
	@Override
	public void createIndexFromDatabase() throws CorruptIndexException,
			IOException {
		log.info("Filling index with database post entries.");

		final ExecutorService executor = Executors
				.newFixedThreadPool(this.numberOfThreads);

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
					jsonDocument.put(this.systemUrlFieldName, systemtHome);
					long indexID = (systemtHome.hashCode() << 32) + Long.parseLong(post.getContentId().toString());
					esClient.getClient()
							.prepareIndex(INDEX_NAME, INDEX_TYPE, String.valueOf(indexID))
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

			log.warn("Generating index for "+ this.INDEX_TYPE+"...");
			// generate index
			SharedResourceIndexGenerator.this.createIndexFromDatabase();

			this.isRunning = false;
		} catch (final Exception e) {
			log.error("Failed to generate index!", e);
		}finally{
			log.warn("Finished generating index for "+ this.INDEX_TYPE);
		}
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.INDEX_NAME;
	}
	
	/**
	 * @return the INDEX_TYPE
	 */
	public String getINDEX_TYPE() {
		return this.INDEX_TYPE;
	}

	/**
	 * @param INDEX_TYPE the INDEX_TYPE to set
	 */
	public void setINDEX_TYPE(String INDEX_TYPE) {
		this.INDEX_TYPE = INDEX_TYPE;
	}

	/**
	 * @return the esClient
	 */
	public ESClient getEsClient() {
		return SharedResourceIndexGenerator.esClient;
	}

	/**
	 * @param esClient the esClient to set
	 */
	public void setEsClient(ESClient esClient) {
		SharedResourceIndexGenerator.esClient = esClient;
	}

	/**
	 * @return the systemtHome
	 */
	public String getSystemtHome() {
		return systemtHome;
	}

	/**
	 * @param systemtHome the systemtHome to set
	 */
	public void setSystemtHome(String systemtHome) {
		SharedResourceIndexGenerator.systemtHome = systemtHome;
	}

}
