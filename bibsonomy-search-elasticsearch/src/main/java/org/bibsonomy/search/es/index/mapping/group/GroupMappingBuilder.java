/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.search.es.index.mapping.group;

import static org.bibsonomy.search.es.ESConstants.IndexSettings.ENABLED;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.KEYWORD_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.NOT_INDEXED;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TEXT_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TYPE_FIELD;

import java.io.IOException;

import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.group.GroupFields;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * mapping for {@link org.bibsonomy.model.Group}s
 *
 * @author dzo
 */
public class GroupMappingBuilder implements MappingBuilder<XContentBuilder> {

	public static final String GROUP_DOCUMENT_TYPE = "group";

	@Override
	public Mapping<XContentBuilder> getMapping() {
		try {
			final XContentBuilder groupMapping = XContentFactory.jsonBuilder()
					.startObject()
						.field("date_detection", false)
						.startObject(ESConstants.IndexSettings.PROPERTIES)
							// group name
							.startObject(GroupFields.NAME)
								.field(TYPE_FIELD, KEYWORD_TYPE)
								.field(ESConstants.NORMALIZER, ESConstants.LOWERCASE_NORMALIZER)
							.endObject()
							// real name
							.startObject(GroupFields.REALNAME)
								.field(TYPE_FIELD, TEXT_TYPE)
								.field(ESConstants.IndexSettings.ANALYZER, ESConstants.STANDARD_TEXT_ANALYSER)
								.startObject(ESConstants.IndexSettings.FIELDS)
									.startObject(ESConstants.RAW_SUFFIX)
										.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
								.endObject()
							.endObject()
							// prefix of the real name
							.startObject(GroupFields.REALNAME_PREFIX)
								.field(TYPE_FIELD, KEYWORD_TYPE)
							.endObject()
							// group name for list sorting
							.startObject(GroupFields.SORTNAME)
								.field(TYPE_FIELD, KEYWORD_TYPE)
								.field(ESConstants.NORMALIZER, ESConstants.LOWERCASE_NORMALIZER)
							.endObject()
							// external id
							.startObject(GroupFields.INTERNAL_ID)
								.field(TYPE_FIELD, KEYWORD_TYPE)
							.endObject()
							// organization
							.startObject(GroupFields.ORGANIZATION)
								.field(TYPE_FIELD, "boolean")
							.endObject()
							// allows join requests
							.startObject(GroupFields.ALLOW_JOIN)
								.field(TYPE_FIELD, "boolean")
							.endObject()
							// shares documents
							.startObject(GroupFields.SHARES_DOCUMENTS)
								.field(TYPE_FIELD, "boolean")
							.endObject()
							// homepage
							.startObject(GroupFields.HOMEPAGE)
								.field(ENABLED, NOT_INDEXED)
							.endObject()
							// parent
							.startObject(GroupFields.PARENT_NAME)
								.field(TYPE_FIELD, KEYWORD_TYPE)
							.endObject()
						.endObject()
					.endObject();
			final Mapping<XContentBuilder> mapping = new Mapping<>();
			mapping.setMappingInfo(groupMapping);
			mapping.setType(GROUP_DOCUMENT_TYPE);
			return mapping;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
