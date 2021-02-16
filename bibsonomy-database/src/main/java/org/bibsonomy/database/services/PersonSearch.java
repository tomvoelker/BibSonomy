/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.database.services;

import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Interface for person search operations
 * 
 * @author jil
 */
public interface PersonSearch {

	/**
	 * Allows autocompletion for persons
	 * @param query contains a query with some mixture of parts of a name, parts of the title or the university name
	 * @return a list of {@link Person}s. Each {@link Person} object is further initialized with a main name.
	 */
	List<Person> getPersons(final PersonQuery query);

	/**
	 * statistics for the matching persons (like the count)
	 * @param loggedinUser
	 * @param query
	 * @return
	 */
	Statistics getStatistics(final User loggedinUser, final PersonQuery query);
}
