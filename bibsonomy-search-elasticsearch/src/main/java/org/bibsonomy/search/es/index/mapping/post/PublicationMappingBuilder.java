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
package org.bibsonomy.search.es.index.mapping.post;

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
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.ANNOTE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.AUTHORS)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.NESTED_TYPE)
				.startObject(ESConstants.IndexSettings.PROPERTIES)
					.startObject(Fields.Publication.PERSON_NAME)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.array(ESConstants.IndexSettings.COPY_TO, Fields.Publication.ALL_AUTHORS)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.AUTHOR_INDEX)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
				.field(ESConstants.IndexSettings.BOOST_FIELD, 0)
			.endObject()
			.startObject(Fields.Publication.DOCUMENTS)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.NESTED_TYPE)
				.startObject(ESConstants.IndexSettings.PROPERTIES)
					.startObject(Fields.Publication.Document.NAME)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.TEXT)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.array(ESConstants.IndexSettings.COPY_TO, Fields.Publication.ALL_DOCS)
					.endObject()
					.startObject(Fields.Publication.Document.HASH)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.CONTENT_HASH)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
					.endObject()
					.startObject(Fields.Publication.Document.DATE)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.KEY)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.ABSTRACT)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.BIBTEXKEY)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(Fields.Publication.BOOKTITLE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.CHAPTER)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.CROSSREF)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.DAY)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(Fields.Publication.EDITION)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.EDITORS)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.NESTED_TYPE)
				.startObject(ESConstants.IndexSettings.PROPERTIES)
					.startObject(Fields.Publication.PERSON_NAME)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
					.endObject()
				.endObject()
			.endObject()
			.startObject(Fields.Publication.ENTRY_TYPE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(Fields.Publication.HOWPUBLISHED)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.INSTITUTION)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.JOURNAL)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.MONTH)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(Fields.Publication.NOTE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.NUMBER)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.ORGANIZATION)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.PAGES)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.PRIVNOTE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.PUBLISHER)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.SCHOOL)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.SERIES)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.TYPE)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.URL)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.VOLUME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.Publication.YEAR)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			.startObject(Fields.PERSON_ENTITY_IDS_FIELD_NAME)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
			.endObject()
			// misc field for restoring the misc field of the publication model object
			.startObject(Fields.Publication.MISC)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
				.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
			.endObject()
			// all misc fields nested (TODO: no queries are using this nested field)
			.startObject(Fields.Publication.MISC_FIELDS)
				.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.NESTED_TYPE)
				.startObject(ESConstants.IndexSettings.PROPERTIES)
					.startObject(Fields.Publication.MISC_KEY)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
					.endObject()
					.startObject(Fields.Publication.MISC_VALUE)
						.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
						.array(ESConstants.IndexSettings.COPY_TO, Fields.Publication.MISC_FIELDS_VALUES)
					.endObject()
				.endObject()
			.endObject();
			// special misc fields
			for (final String specialMiscField : Fields.Publication.SPECIAL_MISC_FIELDS) {
				builder.startObject(specialMiscField)
					.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
				.endObject();
			}
	}

}
