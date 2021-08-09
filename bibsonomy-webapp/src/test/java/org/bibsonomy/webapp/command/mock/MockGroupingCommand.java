/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.mock;

import java.util.List;

import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author  dzo
 * @version  $Id$
 */
public class MockGroupingCommand implements GroupingCommand {
	private String abstractGrouping;
	private List<String> groups;
	
	/**
	 * @param abstractGrouping the abstractGrouping to set
	 */
	@Override
	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}
	
	/**
	 * @return the abstractGrouping
	 */
	@Override
	public String getAbstractGrouping() {
		return abstractGrouping;
	}
	
	/**
	 * @param groups the groups to set
	 */
	@Override
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	/**
	 * @return the groups
	 */
	@Override
	public List<String> getGroups() {
		return groups;
	}
}