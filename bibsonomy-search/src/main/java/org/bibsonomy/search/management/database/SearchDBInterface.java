/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.management.database;

import java.util.Date;
import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.model.User;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * interface encapsulating database access for lucene
 * 
 * @author fei
 * 
 * @param <R> resource type
 */
public interface SearchDBInterface<R extends Resource> {
	
	/** the max entries to fetch from the database into memory */
	public static final int SQL_BLOCKSIZE = 40000;

	/** 
	 * @param userName
	 * @param limit
	 * @param offset
	 * @return all posts for given user
	 */
	public List<SearchPost<R>> getPostsForUser(final String userName, final int limit, final int offset);

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
	 * @param limit 
	 * @param offset 
	 * @return new posts to insert in the index
	 */
	public List<SearchPost<R>> getNewPosts(int lastTasId, int limit, int offset);
	
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
	public List<SearchPost<R>> getPostEntries(int lastContentId, int max);

	/**
	 * @return
	 */
	public long getLastPersonChangeId();

	/**
	 * @param fromPersonChangeId
	 * @param toPersonChangeIdExclusive
	 * @param databaseSession
	 * @return
	 */
	public List<ResourcePersonRelationLogStub> getPubPersonRelationsByChangeIdRange(long fromPersonChangeId, long toPersonChangeIdExclusive);

	/**
	 * @param firstChangeId
	 * @param toPersonChangeIdExclusive
	 * @return
	 */
	public List<PersonName> getPersonMainNamesByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive);

	/**
	 * @param firstChangeId
	 * @param toPersonChangeIdExclusive 
	 * @return
	 */
	public List<Person> getPersonByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive);

	/**
	 * @param interHash
	 * @return
	 */
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash);

	/**
	 * @return
	 */
	public SearchIndexSyncState getDbState();
}
