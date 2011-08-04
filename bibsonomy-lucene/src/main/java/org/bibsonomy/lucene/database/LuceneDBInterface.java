package org.bibsonomy.lucene.database;

import java.util.Date;
import java.util.List;

import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

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
	 * @param limit
	 * @param offset
	 * @return all posts for given user
	 */
	public List<LucenePost<R>> getPostsForUser(final String userName, final int limit, final int offset);

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
	 * @return get list of all user spam flags since last index update  
	 */
	public List<User> getPredictionForTimeRange(Date fromDate);
	
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
	 * @param lastContentId the last content id (all post.contentid > lastContentId)
	 * @param max size
	 * @return get post entries for index creation
	 */
	public List<LucenePost<R>> getPostEntries(int lastContentId, int max);
}
