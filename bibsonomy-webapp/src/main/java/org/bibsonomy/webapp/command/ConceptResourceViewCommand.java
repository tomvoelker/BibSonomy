/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command;

/**
 * Bean for Concept Sites
 * 
 * @author Michael Wagner
 */
public class ConceptResourceViewCommand extends TagResourceViewCommand {
	
	/** the user whose resources are requested */
	// TODO: duplicate field @see ResourceViewCommand
	private String requestedUser = "";
	
	/** the group which resources are requested */
	private String requestedGroup = "";

	/** bean for concepts */
	private ConceptsCommand concepts = new ConceptsCommand();
	
	/**
	 * @return requestedUser the name of the user whose resources are requested
	 */
	@Override
	public String getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @param requestedUser the name of the user whose resources are requested
	 */
	@Override
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
	
	/**	
	 * @return the name of the group that resources are requested
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the group
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}	
}