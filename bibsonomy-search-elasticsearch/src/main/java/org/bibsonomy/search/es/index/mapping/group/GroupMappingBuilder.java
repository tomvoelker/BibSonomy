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
							.endObject()
							// real name
							.startObject(GroupFields.REALNAME)
								.field(TYPE_FIELD, TEXT_TYPE)
								.field(ESConstants.IndexSettings.ANALYZER, ESConstants.NGRAM_ANALYSER)
								.startObject("fields")
									.startObject(GroupFields.REALNAME_SORT)
										.field(TYPE_FIELD, KEYWORD_TYPE)
									.endObject()
								.endObject()
							.endObject()
							.startObject(GroupFields.REALNAME_LOWERCASE)
								.field(TYPE_FIELD, TEXT_TYPE)
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
