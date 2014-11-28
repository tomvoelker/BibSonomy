/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Reporting command for a group.
 * 
 * FIXME: This is just a starting point.
 * 
 * @author dbenz
 */
public class GroupReportingCommand extends BaseCommand {

	/** holds the table with the publication counts per type */ 
	private final ReportingTableCommand<Integer, String, Integer> publicationCounts = new ReportingTableCommand<Integer, String, Integer>();
	
	/** the name of the requested group */
	private String requestedGroup = "";

	/** transposes displayed matrix if is set to 1 */
	// TODO: boolean as type
	private String transposeMatrix = "1";

	private String requestedTags;

	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(final String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the transposeMatrix
	 */
	public String getTransposeMatrix() {
		return this.transposeMatrix;
	}

	/**
	 * @param transposeMatrix the transposeMatrix to set
	 */
	public void setTransposeMatrix(final String transposeMatrix) {
		this.transposeMatrix = transposeMatrix;
	}

	/**
	 * @return the requestedTags
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}

	/**
	 * @param requestedTags the requestedTags to set
	 */
	public void setRequestedTags(final String requestedTags) {
		this.requestedTags = requestedTags;
	}

	/**
	 * @return the publicationCounts
	 */
	public ReportingTableCommand<Integer, String, Integer> getPublicationCounts() {
		return this.publicationCounts;
	}
	
}
