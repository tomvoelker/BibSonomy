package org.bibsonomy.lucene.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.params.GroupParam;
import org.bibsonomy.lucene.database.params.GroupTasParam;
import org.bibsonomy.lucene.database.params.TasParam;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Post;
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
	 * @return get most recent post's date
	 */
	public Integer getNewestContentIdFromTas();
	
	/**
	 * @param fromDate
	 * @param toDate
	 * 
	 * @return get list of content ids to delete from index with fromDate<date<=date
	 */
	public List<Integer> getContentIdsToDelete(Date fromDate, Date toDate);
	
	/**
	 * get list of content ids to delete from index with fromDate<date<=date
	 * 
	 * @param lastLogDate
	 * @return list of content ids to delete from index with fromDate<date<=date
	 */
	public List<Integer> getContentIdsToDelete(Date lastLogDate);

	/**
	 * @param fromDate
	 * @param toDate
	 * 
	 * @return TODO: improve documentation
	 */
	@Deprecated // TODO: really?!
	public List<Post<R>> getPostsForTimeRange2(Date fromDate, Date toDate);
	
	/**
	 * @param lastTasId
	 * @return new posts to insert in the index
	 */
	public List<LucenePost<R>> getNewPosts(Integer lastTasId);

	/**
	 * @param fromDate
	 * @param toDate
	 * @return  get list of all posts where in the given time range only the tag assignments
	 * have changed
	 */
	@Deprecated // TODO: really?!
	public List<Post<R>> getUpdatedPostsForTimeRange(Date fromDate, Date toDate);
	
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
	 * @param groupId
	 * @return group name to the given groupId
	 */
	@Deprecated // TODO: really?!
	public String getGroupNameByGroupId(int groupId);

	/**
	 * @param groupId
	 * @return the members of the group
	 */
	@Deprecated // TODO: really?!
	public List<String> getGroupMembersByGroupId(int groupId);

	/**
	 * get given group's members
	 * 
	 * @param groupName
	 * @return the members of the group
	 */
	public List<String> getGroupMembersByGroupName(String groupName);

	/**
	 * @param groupId
	 * @param authUserName
	 * @return get all members of the given group, which have the user as a friend
	 */
	@Deprecated // TODO: really?!
	public List<String> getGroupFriendsByGroupIdForUser(int groupId, String authUserName);	
	
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
	 * @return get number of TAS entries 
	 */
	@Deprecated // TODO: really?
	public int getTasSize();
	
	/**
	 * @return get number of posts
	 */
	public int getNumberOfPosts();
	
	/**
	 * @return get list of group ids with corresponding group names
	 */
	public List<GroupParam> getGroupIDs();
	
	/**
	 * get list of tag assignments, that is: pairs of tag names with corresponding 
	 * content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 */
	public List<TasParam> getTasEntries(Integer skip, Integer max);
	
	/**
	 * get list of tag assignments, already grouped per post
	 * 
	 * @param skip
	 * @param max
	 * @return pair of content_id with corresponding space separated list of assigned tags
	 */
	public List<TasParam> getGroupedTasEntries(int skip, int max);

	/**
	 * get list of group ids with corresponding content ids
	 * 
	 * @param skip The number of results to ignore
	 * @param max The maximum number of results to return
	 * @return A List of result objects
	 */
	@Deprecated
	public List<GroupTasParam> getGroupTasEntries(Integer skip, Integer max);

	/** 
	 * @param skip offset
	 * @param max size
	 * @return get post entries for index creation
	 */
	public List<LucenePost<R>> getPostEntries(Integer skip, Integer max);
	
	/**
	 * @return get map from url-hashes to corresponding urls
	 */
	public Map<String,String> getUrlMap();

	/**
	 * @return the latest date in db
	 */
	public Date getLatestDate();

	/**
	 * TODO: remove or merge with {@link #getNewPosts(Integer)}
	 * @param from TODO
	 * @param now TODO
	 * @return TODO
	 */
	public List<LucenePost<R>> getNewPosts(Date from, Date now);

	/**
	 * @param offset
	 * @param limit
	 * @param date
	 * @return all posts for a resource specified by offset and limit and not older than date
	 */
	public List<LucenePost<R>> getPosts(int offset, int limit, Date date);

	/**
	 * 
	 * @param from
	 * @param now
	 * @return all posts to delete from the index
	 */
	public List<LucenePost<R>> getPostsToDelete(Date from, Date now);
}
