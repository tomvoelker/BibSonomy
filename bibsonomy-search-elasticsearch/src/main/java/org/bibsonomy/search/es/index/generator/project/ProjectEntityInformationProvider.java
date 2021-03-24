package org.bibsonomy.search.es.index.generator.project;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * entity information provider
 *
 * @author dzo
 */
public class ProjectEntityInformationProvider extends OneToManyEntityInformationProvider<Project, CRISLink> {


	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 * @param toManyEntityInformationProvider
	 */
	public ProjectEntityInformationProvider(Converter<Project, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder, EntityInformationProvider<CRISLink> toManyEntityInformationProvider) {
		super(converter, mappingBuilder, toManyEntityInformationProvider);
	}

	@Override
	public int getContentId(final Project project) {
		return project.getId();
	}

	@Override
	public String getEntityId(final Project entity) {
		return entity.getExternalId();
	}

	@Override
	public String getType() {
		return ProjectFields.PROJECT_DOCUMENT_TYPE;
	}

	@Override
	public String getRouting(final Project entity) {
		return entity.getExternalId();
	}
}
