package org.bibsonomy.recommender.tags.database;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
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
	 * @param postID TODO
	 * @param timeout querie's timeout value
	 * @return unique query id
	 */
	public Long addQuery(String userName, Date date, Post<? extends Resource> post, int postID, int timeout);

	/**
	 * Add recommender to given query.
	 * 
	 * @param queryId
	 * @param recId
	 * @param recDescr 
	 * @param recMeta
	 * @return unique identifier for given recommender settings
	 */
	public Long addRecommender(Long queryId, String recId, String recDescr, byte[] recMeta);

	/**
	 * adds given recommender (identified by it's id) to given query
	 * 
	 * @param qid query's id
	 * @param sid recommender's id
	 */
	public void addRecommenderToQuery(Long qid, Long sid );
	
	/**
	 * insert recommender setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * @param recId 
	 * @param recDescr 
	 * @param recMeta 
	 * 
	 * @return unique identifier for given settings
	 */
	public Long insertRecommenderSetting(String recId, String recDescr, byte[] recMeta);
	
	/**
	 * Add result selector to given query.
	 * @param qid query id
	 * @param selectorInfo 
	 * @param selectorMeta 
	 * 
	 * @return TODO 
	 */
	public Long addResultSelector(Long qid, String selectorInfo, byte[] selectorMeta);

	/**
	 * set selection strategy which was applied in given query
	 * 
	 * @param qid query's id
	 * @param rid result selector's id
	 */
	public void setResultSelectorToQuery(Long qid, Long rid );

	/**
	 * insert selector setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * @param selectorInfo 
	 * @param selectorMeta 
	 * @return unique identifier for given settings
	 */
	public Long insertSelectorSetting(String selectorInfo, byte[] selectorMeta);

	/**
	 * Add id of recommender selected for given query.
	 * 
	 * @param qid query_id 
	 * @param sid recommender's setting id
	 */
	public void addSelectedRecommender(Long qid, Long sid);

	/**
	 * Add recommender's recommended tags.
	 * 
	 * @param queryId unique id identifying query
	 * @param settingsId unique id identifying recommender
	 * @param tags recommended tags
	 * @param latency 
	 * @return number of recommendations added
	 */
	public int addRecommendation(Long queryId, Long settingsId, SortedSet<RecommendedTag> tags, long latency);

	/**
	 * Connect postID with recommendation.
	 *  For each post process an unique id is generated. This is used for mapping 
	 *  posts to recommendations and vice verca. 
	 * @param post post as stored in bibsonomy
	 * @param postID post's random id as generated in PostBookmarkController
	 */
	public void connectWithPost(Post<? extends Resource> post, int postID);

	/**
	 * Get sorted list of tags recommended in a given query by a given recommender. 
	 * 
	 * @param qid
	 * @param sid
	 * @return tags recommended in query identified by qid and recommender identified by sid
	 */
	public SortedSet<RecommendedTag> getRecommendations(Long qid, Long sid);

	/**
	 * Append tags which were recommended in a given query by a given recommender to a given collection. 
	 * 
	 * @param qid
	 * @param sid
	 * @param recommendedTags 
	 */
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedTag> recommendedTags);

	/**
	 * Get sorted list of tags recommended in a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 */
	public SortedSet<RecommendedTag> getRecommendations(Long qid);

	/**
	 * Append tags which are recommended in a given query to given collection 
	 * 
	 * @param qid query id
	 * @param recommendedTags collection where recommended tags should be appended
	 */
	public void getRecommendations(Long qid, Collection<RecommendedTag> recommendedTags);

	/**
	 * Get (unsorted) list of selected tags for a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 */
	public List<RecommendedTag> getSelectedTags(Long qid);

	/**
	 * Get list of recommender settings which where selected for given query.
	 * 
	 * @param qid query_id
	 * @return list of recommender settings 
	 */
	public List<Long> getSelectedRecommenderIDs(Long qid);

	/**
	 * Get list of newest tas entries
	 * @param offset
	 * @param range
	 * @return list of range number of new entries, starting by offset 
	 */
	public List<TasEntry> getNewestEntries(Integer offset, Integer range);

	/**
	 * Get user's most popular tag names with corresponding tag frequencies 
	 * 
	 * @param username
	 * @param range - the number of tags to get 
	 * 
	 * @return list of pairs [tagname,frequency]
	 */
	public List<Pair<String, Integer>> getMostPopularTagsForUser(final String username, final int range);

	/**
	 * Gets the most popular tags of the given resource.
	 * 
	 * @param <T> The type of the resource.
	 * @param resourceType
	 * @param intraHash
	 * @param range
	 * @return The most popular tags of the given resource.
	 */
	public <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range);

	/**
	 * Get number of tags used by given user. 
	 * @param username
	 * @return number of tags used by given user 
	 */
	public Integer getNumberOfTagsForUser(String username);

	/**
	 * Get number of TAS of the given user. 
	 * @param username
	 * @return number of TAS of given user
	 */
	public Integer getNumberOfTasForUser(String username);

	/**
	 * Get number of tags attached to a given resource.. 
	 * @param <T> 
	 * @param resourceType - type of the resource 
	 * @param intraHash - hash of the resource
	 * 
	 * @return The number of tags attached to the resource.
	 */
	public <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash);

	/**
	 * Get number of TAS for a given resource.. 
	 * @param <T> 
	 * @param resourceType - type of the resource 
	 * @param intraHash - hash of the resource
	 * 
	 * @return The number of TAS of the resource.
	 */
	public <T extends Resource> Integer getNumberOfTasForResource(final Class<T> resourceType, final String intraHash);
	
	/**
	 * Maps BibSonomy's user name to corresponding user id
	 * 
	 * @param userName user's name
	 * @return user's id, null if user name doesn't exist
	 */
	public Integer getUserIDByName(String userName);	

	/**
	 * Maps BibSonomy's user id to corresponding user name
	 * 
	 * @param userID user's id
	 * @return user's name, null if user id doesn't exist
	 */
	public String getUserNameByID(int userID);

	/**
	 * Get list of all tags from given recommender and query
	 * @param sid recommender's setting id
	 * @param qid query id
	 * @return list of all tags from given recommender and query
	 */
	public List<String> getTagNamesForRecQuery(Long sid, Long qid);

	/**
	 * Get list of all tags chosen by user for given post
	 * @param cid post's content_id
	 * @return list of all tags chosen by user for given post
	 */
	public List<String> getTagNamesForPost(Integer cid);

	/**
	 * Returns details for given recommender.
	 * @param sid Recommender's setting id
	 * @return Details for given recommender if found -- null otherwise
	 */
	public RecSettingParam getRecommender(Long sid);

	/**
	 * Get list of all recommenders (id) which delivered tags in given query.
	 * @param qid query id
	 * @return list of ids
	 */
	public List<Long> getActiveRecommenderIDs(Long qid);

	/**
	 * Get list of all recommenders (id) which where queried.
	 * @param qid query id
	 * @return list of ids
	 */
	public List<Long> getAllRecommenderIDs(Long qid);

	/**
	 * Get list of all recommenders (id) which where queried.
	 * @param qid query id
	 * @return list of ids
	 */
	public List<Pair<Long, Long>> getRecommenderSelectionCount(Long qid);

	/**
	 * Get list of all recommenders (id) which where queried and not selected previously 
	 * during given post process.
	 * 
	 * @param qid query id
	 * @return list of ids
	 */
	public List<Long> getAllNotSelectedRecommenderIDs(Long qid);

	/**
	 * Returns details for given selector.
	 * @param sid Result selector's setting id
	 * @return Details for given recommender if found -- null otherwise
	 */
	public SelectorSettingParam getSelector(Long sid);

	/**
	 * Return query information for given query id
	 * @param qid querie's id
	 * @return RecQueryParam on success, null otherwise
	 */
	public RecQueryParam getQuery(Long qid);

	/**
	 * Return list of all queries for given recommender 
	 * @param sid recommender's query
	 * @return list of all queries for given recommender
	 */
	public List<RecQueryParam> getQueriesForRecommender(Long sid);

	/**
	 * Get recommender-info for admin statuspage
	 * @return recommender-info
	 * @param id recommenderID
	 */
	public RecAdminOverview getRecommenderAdminOverview(String id);
		
	/**
	 * Get the average latency of a given recommender-setting
	 * 
	 * @param sid
	 * @param numberOfQueries number of newest latency-values which will be fetched to calculate the average latency
	 * @return average latency of the recommender
	 */
	public Long getAverageLatencyForRecommender(Long sid, Long numberOfQueries);

	/**
	 * Get all setting-ids which are flagged as a distant recommender
	 * @return setting-ids of all distant recommenders
	 */
	public List<Long> getDistantRecommenderSettingIds();
	
	/**
	 * Get all setting-ids which are flagged as a local recommender
	 * @return setting-ids of all local recommenders
	 */
	public List<Long> getLocalRecommenderSettingIds();
	
	/**
	 * Get all settingids which are set to status 'active'
	 * @return identifiers of currently activated settings 
	 */
	public List<Long> getActiveRecommenderSettingIds();
	
	/**
	 * Get all settingids which are set to status 'disabled'
	 * @return identifiers of currently disabled settings 
	 */
	public List<Long> getDisabledRecommenderSettingIds();
	
    /**
     * Get related recommender-ids for a list of setting-ids
     * @param sids setting-ids
     * @return map settingid->recommenderid
     */
	public Map<Long, String> getRecommenderIdsForSettingIds(List<Long> sids);
	
	/**
	 * Activate and disable several recommenders at once.
	 * @param activeRecs identifiers of the settings to be activated
	 * @param disabledRecs identifiers of the settings to be disabled
	 */
	public void updateRecommenderstatus(List<Long> activeRecs, List<Long> disabledRecs );
		
	/**
	 * Set a recommender to status 'removed'.
	 * @param url recommender-id
	 */
	public void removeRecommender(String url);
	
	/**
	 * Change the url of a recommender which is already contained in the database.
	 * @param sid setting-id
	 * @param url new url
	 */
	public void updateRecommenderUrl(long sid, URL url);
	
	/**
	 * Tries to guess query_id from given content id.
	 * 
	 * @param content_id
	 * @return nearest query_id, if guess is possible -- otherwise null
	 */
	public Long guessQueryFromPost(Integer content_id);

	/**
	 * Guess content_id for given query_id.
	 * 
	 * @param query_id
	 * @return nearest content_id if found -- otherwise null
	 */
	public Integer guessPostFromQuery(Long query_id);

	/**
	 * Get queryID for given postID, user_name and date
	 * @param user_name 
	 * @param date 
	 * @param postID
	 * @return TODO
	 */
	public Long getQueryForPost(String user_name, Date date, Integer postID);

	/**
	 * Get contentID for given queryID
	 * @param queryID 
	 * 
	 * @return TODO
	 */
	public Integer getContentIDForQuery(Long queryID);

	/**
	 * Store selected recommended tags.
	 * 
	 * @param qid query id
	 * @param rid result selector id
	 * @param result set of recommended tags
	 * @return TODO
	 */
	public int storeRecommendation(Long qid, Long rid, Collection<RecommendedTag> result);
	
	/**
	 * Log recommender event.
	 * @param qid unique query id for identifying interrelated recommender responses
	 * @param sid unique id identifying recommender's settings
	 * @param latency 
	 * @param tags tags calculated by recommender
	 * @param preset predetermined tags, null if none given
	 * @return true on success, false otherwise
	 */
	public boolean logRecommendation(Long qid, Long sid, long latency, SortedSet<RecommendedTag> tags, SortedSet<RecommendedTag> preset);

}