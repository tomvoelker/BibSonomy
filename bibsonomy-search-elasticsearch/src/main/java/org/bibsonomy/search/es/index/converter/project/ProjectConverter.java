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
package org.bibsonomy.search.es.index.converter.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.util.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * convertes a {@link Project} for elasticsearch
 * @author dzo
 */
public class ProjectConverter implements Converter<Project, Map<String, Object>, Boolean> {

	@Override
	public Map<String, Object> convert(final Project source) {
		final Map<String, Object> converted = new HashMap<>();

		converted.put(ProjectFields.EXTERNAL_ID, source.getExternalId());
		final String title = source.getTitle();
		converted.put(ProjectFields.TITLE, title);
		converted.put(ProjectFields.TITLE_PREFIX, ElasticsearchIndexSearchUtils.getPrefixForString(title.toLowerCase()));
		converted.put(ProjectFields.SUB_TITLE, source.getSubTitle());
		converted.put(ProjectFields.DESCRIPTION, source.getDescription());
		converted.put(ProjectFields.TYPE, source.getType());
		converted.put(ProjectFields.SPONSOR, source.getSponsor());
		converted.put(ProjectFields.BUDGET, source.getBudget());
		converted.put(ProjectFields.START_DATE, ElasticsearchUtils.dateToString(source.getStartDate()));
		converted.put(ProjectFields.END_DATE, ElasticsearchUtils.dateToString(source.getEndDate()));
		final Project parentProject = source.getParentProject();
		if (present(parentProject)) {
			converted.put(ProjectFields.PARENT, parentProject.getExternalId());
		}

		converted.put(ProjectFields.JOIN_FIELD, Collections.singletonMap("name", ProjectFields.TYPE_PROJECT));

		return converted;
	}

	@Override
	public Project convert(Map<String, Object> source, Boolean fullDetails) {
		final Project project = new Project();
		project.setExternalId((String) source.get(ProjectFields.EXTERNAL_ID));

		// only set full details about the project when the user is allowed to see them
		if (fullDetails && source.containsKey(ProjectFields.BUDGET)) {
			project.setBudget(((Double) source.get(ProjectFields.BUDGET)).floatValue());
		}

		project.setTitle((String) source.get(ProjectFields.TITLE));
		project.setSubTitle((String) source.get(ProjectFields.SUB_TITLE));
		project.setDescription((String) source.get(ProjectFields.DESCRIPTION));
		project.setType((String) source.get(ProjectFields.TYPE));
		project.setSponsor((String) source.get(ProjectFields.SPONSOR));
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

