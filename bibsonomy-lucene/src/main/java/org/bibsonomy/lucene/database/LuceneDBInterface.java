/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	/**
	 * 
	 * @param lastOffset the last offset (i.e. the number of posts indexed so far)
	 * @param max size
	 * @return post entries for index creation
	 */
	public List<LucenePost<R>> getPostEntriesOrderedByHash(int lastOffset, int max);
}
