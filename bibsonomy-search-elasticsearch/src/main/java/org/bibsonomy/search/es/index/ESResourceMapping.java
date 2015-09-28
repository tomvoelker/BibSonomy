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
package org.bibsonomy.search.es.index;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * This class is responsible for the mapping of the specified resource type for
 * Shared Resource Indices
 * Maps JSON fields (of posts) to index fields and defines field types
 * 
 * @author lutful
 */
public class ESResourceMapping {
	
	@Deprecated // TODO: remove this dependency!
	private final Class<? extends Resource> resourceType;
	@Deprecated // TODO: remove
	private final ESClient esClient;
	private final String indexName;

	/**
	 * @param resourceType
	 * @param esClient
	 * @param indexName 
	 */
	public ESResourceMapping(final Class<? extends Resource> resourceType, final ESClient esClient, String indexName) {
		this.resourceType = resourceType;
		this.esClient = esClient;
		this.indexName =  indexName;
	}

	/**
	 * performs the mapping for the specified resource type
	 * 
	 * @throws IOException
	 */
	public void doMapping() throws IOException {
		final XContentBuilder mappingBuilder;
		final String objectType = ResourceFactory.getResourceName(this.resourceType);
		// TODO: a more generic version
		if (Bookmark.class == this.resourceType) {
			/*
			 * FIXME: What about GoldStandardBookmarks?
			 */
			mappingBuilder = createMappingForBookmark(objectType);
		} else {
			// mapping is identical for both BibTex and GoldStandardPublication
			mappingBuilder = createMappingForPublication(objectType);
		}

		this.esClient.getClient().admin().indices().preparePutMapping(indexName).setType(objectType).setSource(mappingBuilder).execute().actionGet();

		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.esClient.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
	}

	/**
	 * @param documentType
	 * @return returns the mapping for Bookmark
	 * @throws IOException
	 */
	private static XContentBuilder createMappingForBookmark(final String documentType) throws IOException {
		return jsonBuilder().startObject().startObject(documentType).startObject("properties").startObject("intrahash").field("type", "string").field("index", "not_analyzed").endObject().startObject("interhash").field("type", "string").field("index", "not_analyzed").endObject().endObject().endObject().endObject();
	}

	/**
	 * @param documentType
	 * @return returns the mapping for BibTex and GoldStandardPublication
	 * @throws IOException
	 * 
	 */
	private static XContentBuilder createMappingForPublication(final String documentType) throws IOException {
		return jsonBuilder().startObject().startObject(documentType).startObject("properties") //
				.startObject("address").field("type", "string").field("index", "no").endObject() //
				.startObject("annote").field("type", "string").field("index", "no").endObject() //
				.startObject("bKey").field("type", "string").field("index", "no").endObject() //
				.startObject("bibtexAbstract").field("type", "string").field("index", "no").endObject() //
				.startObject("bibtexKey").field("type", "string").field("index", "no").endObject() //
				.startObject("booktitle").field("type", "string").field("index", "no").endObject() //
				.startObject("chapter").field("type", "string").field("index", "no").endObject() //
				.startObject("crossref").field("type", "string").field("index", "no").endObject() //
				.startObject("day").field("type", "string").field("index", "no").endObject() //
				.startObject("edition").field("type", "string").field("index", "no").endObject() //
				.startObject("editor").field("type", "string").field("index", "no").endObject() //
				.startObject("entrytype").field("type", "string").field("index", "no").endObject() //
				.startObject("howPublished").field("type", "string").field("index", "no").endObject() //
				.startObject("institution").field("type", "string").field("index", "no").endObject() //
				.startObject("interhash").field("type", "string").field("index", "not_analyzed").endObject() //
				.startObject("intrahash").field("type", "string").field("index", "not_analyzed").endObject() //
				.startObject("journal").field("type", "string").field("index", "no").endObject() //
				.startObject("misc").field("type", "string").field("index", "no").endObject() //
				.startObject("month").field("type", "string").field("index", "no").endObject() //
				.startObject("note").field("type", "string").field("index", "no").endObject() //
				.startObject("number").field("type", "string").field("index", "no").endObject() //
				.startObject("organization").field("type", "string").field("index", "no").endObject() //
				.startObject("pages").field("type", "string").field("index", "no").endObject() //
				.startObject("privnote").field("type", "string").field("index", "not_analyzed").field("store", "false").endObject() //
				.startObject("publisher").field("type", "string").field("index", "no").endObject() //
				.startObject("school").field("type", "string").field("index", "analyzed").endObject() //
				.startObject("series").field("type", "string").field("index", "no").endObject() //
				.startObject("title").field("type", "string").field("index", "analyzed").endObject() //
				.startObject("type").field("type", "string").field("index", "no").endObject() //
				.startObject("url").field("type", "string").field("index", "no").endObject() //
				.startObject("volume").field("type", "string").field("index", "no").endObject() //
				.startObject("year").field("type", "string").field("index", "not_analyzed").endObject() //
				.startObject(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME).field("type", "string").field("index", "not_analyzed").endObject() //
				.startObject(Fields.SYSTEM_URL).field("type", "string").field("index", "not_analyzed").endObject() //
				.startObject(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME).field("type", "string").field("index", "analyzed").endObject() //
				.startObject(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME).field("type", "string").field("index", "analyzed").endObject() //
				.startObject(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME).field("type", "string").field("index", "analyzed").endObject() //
				.startObject(ESConstants.PERSON_ENTITY_IDS_FIELD_NAME).field("type", "string").field("index", "analyzed").endObject() //
				.endObject().endObject().endObject();
	}
	
	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}

}
