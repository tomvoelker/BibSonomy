package org.bibsonomy.lucene.database;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.params.GroupParam;
import org.bibsonomy.lucene.database.params.GroupTasParam;
import org.bibsonomy.lucene.database.params.TasParam;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * interface encapsulating database access for lucene
 * 
 * @author fei
 * 
 * @param R resource type
 */
public interface LuceneDBInterface<R extends Resource> {

	/**
	 * get all posts for given user
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param simHash
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<LucenePost<R>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset);

	/**
	 * get most recent post's date
	 * @return
	 */
	public Date getNewestRecordDateFromTas();

	/**
	 * get most recent post's date
	 * @return
	 */
	public Integer getNewestContentIdFromTas();
	
	/**
	 * get list of content ids to delete from index with fromDate<date<=date
	 * 
	 * @param fromDate
	 * @param toDate
	 */
	public List<Integer> getContentIdsToDelete(Date fromDate, Date toDate);
	
	/**
	 * get list of content ids to delete from index with fromDate<date<=date
	 * 
	 * @param fromDate
	 */
	public List<Integer> getContentIdsToDelete(Date lastLogDate);

	/**
	 * get all public posts within a given time range
	 * 
	 * FIXME: use bibsonomy model classes
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Deprecated
	public List<HashMap<String, Object>> getPostsForTimeRange(Date fromDate, Date toDate);

	public List<Post<R>> getPostsForTimeRange2(Date fromDate, Date toDate);

	public List<LucenePost<R>> getNewPosts(Integer lastTasId);

	/**
	 * get list of all posts where in the given time range only the tag assignments
	 * have changed
	 * 
	 * @param retrieveFromDate
	 * @param retrieveToDate
	 * @return
	 */
	public List<Post<R>> getUpdatedPostsForTimeRange(Date fromDate, Date toDate);
	
	/**
	 * get list of all users which where flagged as spammer since last 
	 * index update
	 * 
	 * @param fromDate
	 * @return
	 */
	public List<String> getSpamPredictionForTimeRange(Date fromDate);

	/**
	 * get list of all users which where flagged as spammer since last 
	 * index update
	 * 
	 * @param fromDate
	 * @return
	 */
	public List<String> getNonSpamPredictionForTimeRange(Date fromDate);

	
	/**
	 * get group name
	 * 
	 * @param groupId
	 * @return
	 */
	public String getGroupNameByGroupId(int groupId);

	/**
	 * get given groups members
	 * 
	 * @param groupId
	 * @return
	 */
	public List<String> getGroupMembersByGroupId(int groupId);

	/**
	 * get all members of the given group, which have the user as a friend
	 * 
	 * @param groupId
	 * @param authUserName
	 * @return
	 */
	public List<String> getGroupFriendsByGroupIdForUser(int groupId, String authUserName);	
	
	//------------------------------------------------------------------------
	// methods for building the index
	// TODO: maybe we should introduce a special class hierarchy
	//------------------------------------------------------------------------
	/** 
	 * get newest tas_id from database 
	 */
	public Integer getLastTasId();

	/** 
	 * get latest log_date from database
	 */
	public Date getLastLogDate();
	
	/** 
	 * get number of TAS entries 
	 */
	public int getTasSize();
	
	/**
	 * get number of posts
	 */
	public int getNumberOfPosts();
	
	/**
	 * get list of group ids with corresponding group names
	 * @return
	 * @throws SQLException
	 */
	public List<GroupParam> getGroupIDs();
	
	/**
	 * get list of tag assignments, that is: pairs of tag names with corresponding 
	 * content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 * @throws SQLException
	 */
	public List<TasParam> getTasEntries(Integer skip, Integer max);
	
	/**
	 * get list of tag assignments, already grouped per post
	 * 
	 * @param skip
	 * @param max
	 * @return pair of content_id with corresponding space separated list of assigned tags
	 * @throws SQLException 
	 */
	public List<TasParam> getGroupedTasEntries(int skip, int max);

	/**
	 * get list of group ids with corresponding content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 * @throws SQLException
	 */
	public List<GroupTasParam> getGroupTasEntries(Integer skip, Integer max);

	/**
	 * get post entries for index creation
	 * 
	 * @param skip offset
	 * @param max size
	 * @return
	 */
	List<LucenePost<R>> getPostEntries(Integer skip, Integer max);
	
	/**
	 * get map from url-hashes to corresponding urls
	 */
	Map<String,String> getUrlMap();
}
