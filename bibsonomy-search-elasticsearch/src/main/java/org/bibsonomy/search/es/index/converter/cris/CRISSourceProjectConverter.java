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
package org.bibsonomy.search.es.index.converter.cris;

import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.index.converter.person.PersonFields;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;

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
		final Map<Object, Object> relation = new HashMap<>();
		relation.put("name", PersonFields.TYPE_PERSON);
		relation.put("parent", source.getExternalId());
		document.put(ProjectFields.JOIN_FIELD, relation);
		return document;
	}

	@Override
	public Project convert(Map<String, Object> source, Object options) {
		throw new UnsupportedOperationException();
	}
}
