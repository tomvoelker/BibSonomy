package org.bibsonomy.search.es.index.converter.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.util.Converter;

import java.util.Map;

/**
 * convertes a {@link Project} for elasticsearch
 * @author dzo
 */
public class ProjectConverter implements Converter<Project, Map<String, Object>, Object> {

	@Override
	public Map<String, Object> convert(Project source) {
		return null;
	}

	@Override
	public Project convert(Map<String, Object> source, Object options) {
		return null;
	}
}

