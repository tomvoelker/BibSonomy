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
package org.bibsonomy.webapp.command;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;

/**
 * Bean for User-Sites
 *
 * @author  Dominik Benz
 */
@Getter
@Setter
public class UserResourceViewCommand extends TagResourceViewCommand {
	
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();

	/** used to show infos about the user in the sidebar (only for admins, currently) */
	private User user;

	@Deprecated // TODO: remove!
	/** Get boolean if user is following this user or if not */
	private boolean isFollowerOfUser = false;
	
	/** <code>true</code> if the logged in user is in the friend list of the requested user. */
	private boolean friendOfUser = false;

	/** <code>true</code> if the requested user is in the friend list of the logged in user. */
	private boolean ofFriendUser = false;

	/** The claimed person of the user */
	private Person claimedPerson = null;

	/**
	 * defines the similarity measure by which the related users are computed
	 * a string describing the user similarity
	 * (default is folkrank)
	 */
	// TODO: use UserRelation as type
	private String userSimilarity = UserRelation.FOLKRANK.name();
	
	/** a list of all groups that are shared between the loggedIn and the requested user */
	private List<Group> sharedGroups = null;

}