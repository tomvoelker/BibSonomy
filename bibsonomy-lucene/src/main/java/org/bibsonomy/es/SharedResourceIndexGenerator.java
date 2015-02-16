package org.bibsonomy.es;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.generator.AbstractIndexGenerator;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is responsible for generating the index for Shared Resources
 *
 * @param <R> the resource of the index to generate
 *
 * @author lutful
 * @author jil
 */
public class SharedResourceIndexGenerator<R extends Resource> extends AbstractIndexGenerator<R> {
	
	private final String indexName = ESConstants.INDEX_NAME;
	private String resourceType;
	private final String systemUrlFieldName = "systemUrl";

	/** converts post model objects to elasticsearch documents */
	protected LuceneResourceConverter<R> resourceConverter;

	// ElasticSearch client
	private ESClient esClient;
	private final String systemHome;
	private static final Log log = LogFactory.getLog(SharedResourceIndexGenerator.class);
	private final SharedResourceIndexUpdater<R> updater;
		
	/**
	 * @param systemHome
	 */
	public SharedResourceIndexGenerator(final String systemHome, SharedResourceIndexUpdater<R> updater) {
		this.systemHome = systemHome;
		this.updater = updater;
	}


	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Override
	public void createEmptyIndex() throws IOException {
		log.info("Start writing data to shared index");
		
		//Add mapping here depending on the resource type which is here indexType
		ESResourceMapping resourceMapping = new ESResourceMapping(resourceType, esClient);
		resourceMapping.doMapping();
	}
	
	@Override
	protected void writeMetaInfo(Integer lastTasId, Date lastLogDate) throws IOException {
		updater.setSystemInformation(lastTasId, lastLogDate);
		updater.flushSystemInformation();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#addPostToIndex(org.bibsonomy.lucene.param.LucenePost)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void addPostToIndex(LucenePost<R> post) {
		Map<String, Object> jsonDocument = new HashMap<String, Object>();
		jsonDocument = (Map<String, Object>) this.resourceConverter.readPost(post, IndexType.ELASTICSEARCH);
		jsonDocument.put(this.systemUrlFieldName, systemHome);
		long indexID = (systemHome.hashCode() << 32) + Long.parseLong(post.getContentId().toString());
		esClient.getClient()
				.prepareIndex(indexName, resourceType, String.valueOf(indexID))
				.setSource(jsonDocument).execute().actionGet();
		log.info("post has been indexed.");
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#getName()
	 */
	@Override
	protected String getName() {
		return getIndexName() + "-" + resourceType;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.AbstractIndexGenerator#activateIndex()
	 */
	@Override
	protected void activateIndex() {
		esClient.getClient().admin().indices().flush(new FlushRequest(ESConstants.INDEX_NAME).full(true)).actionGet();
	}

	/**
	 * @return returns the converter
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return this.resourceConverter;
	}

	/**
	 * @param resourceConverter
	 */
	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

}
