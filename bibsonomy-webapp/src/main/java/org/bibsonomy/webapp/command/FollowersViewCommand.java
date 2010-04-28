package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;

/**
 * 
 * @author Christian Kramer
 * @version $Id$
 *
 */
public class FollowersViewCommand extends TagResourceViewCommand {
	private List<User> followersOfUser;
	private List<User> userIsFollowing;

	private RankingCommand ranking = new RankingCommand();
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
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
	public void setFollowersOfUser(List<User> followersOfUser) {
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
	public void setUserIsFollowing(List<User> userIsFollowing) {
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
	public void setRanking(RankingCommand ranking) {
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
	public void setUserSimilarity(String userSimilarity) {
		this.userSimilarity = userSimilarity;
	}
}
