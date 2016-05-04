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
package org.bibsonomy.webapp.command;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.exception.LogicException;

/**
 * Bean for User-Sites
 *
 * @author  Dominik Benz
 */
public class UserResourceViewCommand extends TagResourceViewCommand {

	private final Collection<LogicException> logicExceptions = new ArrayList<>();
	
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();
	/**
     * used to show infos about the user in the sidebar (only for admins, currently)
     */
	private User user;
	@Deprecated // TODO: remove!
	private boolean isFollowerOfUser = false;
	
	/**
	 * Has the requested user added the logged in user to her friend list? 
	 */
	private boolean friendOfUser = false;
	/**
	 * Has the logged in user added the requested user to his friend list?
	 */
	private boolean ofFriendUser = false;
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
	 */
	// TODO: use UserRelation as type
	private String userSimilarity = UserRelation.FOLKRANK.name();
	
	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}
	
	/**
	 * The groups that are shared by the requested and the loggedIn user
	 */
	private List<Group> sharedGroups = null;

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Set user similarity 
	 * @param userSimilarity - a string describing the user similarity
	 */
	public void setUserSimilarity(String userSimilarity) {
		this.userSimilarity = userSimilarity;
	}

	/**
	 * Get user similarity 
	 * @return - the user similarity
	 */
	public String getUserSimilarity() {
		return userSimilarity;
	}

	/**
	 * Get boolean if user is following this user or if not
	 * @return true if user already follows this user and false if not
	 */
	public boolean getIsFollowerOfUser() {
		return this.isFollowerOfUser;
	}

	/**
	 * Set if user is following this use or if not
	 * @param isFollowerOfUser
	 */
	public void setIsFollowerOfUser(boolean isFollowerOfUser) {
		this.isFollowerOfUser = isFollowerOfUser;
	}

	/**
	 * @return <code>true</code> if the logged in user is in the friend list of the requested user.
	 */
	public boolean getFriendOfUser() {
		return this.friendOfUser;
	}

	/**
	 * @return <code>true</code> if the requested user is in the friend list of the logged in user.
	 */
	public boolean getOfFriendUser() {
		return this.ofFriendUser;
	}

	/**
	 * @param friendOfUser
	 */
	public void setFriendOfUser(boolean friendOfUser) {
		this.friendOfUser = friendOfUser;
	}

	/**
	 * @param ofFriendUser
	 */
	public void setOfFriendUser(boolean ofFriendUser) {
		this.ofFriendUser = ofFriendUser;
	}

	/**
	 * @return a list of all groups that are shared between the loggedIn and the requested user
	 */
	public List<Group> getSharedGroups() {
		return this.sharedGroups;
	}

	/**
	 * @param sharedGroups the list of all groups that are shared between the loggedIn and the requested user
	 */
	public void setSharedGroups(List<Group> sharedGroups) {
		this.sharedGroups = sharedGroups;
	}
	
	/**
	 * @return the logicExceptions
	 */
	public Collection<LogicException> getLogicExceptions() {
		return this.logicExceptions;
	}
	
}