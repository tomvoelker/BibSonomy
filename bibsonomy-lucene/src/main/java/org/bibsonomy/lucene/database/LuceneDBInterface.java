package org.bibsonomy.lucene.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;

/**
 * interface encapsulating database access for lucene
 * 
 * @author fei
 * @version $Id$
 * 
 * @param <R> resource type
 */
public interface LuceneDBInterface<R extends Resource> {

	/** 
	 * @param userName
	 * @param requestedUserName
	 * @param simHash
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @return all posts for given user
	 */
	public List<LucenePost<R>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset);

	/**
	 * @return get most recent post's date
	 */
	public Date getNewestRecordDateFromTas();
	
	/**
	 * get list of content ids to delete from index with fromDate<date<=date
	 * 
	 * @param lastLogDate
	 * @return list of content ids to delete from index with fromDate<date<=date
	 */
	public List<Integer> getContentIdsToDelete(Date lastLogDate);
	
	/**
	 * @param lastTasId
	 * @return new posts to insert in the index
	 */
	public List<LucenePost<R>> getNewPosts(Integer lastTasId);
	
	/**
	 * @param fromDate
	 * @return get list of all users which where flagged as spammer since last 
	 * index update
	 */
	public List<String> getSpamPredictionForTimeRange(Date fromDate);

	/** 
	 * @param fromDate
	 * @return get list of all users which where flagged as spammer since last 
	 * index update
	 */
	public List<String> getNonSpamPredictionForTimeRange(Date fromDate);

	/**
	 * get list of all friends for a given user
	 * 
	 * @param userName the user name
	 * @return all friends of given user 
	 */
	public Collection<String> getFriendsForUser(String userName);

	/**
	 * get given group's members
	 * 
	 * @param groupName
	 * @return the members of the group
	 */
	public List<String> getGroupMembersByGroupName(String groupName);
	
	//------------------------------------------------------------------------
	// methods for building the index
	// TODO: maybe we should introduce a special class hierarchy
	//------------------------------------------------------------------------
	/** 
	 * @return get newest tas_id from database 
	 */
	public Integer getLastTasId();

	/** 
	 * @return get latest log_date from database
	 */
	public Date getLastLogDate();
	
	/**
	 * @return get number of posts
	 */
	public int getNumberOfPosts();

	/** 
	 * @param skip offset
	 * @param max size
	 * @return get post entries for index creation
	 */
	public List<LucenePost<R>> getPostEntries(Integer skip, Integer max);
}
