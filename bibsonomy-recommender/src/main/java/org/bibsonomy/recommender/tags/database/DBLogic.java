package org.bibsonomy.recommender.tags.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.database.params.Pair;
import org.bibsonomy.recommender.tags.database.params.RecQueryParam;
import org.bibsonomy.recommender.tags.database.params.RecSettingParam;
import org.bibsonomy.recommender.tags.database.params.SelectorSettingParam;
import org.bibsonomy.recommender.tags.database.params.TasEntry;

/**
 * @author rja
 * @version $Id$
 */
public interface DBLogic {

	//------------------------------------------------------------------------
	// database access interface
	//------------------------------------------------------------------------
	/**
	 * Add new query to database.
	 * 
	 * @param userName user who submitted post
	 * @param date querie's timestamp
	 * @param post user's post
	 * @param timeout querie's timeout value
	 * @return unique query id
	 * 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract Long addQuery(String userName, Date date, Post<? extends Resource> post, int postID, int  timeout) throws SQLException;

	/**
	 * Add recommender to given query.
	 * 
	 * @param queryId
	 * @param recId
	 * @param recMeta
	 * @return unique identifier for given recommender settings
	 * @throws SQLException 
	 */
	public abstract Long addRecommender(Long queryId, String recId, String recDescr, byte[] recMeta) throws SQLException;

	/**
	 * Add result selector to given query.
	 * @param qid query id
	 * @param resultSelector
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public abstract Long addResultSelector(Long qid, String selectorInfo, byte[] selectorMeta) throws SQLException;

	/**
	 * Add id of recommender selected for given query.
	 * 
	 * @param qid query_id 
	 * @param sid recommender's setting id
	 * @throws SQLException 
	 */
	public abstract void addSelectedRecommender(Long qid, Long sid) throws SQLException;

	/**
	 * Add recommender's recommended tags.
	 * 
	 * @param queryId unique id identifying query
	 * @param settingsId unique id identifying recommender
	 * @param tags recommended tags
	 * @param latency 
	 * @return number of recommendations added
	 * @throws SQLException
	 */
	public abstract int addRecommendation(Long queryId, Long settingsId, SortedSet<RecommendedTag> tags, long latency) throws SQLException;

	/**
	 * Connect postID with recommendation.
	 *    For each post process an unique id is generated. This is used for mapping 
	 *    posts to recommendations and vice verca.  
	 * @param post post as stored in bibsonomy
	 * @param post's random id as generated in PostBookmarkController
	 * @throws SQLException 
	 */
	public abstract void connectWithPost(Post<? extends Resource> post, int postID) throws SQLException;

	/**
	 * Get sorted list of tags recommended in a given query by a given recommender. 
	 * 
	 * @param qid
	 * @param sid
	 * @return tags recommended in query identified by qid and recommender identified by sid
	 * @throws SQLException
	 */
	public abstract SortedSet<RecommendedTag> getRecommendations(Long qid, Long sid) throws SQLException;

	/**
	 * Append tags which were recommended in a given query by a given recommender to a given collection. 
	 * 
	 * @param qid
	 * @param sid
	 * @return tags recommended in query identified by qid and recommender identified by sid
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public abstract void getRecommendations(Long qid, Long sid, Collection<RecommendedTag> recommendedTags) throws SQLException;

	/**
	 * Get sorted list of tags recommended in a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 * @throws SQLException
	 */
	public abstract SortedSet<RecommendedTag> getRecommendations(Long qid) throws SQLException;

	/**
	 * Append tags which are recommended in a given query to given collection 
	 * 
	 * @param qid query id
	 * @param recommendedTags collection where recommended tags should be appended
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public abstract void getRecommendations(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException;

	/**
	 * Get (unsorted) list of selected tags for a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public abstract List<RecommendedTag> getSelectedTags(Long qid) throws SQLException;

	/**
	 * Get list of recommender settings which where selected for given query.
	 * 
	 * @param qid query_id
	 * @return list of recommender settings 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Long> getSelectedRecommenderIDs(Long qid) throws SQLException;

	/**
	 * Get list of newest tas entries
	 * @param offset
	 * @param range
	 * @return list of range number of new entries, starting by offset 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<TasEntry> getNewestEntries(Integer offset, Integer range) throws SQLException;

	/**
	 * Get user's most popular tag names with corresponding tag frequencies 
	 * 
	 * @param username
	 * @param range - the number of tags to get 
	 * 
	 * @return list of pairs [tagname,frequency]
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Pair<String, Integer>> getMostPopularTagsForUser(final String username, final int range) throws SQLException;

	/**
	 * Gets the most popular tags of the given resource.
	 * 
	 * @param <T> The type of the resource.
	 * @param resourceType
	 * @param intraHash
	 * @param range
	 * @return The most popular tags of the given resource.
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public abstract <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range) throws SQLException;

	/**
	 * Get number of tags used by given user. 
	 * @param username
	 * @return number of tags used by given user
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract Integer getNumberOfTagsForUser(String username) throws SQLException;

	/**
	 * Get number of TAS of the given user. 
	 * @param username
	 * @return number of TAS of given user
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract Integer getNumberOfTasForUser(String username) throws SQLException;

	/**
	 * Get number of tags attached to a given resource.. 
	 * @param <T> 
	 * @param resourceType - type of the resource 
	 * @param intraHash - hash of the resource
	 * 
	 * @return The number of tags attached to the resource.
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash) throws SQLException;

	/**
	 * Get number of TAS for a given resource.. 
	 * @param <T> 
	 * @param resourceType - type of the resource 
	 * @param intraHash - hash of the resource
	 * 
	 * @return The number of TAS of the resource.
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract <T extends Resource> Integer getNumberOfTasForResource(final Class<T> resourceType, final String intraHash) throws SQLException;
	
	/**
	 * Maps BibSonomy's user name to corresponding user id
	 * 
	 * @param userName user's name
	 * @return user's id, null if user name doesn't exist
	 */
	public abstract Integer getUserIDByName(String userName);

	/**
	 * Maps BibSonomy's user id to corresponding user name
	 * 
	 * @param userID user's id
	 * @return user's name, null if user id doesn't exist
	 */
	public abstract String getUserNameByID(int userID);

	/**
	 * Get list of all tags from given recommender and query
	 * @param sid recommender's setting id
	 * @param qid query id
	 * @return list of all tags from given recommender and query
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<String> getTagNamesForRecQuery(Long sid, Long qid) throws SQLException;

	/**
	 * Get list of all tags chosen by user for given post
	 * @param cid post's content_id
	 * @return list of all tags chosen by user for given post
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<String> getTagNamesForPost(Integer cid) throws SQLException;

	/**
	 * Returns details for given recommender.
	 * @param sid Recommender's setting id
	 * @return Details for given recommender if found -- null otherwise
	 * @throws SQLException 
	 */
	public abstract RecSettingParam getRecommender(Long sid) throws SQLException;

	/**
	 * Get list of all recommenders (id) which delivered tags in given query.
	 * @param qid query id
	 * @return list of ids
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Long> getActiveRecommenderIDs(Long qid) throws SQLException;

	/**
	 * Get list of all recommenders (id) which where queried.
	 * @param qid query id
	 * @return list of ids
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Long> getAllRecommenderIDs(Long qid) throws SQLException;

	/**
	 * Get list of all recommenders (id) which where queried.
	 * @param qid query id
	 * @return list of ids
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Pair<Long, Long>> getRecommenderSelectionCount(Long qid) throws SQLException;

	/**
	 * Get list of all recommenders (id) which where queried and not selected previously 
	 * during given post process.
	 * 
	 * @param qid query id
	 * @return list of ids
	 * @throws SQLException 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Long> getAllNotSelectedRecommenderIDs(Long qid) throws SQLException;

	/**
	 * Returns details for given selector.
	 * @param sid Result selector's setting id
	 * @return Details for given recommender if found -- null otherwise
	 * @throws SQLException 
	 * @throws SQLException 
	 */
	public abstract SelectorSettingParam getSelector(Long sid) throws SQLException;

	/**
	 * Return query information for given query id
	 * @param qid querie's id
	 * @return RecQueryParam on success, null otherwise
	 * @throws SQLException 
	 */
	public abstract RecQueryParam getQuery(Long qid) throws SQLException;

	/**
	 * Return list of all queries for given recommender 
	 * @param sid recommender's query
	 * @return list of all queries for given recommender
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract List<RecQueryParam> getQueriesForRecommender(Long sid) throws SQLException;

	/**
	 * Tries to guess query_id from given content id.
	 * 
	 * @param content_id
	 * @return nearest query_id, if guess is possible -- otherwise null
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public abstract Long guessQueryFromPost(Integer content_id) throws SQLException;

	/**
	 * Guess content_id for given query_id.
	 * 
	 * @param query_id
	 * @return nearest content_id if found -- otherwise null
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public abstract Integer guessPostFromQuery(Long query_id) throws SQLException;

	/**
	 * Get queryID for given postID, user_name and date
	 * @param postID
	 * @return
	 * @throws SQLException 
	 */
	public abstract Long getQueryForPost(String user_name, Date date, Integer postID) throws SQLException;

	/**
	 * Get contentID for given queryID
	 * @throws SQLException 
	 */
	public abstract Integer getContentIDForQuery(Long queryID) throws SQLException;

	/**
	 * Get contentID for given query data
	 * @throws SQLException 
	 */
	public abstract Integer getContentIDForQuery(String userName, Date date, Integer postID);

	/**
	 * Store selected recommended tags.
	 * 
	 * @param qid query id
	 * @param rid result selector id
	 * @param result set of recommended tags
	 * @throws SQLException 
	 */
	public abstract int storeRecommendation(Long qid, Long rid, Collection<RecommendedTag> result) throws SQLException;

	//------------------------------------------------------------------------
	// logging interface implementation
	//------------------------------------------------------------------------
	/**
	 * Log recommender event.
	 * @param qid unique query id for identifying interrelated recommender responses
	 * @param sid unique id identifying recommender's settings
	 * @param latency 
	 * @param tags tags calculated by recommender
	 * @param preset predetermined tags, null if none given
	 * @return true on success, false otherwise
	 * @throws SQLException 
	 */
	public abstract boolean logRecommendation(Long qid, Long sid, long latency, SortedSet<RecommendedTag> tags, SortedSet<RecommendedTag> preset) throws SQLException;

}