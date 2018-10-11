package org.bibsonomy.search.es.index.mapping.person;

import static org.bibsonomy.search.es.ESConstants.IndexSettings.DATE_TIME_FORMAT;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.DATE_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.ENABLED;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.FORMAT_FIELD;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.KEYWORD_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.NESTED_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.NOT_INDEXED;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TEXT_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TYPE_FIELD;

import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * this class builds a mapping for the converted {@link org.bibsonomy.model.Person}
 * @author dzo
 */
public class PersonMappingBuilder implements MappingBuilder<XContentBuilder> {
	/*+ the person mapping type */
	public static final String PERSON_DOCUMENT_TYPE = "person";

	@Override
	public Mapping<XContentBuilder> getMapping() {
		try {
			XContentBuilder personMapping = XContentFactory.jsonBuilder()
							.startObject()
							/*
							 * set the date detection to false: we load the misc
							 * fields as field = value into es (=> dynamic mapping)
							 */
								.field("date_detection", false)
								.startObject(ESConstants.IndexSettings.PROPERTIES)
									// person id
									.startObject(PersonFields.PERSON_ID)
									.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
									// academic degree
									.startObject(PersonFields.ACADEMIC_DEGREE)
									.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
									// college
									.startObject(PersonFields.COLLEGE)
									.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
									// homepage
									.startObject(PersonFields.HOMEPAGE)
									.field(ENABLED, NOT_INDEXED)
									.endObject()
									// email
									.startObject(PersonFields.EMAIL)
									.field(ENABLED, NOT_INDEXED)
									.endObject()
									// ocrid id
									.startObject(PersonFields.ORCID_ID)
									.field(ENABLED, NOT_INDEXED)
									.endObject()
									// research id
									.startObject(PersonFields.RESEARCHER_ID)
									.field(ENABLED, NOT_INDEXED)
									.endObject()
									// user name
									.startObject(PersonFields.USER_NAME)
									.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
									// gender
									.startObject(PersonFields.GENDER)
									.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
									// change data
									.startObject(PersonFields.CHANGE_DATE)
									.field(TYPE_FIELD, DATE_TYPE)
									.field(FORMAT_FIELD, DATE_TIME_FORMAT)
									.endObject()
									// names
									.startObject(PersonFields.NAMES)
										.field(TYPE_FIELD, NESTED_TYPE)
										.startObject(ESConstants.IndexSettings.PROPERTIES)
											.startObject(ESConstants.Fields.Publication.PERSON_NAME)
												.field(PersonFields.NAME, TEXT_TYPE)
												.array(ESConstants.IndexSettings.COPY_TO, PersonFields.ALL_NAMES)
											.endObject()
										.endObject()
									.endObject()
								.endObject()
							.endObject()
						.endObject();
			final Mapping<XContentBuilder> mapping = new Mapping<>();
			mapping.setMappingInfo(personMapping);
			mapping.setType(PERSON_DOCUMENT_TYPE);
			return mapping;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
