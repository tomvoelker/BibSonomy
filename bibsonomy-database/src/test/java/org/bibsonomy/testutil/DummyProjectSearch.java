/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.testutil;

import org.bibsonomy.services.searcher.ProjectSearch;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * dummy implementation for {@link ProjectSearch}
 *
 * @author dzo
 */
public class DummyProjectSearch implements ProjectSearch {

	@Override
	public List<Project> getProjects(final User loggedinUser, final ProjectQuery query) {
		return new LinkedList<>();
	}

	@Override
	public Statistics getStatistics(User loggedinUser, ProjectQuery query) {
		return new Statistics();
	}

	@Override
	public <E> Set<E> getDistinctFieldValues(FieldDescriptor<Project, E> fieldDescriptor) {
		return Collections.emptySet();
	}
}
