package org.bibsonomy.lucene.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.Pair;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 * @version $Id$
 * @param <R> the resource the logic handles
 */
public abstract class LuceneDBLogic<R extends Resource> extends LuceneDBGenerateLogic<R> {
	private static final Log log = LogFactory.getLog(LuceneDBLogic.class);

	/**
	 * constructor disabled for enforcing singleton pattern 
	 */
	protected LuceneDBLogic() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<R>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset) {
		final ResourcesParam<R> param = getResourcesParam();
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(simHash);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		
		List<LucenePost<R>> retVal = null;
		try {
			retVal = this.sqlMap.queryForList("get" + this.getResourceName() + "ForUser", param);
		} catch (SQLException e) {
			log.error("Error fetching " +" for user " + param.getUserName(), e);
		}
		
		return retVal != null ? retVal : new LinkedList<LucenePost<R>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNewestRecordDateFromTas()
	 */
	@Override
	public Date getNewestRecordDateFromTas() {
		Date retVal = null;
		try {
			retVal = (Date)this.sqlMap.queryForObject("getNewestRecordDateFromTas");
		} catch (SQLException e) {
			log.error("Error determining last tas entry", e);
		}
		
		return retVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNewestContentIdFromTas()
	 */
	@Override
	public Integer getNewestContentIdFromTas() {
		Integer retVal = 0;
		try {
			retVal = (Integer)this.sqlMap.queryForObject("getNewestContentIdFromTas");
		} catch (SQLException e) {
			log.error("Error determining last tas entry", e);
		}
		
		return retVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getContentIdsToDelete(java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getContentIdsToDelete(Date fromDate, Date toDate) {
		List<Integer> retVal;
		
		Pair<Date,Date> param = new Pair<Date,Date>();
		param.setFirst(fromDate);
		param.setSecond(toDate);
		
		try {
			retVal = this.sqlMap.queryForList("get" + this.getResourceName() + "ContentIdsToDelete", param);
		} catch (SQLException e) {
			log.error("Error getting content ids to delete", e);
			retVal = new LinkedList<Integer>();
		}
		
		log.debug("getContentIdsToDelete - count: " + retVal.size());

		return retVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getContentIdsToDelete(java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getContentIdsToDelete(Date lastLogDate) {
		List<Integer> retVal;
		
		try {
			retVal = this.sqlMap.queryForList("get" + this.getResourceName() + "ContentIdsToDelete2", lastLogDate);
		} catch (SQLException e) {
			log.error("Error getting content ids to delete", e);
			retVal = new LinkedList<Integer>();
		}
		
		log.debug("getContentIdsToDelete - count: " + retVal.size());

		return retVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getUpdatedPostsForTimeRange(java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Post<R>> getUpdatedPostsForTimeRange(Date fromDate, Date toDate) {
		List<Post<R>> retVal = null;
		
		ResourcesParam<R> param = this.getResourcesParam();
		param.setFromDate(fromDate);
		param.setToDate(toDate);
		
		try {
			retVal = this.sqlMap.queryForList("getUpdated" + this.getResourceName() + "PostsForTimeRange", param);
		} catch (SQLException e) {
			log.error("Error getting content ids to delete", e);
		}
		if( retVal==null ) {
			retVal = new LinkedList<Post<R>>();
		}
		
		log.debug("getContentIdsToDelete - count: " + retVal.size());

		return retVal;	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getFriendsForUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> getFriendsForUser(String userName) {
		List<String> retVal = null;
		
		try {
			retVal = this.sqlMap.queryForList("getFriendsForUser", userName);
		} catch (SQLException e) {
			log.error("Error getting friends for user "+userName, e);
		}
		if( retVal==null ) {
			retVal = new LinkedList<String>();
		}
		
		return retVal;	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getGroupNameByGroupId(int)
	 */
	@Override
	public String getGroupNameByGroupId(int groupId) {
		String retVal = null;
		
		try {
			retVal = (String)this.sqlMap.queryForObject("getGroupNameByGroupId", groupId);
		} catch (SQLException e) {
			log.error("Error getting group name", e);
		}
		if( retVal==null ) {
			retVal = "";
		}
		
		return retVal;	
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getGroupMembersByGroupId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGroupMembersByGroupId(int groupId) {
		List<String> retVal = null;
		
		try {
			retVal = this.sqlMap.queryForList("getGroupMembersByGroupId", groupId);
		} catch (SQLException e) {
			log.error("Error getting group members", e);
		}
		if( retVal==null ) {
			retVal = new LinkedList<String>();
		}
		
		return retVal;	
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getGroupMembersByGroupName(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getGroupMembersByGroupName(String groupName) {
		List<String> retVal = null;
		
		try {
			retVal = this.sqlMap.queryForList("getGroupMembersByGroupName", groupName);
		} catch (SQLException e) {
			log.error("Error getting group members", e);
		}
		if( retVal==null ) {
			retVal = new LinkedList<String>();
		}
		
		return retVal;	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getGroupFriendsByGroupIdForUser(int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGroupFriendsByGroupIdForUser(int groupId, String authUserName) {
		List<String> retVal = null;
		
		Pair<Integer,String> param = new Pair<Integer, String>();
		param.setFirst(groupId);
		param.setSecond(authUserName);
		
		try {
			retVal = this.sqlMap.queryForList("getGroupFriendsByGroupIdForUser", param);
		} catch (SQLException e) {
			log.error("Error getting friends of given user for given group", e);
		}
		if( retVal==null ) {
			retVal = new LinkedList<String>();
		}
		
		return retVal;	
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getLastLogDate()
	 */
	@Override
	public Date getLastLogDate() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastLog" + this.getResourceName(), Date.class, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNumberOfPosts()
	 */
	@Override
	public int getNumberOfPosts() {
		Integer retVal = 0;
		try {
			retVal = (Integer)sqlMap.queryForObject("get" + this.getResourceName() + "Count");
		} catch (SQLException e) {
			log.error("Error determining " + this.getResourceName() + " size.", e);
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostsForTimeRange2(java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Post<R>> getPostsForTimeRange2(Date fromDate, Date toDate) {
		final ResourcesParam<R> param = this.getResourcesParam();
		param.setFromDate(fromDate);
		param.setToDate(toDate);
		param.setLimit(Integer.MAX_VALUE);
		
		try {
			return sqlMap.queryForList("get" + this.getResourceName() + "PostsForTimeRange2", param);
		} catch (SQLException e) {
			log.error("Error getting " + this.getResourceName() + " entries.", e);
		}
		
		return new LinkedList<Post<R>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostEntries(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<R>> getPostEntries(Integer skip, Integer max) {
		final ResourcesParam<R> param = this.getResourcesParam();
		param.setOffset(skip);
		param.setLimit(max);
		
		try {
			return sqlMap.queryForList("get" + this.getResourceName() + "ForIndex3", param);
		} catch (SQLException e) {
			log.error("Error getting " + this.getResourceName() + " entries.", e);
		}
		
		return new LinkedList<LucenePost<R>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNewPosts(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<R>> getNewPosts(Integer lastTasId) {
		final ResourcesParam<R> param = this.getResourcesParam();
		param.setLastTasId(lastTasId);
		param.setLimit(Integer.MAX_VALUE);
		
		try {
			return sqlMap.queryForList("get" + this.getResourceName() + "PostsForTimeRange3", param);
		} catch (SQLException e) {
			log.error("Error getting " + this.getResourceName() + " entries.", e);
		}
		
		return new LinkedList<LucenePost<R>>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<R>> getPosts(int offset, int limit, Date date) {
		final DBSession session = this.openSession();
		final ResourcesParam<R> param = this.getResourcesParam();
		param.setOffset(offset);
		param.setLimit(limit);
		param.setLastDate(date);
		try {
			return this.queryForList("get" + this.getResourceName() + "ForIndex", param, session);
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<R>> getPostsToDelete(Date lastLogDate, Date now) {
		final Pair<Date, Date> param = new Pair<Date, Date>(lastLogDate, now);
		final DBSession session = this.openSession();
		try {
			return this.queryForList("get" + this.getResourceName() + "PostsToDelete", param, session);			
		} finally {
			session.close();
		}
	}
	
	protected abstract String getResourceName();
	
	protected abstract ResourcesParam<R> getResourcesParam();
}
