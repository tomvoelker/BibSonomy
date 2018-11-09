package org.bibsonomy.search.es.index.converter.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * convertes a {@link Project} for elasticsearch
 * @author dzo
 */
public class ProjectConverter implements Converter<Project, Map<String, Object>, Object> {

	@Override
	public Map<String, Object> convert(final Project source) {
		final Map<String, Object> converted = new HashMap<>();

		converted.put(ProjectFields.EXTERNAL_ID, source.getExternalId());
		converted.put(ProjectFields.TITLE, source.getTitle());
		converted.put(ProjectFields.SUB_TITLE, source.getSubTitle());
		converted.put(ProjectFields.DESCRIPTION, source.getDescription());
		converted.put(ProjectFields.TYPE, source.getType());
		converted.put(ProjectFields.BUDGET, source.getBudget());
		converted.put(ProjectFields.START_DATE, ElasticsearchUtils.dateToString(source.getStartDate()));
		converted.put(ProjectFields.END_DATE, ElasticsearchUtils.dateToString(source.getEndDate()));
		final Project parentProject = source.getParentProject();
		if (present(parentProject)) {
			converted.put(ProjectFields.PARENT, parentProject.getExternalId());
		}

		return converted;
	}

	@Override
	public Project convert(Map<String, Object> source, Object options) {
		final Project project = new Project();
		project.setExternalId((String) source.get(ProjectFields.EXTERNAL_ID));
		project.setBudget((Float) source.get(ProjectFields.BUDGET));
		project.setTitle((String) source.get(ProjectFields.TITLE));
		project.setSubTitle((String) source.get(ProjectFields.SUB_TITLE));
		project.setDescription((String) source.get(ProjectFields.DESCRIPTION));
		project.setType((String) source.get(ProjectFields.TYPE));
		if (source.containsKey(ProjectFields.PARENT)) {
			final Project parentProject = new Project();
			parentProject.setExternalId((String) source.get(ProjectFields.PARENT));
			project.setParentProject(parentProject);
		}
		project.setStartDate(ElasticsearchUtils.parseDate(source, ProjectFields.START_DATE));
		project.setEndDate(ElasticsearchUtils.parseDate(source, ProjectFields.END_DATE));

		return project;
	}
}

