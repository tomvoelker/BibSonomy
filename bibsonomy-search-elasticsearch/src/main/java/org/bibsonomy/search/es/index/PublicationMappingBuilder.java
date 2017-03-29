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
package org.bibsonomy.search.es.index;

import java.io.IOException;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * mapping builder for indexed {@link BibTex}
 *
 * @author dzo
 */
public class PublicationMappingBuilder extends ResourceMappingBuilder<BibTex> {
	private static final String COPY_TO = "copy_to";

	/**
	 * @param resourceType
	 */
	public PublicationMappingBuilder(Class<BibTex> resourceType) {
		super(resourceType);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceMapping#doResourceSpecificMapping(org.elasticsearch.common.xcontent.XContentBuilder)
	 */
	@Override
	protected void doResourceSpecificMapping(XContentBuilder builder) throws IOException {
		builder
			.startObject(Fields.Publication.ADDRESS)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.ANNOTE)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.AUTHORS)
				.field(TYPE_FIELD, NESTED_TYPE)
				.startObject(PROPERTIES)
					.startObject(Fields.Publication.PERSON_NAME)
						.field(TYPE_FIELD, STRING_TYPE)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.DOCUMENTS)
				.field(TYPE_FIELD, NESTED_TYPE)
				.startObject(PROPERTIES)
					.startObject(Fields.Publication.Document.NAME)
						.field(TYPE_FIELD, STRING_TYPE)
						.field(INDEX_FIELD, NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.TEXT)
						.field(TYPE_FIELD, STRING_TYPE)
						.field(INCLUDE_IN_ALL_FIELD, false)
						.array(COPY_TO, Fields.PRIVATE_ALL_FIELD, Fields.ALL_DOCS)
					.endObject()
					.startObject(Fields.Publication.Document.HASH)
						.field(TYPE_FIELD, STRING_TYPE)
						.field(INDEX_FIELD, NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.CONTENT_HASH)
						.field(TYPE_FIELD, STRING_TYPE)
						.field(INDEX_FIELD, NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.DATE)
						.field(TYPE_FIELD, STRING_TYPE)
						.field(INDEX_FIELD, NOT_INDEXED)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.KEY)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.ABSTRACT)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.BIBTEXKEY)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
			.endObject()
			.startObject(Fields.Publication.BOOKTITLE)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.CHAPTER)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.CROSSREF)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.DAY)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.EDITION)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.EDITORS)
				.field(TYPE_FIELD, NESTED_TYPE)
				.startObject(PROPERTIES)
					.startObject(Fields.Publication.PERSON_NAME)
						.field(TYPE_FIELD, STRING_TYPE)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.ENTRY_TYPE)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.HOWPUBLISHED)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.INSTITUTION)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.JOURNAL)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.MISC)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.MONTH)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.NOTE)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.NUMBER)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.ORGANIZATION)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.PAGES)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.PRIVNOTE)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INCLUDE_IN_ALL_FIELD, false)
				.field(COPY_TO, Fields.PRIVATE_ALL_FIELD)
				.field("store", "false") // TODO: remove?
			.endObject()
			.startObject(Fields.PRIVATE_ALL_FIELD)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.PUBLISHER)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.SCHOOL)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.Publication.SERIES)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.TYPE)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.URL)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(Fields.Publication.VOLUME)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject(Fields.Publication.YEAR)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
				.field(INCLUDE_IN_ALL_FIELD, true)
			.endObject()
			.startObject(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(Fields.PERSON_ENTITY_IDS_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject();
	}

}
