package org.bibsonomy.es;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
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
 * TODO: add documentation to this class
 *
 * @author lka
 * @param <R> 
 */
public class EsResourceSearch<R extends Resource>{

	private static final String INDEX_NAME = "posts";
	private static final String TYPE_NAME = "publication";
	
	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;
	
	/**
	 * 
	 */
	protected static final Log log = LogFactory
			.getLog(EsResourceSearch.class);

	// ElasticSearch client
	private final ESClient esClient = new ESNodeClient();

	private String searchTerms;
	/**
	 * @return the ElasticSearch Client
	 */
	public ESClient getEsClient() {
		return this.esClient;
	}

	/**
	 * @return the searchTerms
	 */
	public String getSearchTerms() {
		return this.searchTerms;
	}

	/**
	 * @param searchTerms the searchTerms to set
	 */
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
	/**
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void shutdown() throws CorruptIndexException, IOException {

		log.info("closing node " + this.esClient.getNode());
		this.esClient.shutdown();

	}

	
	
	/**
	 * @return 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * 
	 */
	public ResultList<Post<R>> fullTextSearch() throws CorruptIndexException, IOException {

		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			QueryBuilder queryBuilder = QueryBuilders.queryString("*"
					+ this.searchTerms + "*");
			SearchRequestBuilder searchRequestBuilder = esClient.getClient().prepareSearch(INDEX_NAME);
			searchRequestBuilder.setTypes(TYPE_NAME);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
			searchRequestBuilder.setFrom(0).setSize(60).setExplain(true);

			SearchResponse response = searchRequestBuilder.execute()
					.actionGet();

			if (response != null) {
				log.info("Current Search results for wildcard '" + this.searchTerms + "': "
						+ response.getHits().getTotalHits());
				for (SearchHit hit : response.getHits()) {

						Map<String, Object> result = hit.getSource();					
						log.warn(result);
						final Post<R> post = this.resourceConverter.writePost(result);
						postList.add(post);
					}
				}
			
		} catch (IndexMissingException ex) {
			log.error("IndexMissingException: " + ex.toString());
		}finally{
			this.shutdown();		
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

	
	
}
