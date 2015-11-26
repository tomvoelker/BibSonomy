/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.common.enums.UserRelation;


/**
 * @author Christian Kramer, Folke Mitzlaff
 */
public class UserRelationAjaxCommand extends AjaxCommand<String> {
	/**
	 * name of the requested user
	 */
	private String requestedUserName;
	
	private UserRelation userRelation = UserRelation.OF_FRIEND;
	
	/**
	 * list of requested relation names
	 */
	private List<String> relationTags;

	/**
	 * 
	 * @return requested username
	 */
	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	/**
	 * @param userName
	 */
	public void setRequestedUserName(String userName) {
		this.requestedUserName = userName;
	}

	/**
	 * @param relationTags
	 */
	public void setRelationTags(List<String> relationTags) {
		this.relationTags = relationTags;
	}

	/**
	 * @return relation tags
	 */
	public List<String> getRelationTags() {
		return relationTags;
	}

	/**
	 * @return the userRelation
	 */
	public UserRelation getUserRelation() {
		return this.userRelation;
	}

	/**
	 * @param userRelation the userRelation to set
	 */
	public void setUserRelation(UserRelation userRelation) {
		this.userRelation = userRelation;
	}
}
