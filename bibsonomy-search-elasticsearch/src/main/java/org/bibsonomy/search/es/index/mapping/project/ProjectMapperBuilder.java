package org.bibsonomy.search.es.index.mapping.project;

import static org.bibsonomy.search.es.ESConstants.IndexSettings.KEYWORD_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TEXT_TYPE;
import static org.bibsonomy.search.es.ESConstants.IndexSettings.TYPE_FIELD;

import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
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
	/*+ the person mapping type */
	public static final String PROJECT_DOCUMENT_TYPE = "project";

	@Override
	public Mapping<XContentBuilder> getMapping() {
		try {
			XContentBuilder projectMapping = XContentFactory.jsonBuilder()
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
						.endObject()
						// subtitle
						.startObject(ProjectFields.SUB_TITLE)
							.field(TYPE_FIELD, TEXT_TYPE)
						.endObject()
						// type
						.startObject(ProjectFields.TYPE)
							.field(TYPE_FIELD, KEYWORD_TYPE)
						.endObject()
							// type
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
							.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
							.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
						.endObject()
							// start date
						.startObject(ProjectFields.END_DATE)
							.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.DATE_TYPE)
							.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
							.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
						.endObject()
					.endObject()
				.endObject();
			final Mapping<XContentBuilder> mapping = new Mapping<>();
			mapping.setMappingInfo(projectMapping);
			mapping.setType(PROJECT_DOCUMENT_TYPE);
			return mapping;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
