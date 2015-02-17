package org.bibsonomy.es;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * This class is responsible for the mapping of the specified resource type for Shared Resource Indices
 *
 * @author lutful
 */
public class ESResourceMapping {
	
	private final String indexName = ESConstants.INDEX_NAME;
	private String indexType;
	// ElasticSearch client
	private ESClient esClient;
	
	
	/**
	 * @param indexType
	 * @param esClient
	 */
	public ESResourceMapping(String indexType, ESClient esClient) {
		this.indexType = indexType;
		this.esClient = esClient;
	}

	/**
	 * performs the mapping for the specified resource type
	 * 
	 * @throws IOException
	 */
	public void doMapping() throws IOException{
		final XContentBuilder mappingBuilder;
		
		if(indexType.equalsIgnoreCase("Bookmark")){
			mappingBuilder = createMappingForBookmark(this.indexType);
		}else {
			// mapping is same for both BibTex and GoldStandradPublication
			mappingBuilder = createMappingForPublication(this.indexType);
		}
		
		esClient.getClient().admin()
		        .indices()
		        .preparePutMapping(indexName)
		        .setType(indexType)
		        .setSource(mappingBuilder)
		        .execute()
		        .actionGet();
		
		// wait for the yellow (or green) status to prevent NoShardAvailableActionException later
		esClient.getClient().admin().cluster().prepareHealth()
		.setWaitForYellowStatus().execute().actionGet();
	}
	
	/**
	 * @param documentType 
	 * @return returns the mapping for Bookmark
	 * @throws IOException 
	 */
	private static XContentBuilder createMappingForBookmark(final String documentType) throws IOException {
		XContentBuilder mapping = jsonBuilder()
				.startObject()
			        .startObject(documentType)
			            .startObject("properties")
			                .startObject("intrahash")
			                	.field("type", "string")
			                    .field("index","not_analyzed")
			                .endObject()
			                .startObject("interhash")
    		                	.field("type", "string")
			                    .field("index","not_analyzed")
			                .endObject()			            
			            .endObject()
			        .endObject()
			    .endObject();
		
		return mapping;
	}

	/**
	 * @param documentType 
	 * @return returns the mapping for BibTex and GoldStandardPublication
	 * @throws IOException 
	 * 
	 */
	private static XContentBuilder createMappingForPublication(final String documentType) throws IOException {
		XContentBuilder mapping = jsonBuilder()
				.startObject()
			        .startObject(documentType)
			            .startObject("properties")
			                .startObject("address")
			                    .field("type", "string") 
			                    .field("index","no")
			                .endObject()
			                .startObject("annote")
			                    .field("type", "string") 
			                    .field("index","no")
			                .endObject()
			                .startObject("bKey")
			                    .field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("bibtexAbstract")
			                    .field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("bibtexKey")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("booktitle")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("chapter")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("crossref")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("day")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("edition")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("editor")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("entrytype")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("howPublished")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("institution")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("interhash")
			                	.field("type","string")
			                    .field("index","not_analyzed")
			                .endObject()
			                .startObject("intrahash")
			                	.field("type","string")
			                    .field("index","not_analyzed")
			                .endObject()
			                .startObject("journal")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("misc")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("month")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("note")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("number")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("organization")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("pages")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("privnote")
			                	.field("type","string")
			                    .field("index","not_analyzed")
			                    .field("store", "false")
			                .endObject()
			                .startObject("publisher")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("school")
		                		.field("type","string")
		                		.field("index","no")
			                .endObject()
			                .startObject("series")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("title")
			                	.field("type","string")
			                    .field("index","analyzed")
			                .endObject()
			                .startObject("type")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("url")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("volume")
			                	.field("type","string")
			                    .field("index","no")
			                .endObject()
			                .startObject("year")
			                	.field("type","string")
			                    .field("index","not_analyzed")
			                .endObject()
			            .endObject()
			        .endObject()
			    .endObject();
		
		return mapping;
		
	}
}
