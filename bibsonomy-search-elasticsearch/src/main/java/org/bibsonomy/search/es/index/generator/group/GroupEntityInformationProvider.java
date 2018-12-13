package org.bibsonomy.search.es.index.generator.group;

import java.util.Map;

import org.bibsonomy.model.Group;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.mapping.group.GroupMappingBuilder;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * entity information provider
 *
 * @author dzo
 */
public class GroupEntityInformationProvider extends EntityInformationProvider<Group> {

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected GroupEntityInformationProvider(Converter<Group, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		super(converter, mappingBuilder);
	}

	@Override
	public int getContentId(Group group) {
		return group.getGroupId();
	}

	@Override
	public String getEntityId(Group entity) {
		return entity.getName();
	}

	@Override
	public String getType() {
		return GroupMappingBuilder.GROUP_DOCUMENT_TYPE;
	}
}
