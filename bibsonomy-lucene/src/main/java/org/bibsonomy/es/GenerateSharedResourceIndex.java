package org.bibsonomy.es;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.generator.LuceneGenerateResourceIndex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.ESClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: add documentation to this class
 *
 * @author lka
 */
public class GenerateSharedResourceIndex extends LuceneGenerateResourceIndex<Resource>{
	
	// ElasticSearch client
	private final ESClient esClient = new ESNodeClient();

	/**
	 * frees allocated resources and closes all files
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	@Override
	public void shutdown() throws CorruptIndexException, IOException {

		log.info("closing node " + this.esClient.getNode());
		this.esClient.shutdown();

	}
	
	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
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

		log.info("Start writing data to lucene index (with duplicate detection)");

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

					int indexTry = 0;
					while (indexTry < 3) {
						if (indexTry == 0) {
							indexTry=3;
							Map<String, Object> jsonDocument = new HashMap<String, Object>();
							jsonDocument = (Map<String, Object>) this.resourceConverter.readPost(post, this.searchType);
								esClient.getClient()
										.prepareIndex("posts", "publication", post.getContentId().toString())
										.setSource(jsonDocument).execute().actionGet();
								log.info("post has been indexed.");
								
							
						}else if(indexTry == 2){
							indexTry=3;
							final ExpBibPost expPost = createTestPost();
							String postJson = null;
							try {
							    postJson = new ObjectMapper().writeValueAsString(expPost);
							    esClient.getClient().prepareIndex("posts", "publication")
								    .setSource(postJson).execute().actionGet();
							    log.info("publication has been indexed.");							  
							} catch (final JsonProcessingException jpe) {
							    log.error("publication could not be serialized. ", jpe);
							}
						}
					}

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

			// all done
			// log.info("(" + i + " indexed entries, " + is +
			// " not indexed spam entries)");
		} catch (final InterruptedException e) {
			log.error("lucene finished not in 18 hours", e);
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

			// generate index
			GenerateSharedResourceIndex.this.createIndexFromDatabase();

			this.isRunning = false;
		} catch (final Exception e) {
			log.error("Failed to generate index!", e);
		} finally {
			try {
				GenerateSharedResourceIndex.this.shutdown();
			} catch (final Exception e) {
				log.error("Failed to close node!", e);
			}
		}
	}
	
    private static ExpBibPost createTestPost() {
	final ExpBibPost expPost = new ExpBibPost();
	expPost.setTitle("blabla title");
	expPost.setDescription("blabla description");
	expPost.setAuthor("blabla author");
	return expPost;
    }

    /**
     * converts post into JSON document for ES to index
     * 
     * @param post
     * @return JSON document of the post
     */
    private static Map<String, Object> putJsonDocument(LucenePost<Resource> post) {

		Map<String, Object> jsonDocument = new HashMap<String, Object>();
		
		jsonDocument.put("changeDate", post.getChangeDate());
		jsonDocument.put("contentId", post.getContentId());

		return jsonDocument;
	}

}
