package org.bibsonomy.es;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.es.ESClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
/**
 * This class performs a search in the Shared Resource Indices based on the search term 
 *
 * @author lutful
 * @param <R> 
 */
public class EsResourceSearch<R extends Resource>{

	private final String indexName = ESConstants.INDEX_NAME;

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
	public void setEsClient(ESClient esClient) {
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
	 * @return postList
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * 
	 */
	public ResultList<Post<R>> fullTextSearch(String searchTerms) throws CorruptIndexException, IOException {

		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			SearchRequestBuilder searchRequestBuilder = esClient.getClient().prepareSearch(indexName);
			searchRequestBuilder.setTypes(resourceType);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
			searchRequestBuilder.setFrom(0).setSize(60).setExplain(true);

			SearchResponse response = searchRequestBuilder.execute().actionGet();

			if (response != null) {
				log.info("Current Search results for '" + searchTerms + "': "
						+ response.getHits().getTotalHits());
				for (SearchHit hit : response.getHits()) {
						Map<String, Object> result = hit.getSource();
						final Post<R> post = this.resourceConverter.writePost(result);
						postList.add(post);
					}
				}
			
		} catch (IndexMissingException e) {
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
	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @return the INDEX_TYPE
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param INDEX_TYPE the INDEX_TYPE to set
	 */
	public void setResourceType(String INDEX_TYPE) {
		this.resourceType = INDEX_TYPE;
	}

	/**
	 * @return the iNDEX_NAME
	 */
	public String getIndexName() {
		return this.indexName;
	}

	
}
