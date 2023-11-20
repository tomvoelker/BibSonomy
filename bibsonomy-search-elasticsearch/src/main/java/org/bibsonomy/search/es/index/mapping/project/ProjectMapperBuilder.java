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
package org.bibsonomy.search.es.index.mapping.project;

import static org.bibsonomy.search.es.ESConstants.IndexSettings.JOIN_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.KEYWORD_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.RELATION_FIELD;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TEXT_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TYPE_FIELD;

import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.index.mapping.person.PersonMappingBuilder;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * builds a mapping for elasticsarch to query {@link org.bibsonomy.model.cris.Project}
 *
 * @author dzo
 */
public class ProjectMapperBuilder implements MappingBuilder<XContentBuilder> {

	@Override
	public Mapping<XContentBuilder> getMapping() {
		try {
			final XContentBuilder projectPersonMapping = XContentFactory.jsonBuilder()
				.startObject()
					.field("date_detection", false)
					.startObject(ESConstants.IndexSettings.PROPERTIES)
						// database id
						.startObject(ProjectFields.EXTERNAL_ID)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
						// title
						.startObject(ProjectFields.TITLE)
							.field(TYPE_FIELD, TEXT_TYPE)
							.field(ESConstants.IndexSettings.ANALYZER, ESConstants.STANDARD_TEXT_ANALYSER)
							.field(ESConstants.IndexSettings.BOOST_FIELD, 2.0)
							.startObject(ESConstants.IndexSettings.FIELDS)
								.startObject(ESConstants.RAW_SUFFIX)
									.field(TYPE_FIELD, KEYWORD_TYPE)
								.endObject()
							.endObject()
						.endObject()
						// lower case variant of the title
						.startObject(ProjectFields.TITLE_PREFIX)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
						// subtitle
						.startObject(ProjectFields.SUB_TITLE)
							.field(ESConstants.IndexSettings.ANALYZER, ESConstants.STANDARD_TEXT_ANALYSER)
							.field(TYPE_FIELD, TEXT_TYPE)
						.endObject()
						// description
						.startObject(ProjectFields.DESCRIPTION)
							.field(ESConstants.IndexSettings.ANALYZER, ESConstants.STANDARD_TEXT_ANALYSER)
							.field(TYPE_FIELD, TEXT_TYPE)
						.endObject()
						// type
						.startObject(ProjectFields.TYPE)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
						// sponsor
						.startObject(ProjectFields.SPONSOR)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
							// budget
						.startObject(ProjectFields.BUDGET)
							.field(TYPE_FIELD, "float")
						.endObject()
						// parent
						.startObject(ProjectFields.PARENT)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
						// start date
						.startObject(ProjectFields.START_DATE)
							.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.DATE_TYPE)
							.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
						.endObject()
							// start date
						.startObject(ProjectFields.END_DATE)
							.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.DATE_TYPE)
							.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
						.endObject()
						// join field
						.startObject(ProjectFields.JOIN_FIELD)
							.field(TYPE_FIELD, JOIN_TYPE)
							.startObject(RELATION_FIELD)
							.field(ProjectFields.TYPE_PROJECT, PersonFields.TYPE_PERSON)
							.endObject()
						.endObject()
						// cris links
						// - persons
						// person id
						.startObject(PersonFields.PERSON_ID)
							.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
						.endObject();
						// person names
			final XContentBuilder projectMapping = PersonMappingBuilder.buildNameMapping(projectPersonMapping).endObject()
				.endObject();
			final Mapping<XContentBuilder> mapping = new Mapping<>();
			mapping.setMappingInfo(projectMapping);
			mapping.setType(ProjectFields.PROJECT_DOCUMENT_TYPE);
			return mapping;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
