package org.bibsonomy.search.es.index.converter.cris;

import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * converts a {@link Project} as source to a elasticsearch document
 *
 * @author dzo
 */
public class CRISSourceProjectConverter implements CRISEntityConverter<Project, Map<String, Object>, Object> {

	@Override
	public boolean canConvert(Linkable linkable) {
		return linkable instanceof Project;
	}

	@Override
	public Map<String, Object> convert(Project source) {
		final Map<String, Object> document = new HashMap<>();
		return document;
	}

	@Override
	public Project convert(Map<String, Object> source, Object options) {
		throw new UnsupportedOperationException();
	}
}
