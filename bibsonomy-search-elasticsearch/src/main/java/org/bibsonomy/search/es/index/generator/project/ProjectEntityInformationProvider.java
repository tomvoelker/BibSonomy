package org.bibsonomy.search.es.index.generator.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * entity information provider
 *
 * @author dzo
 */
public class ProjectEntityInformationProvider extends EntityInformationProvider<Project> {

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected ProjectEntityInformationProvider(Converter<Project, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		super(converter, mappingBuilder);
	}

	@Override
	public int getContentId(Project project) {
		return project.getId();
	}

	@Override
	public String getEntityId(Project entity) {
		return entity.getExternalId();
	}

	@Override
	public String getType() {
		return null;
	}
}
