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
	/** 
	 * the ranking period; starting from 1:
	 *   - 1: the most recent 1000 posts
	 *   - 2: most recent 1001 to 2000
	 *   - 3: most recent 2001 to 3000... 
	*/
	private Integer rankingPeriod;
	
	/**
     * Start-/End values for ranking periods
    */ 
	private Integer rankingPeriodStart;
	private Integer rankingPeriodEnd;
	
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

	public void setRankingPeriod(Integer rankingPeriod) {
		this.rankingPeriod = rankingPeriod;
	}

	public Integer getRankingPeriod() {
		return rankingPeriod;
	}

	public void setRankingPeriodStart(Integer rankingPeriodStart) {
		this.rankingPeriodStart = rankingPeriodStart;
	}

	public Integer getRankingPeriodStart() {
		return rankingPeriodStart;
	}

	public void setRankingPeriodEnd(Integer rankingPeriodEnd) {
		this.rankingPeriodEnd = rankingPeriodEnd;
	}

	public Integer getRankingPeriodEnd() {
		return rankingPeriodEnd;
	}
	
	public Integer getNextRankingPeriod() {
		if (this.rankingPeriod == null) {
			return 1;
		}
		return this.rankingPeriod + 1;
	}
	
	public Integer getPrevRankingPeriod() {
		if (this.rankingPeriod == null || this.rankingPeriod == 0) {
			return 0;
		}
		return this.rankingPeriod - 1;
	}

	public void setUserSimilarity(String userSimilarity) {
		this.userSimilarity = userSimilarity;
	}

	public String getUserSimilarity() {
		return userSimilarity;
	}	

	
}
