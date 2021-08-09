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
package org.bibsonomy.database.managers.util.cris;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

/**
 * checks the link direction of a {@link Project} and {@link Person} link
 * @author dzo
 */
public class ProjectPersonLinkDirectionChecker implements LinkDirectionChecker {

	@Override
	public boolean requiresSwap(Linkable source, Linkable target) {
		if (checkType(source) && checkType(target)) {
			if (source instanceof Person) {
				return true;
			}
		}
		return false;
	}

	private boolean checkType(Linkable linkable) {
		return linkable instanceof Project || linkable instanceof Person;
	}
}
