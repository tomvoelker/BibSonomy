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

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;

/**
 * 
 * @author Christian Kramer
 */
public class FollowersViewCommand extends TagResourceViewCommand {
	private List<User> followersOfUser;
	private List<User> userIsFollowing;

	private RankingCommand ranking = new RankingCommand();
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
	 * TODO: change type to UserRelation
	 */
	private String userSimilarity = UserRelation.FOLKRANK.name();	
	
	
	/**
	 * 
	 * @return all users which are following this user
	 */
	public List<User> getFollowersOfUser() {
		return this.followersOfUser;
	}
	
	/**
	 * 
	 * @param followersOfUser
	 */
	public void setFollowersOfUser(final List<User> followersOfUser) {
		this.followersOfUser = followersOfUser;
	}
	
	/**
	 * 
	 * @return list of user which the user is following
	 */
	public List<User> getUserIsFollowing() {
		return this.userIsFollowing;
	}
	
	/**
	 * 
	 * @param userIsFollowing
	 */
	public void setUserIsFollowing(final List<User> userIsFollowing) {
		this.userIsFollowing = userIsFollowing;
	}

	/**
	 * @return the ranking
	 */
	public RankingCommand getRanking() {
		return this.ranking;
	}

	/**
	 * @param ranking the ranking to set
	 */
	public void setRanking(final RankingCommand ranking) {
		this.ranking = ranking;
	}

	/**
	 * @return the userSimilarity
	 */
	public String getUserSimilarity() {
		return this.userSimilarity;
	}

	/**
	 * @param userSimilarity the userSimilarity to set
	 */
	public void setUserSimilarity(final String userSimilarity) {
		this.userSimilarity = userSimilarity;
	}
}
