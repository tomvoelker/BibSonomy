package org.bibsonomy.recommender;

import java.io.Reader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.multiplexer.strategy.RecommendationSelector;
import org.bibsonomy.recommender.params.Pair;
import org.bibsonomy.recommender.params.PostGuess;
import org.bibsonomy.recommender.params.PostParam;
import org.bibsonomy.recommender.params.QueryGuess;
import org.bibsonomy.recommender.params.RecQueryParam;
import org.bibsonomy.recommender.params.RecQuerySettingParam;
import org.bibsonomy.recommender.params.RecResponseParam;
import org.bibsonomy.recommender.params.RecSettingParam;
import org.bibsonomy.recommender.params.SelectorTagParam;
import org.bibsonomy.recommender.params.SelectorQueryMapParam;
import org.bibsonomy.recommender.params.SelectorSettingParam;
import org.bibsonomy.recommender.params.TasEntry;
import org.bibsonomy.recommender.params.TasParam;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Class for encapsulating database access of recommenders.
 * 
 * @author fei
 */
public class DBAccess extends AbstractDatabaseManager {
	private static final Logger log = Logger.getLogger(DBAccess.class);
	
	//------------------------------------------------------------------------
	// database logic interface
	//------------------------------------------------------------------------
	/**
	 * Initialize iBatis layer.
	 */
	private static final SqlMapClient sqlMap;
	private static final SqlMapClient sqlBibMap;  // access to bibsonomy's db

	static {
		try {
			// initialize database client for recommender logs
			String resource = "SqlMapConfig_recommender.xml";
			Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database [1] connection initialized.");
			
			// initialize database client for accessing post data
			String resource2 = "SqlMapConfig_recommenderBibDB.xml";
			Reader reader2 = Resources.getResourceAsReader (resource2);
			sqlBibMap = SqlMapClientBuilder.buildSqlMapClient(reader2);
			log.info("Database [2] connection initialized.");			
		} catch (Exception e) {
			throw new RuntimeException ("Error initializing DBAccess class. Cause: " + e);
		}
	}

	/**
	 * Get (unique) database handler for querying the recommender database.
	 * @return The default SqlMap which can be used to query the database.
	 */
	public static SqlMapClient getSqlMapInstance () {
		return sqlMap;
	}

	/**
	 * Get (unique) database handler for querying bibsonomy's database.
	 * @return The SqlMap which can be used to query bibsonomy's database
	 */
	private static SqlMapClient getSqlBibMapInstance () {
		return sqlBibMap;
	}

	//------------------------------------------------------------------------
	// database access interface
	//------------------------------------------------------------------------
	/**
	 * Add new query to database.
	 * 
	 * @param userName user who submitted post
	 * @param date querie's timestamp
	 * @param post user's post
	 * @return unique query id
	 * 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static Long addQuery(
			String userName, Timestamp date, 
			Post<? extends Resource> post) throws SQLException {
		// construct parameter
		RecQueryParam recQuery = new RecQueryParam();
		recQuery.setTimeStamp(date);
		recQuery.setUserName(userName);
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) )
			recQuery.setContentType(new Integer(1));
		else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) )
			recQuery.setContentType(new Integer(2));
		
		// insert recommender query
		Long queryId = (Long) sqlMap.insert("addRecommenderQuery", recQuery);

		// store post
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) )
			storeBookmarkPost(userName, queryId, (Post<Bookmark>)post, "", true);
		else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) )
			storeBibTexPost(userName, queryId, (Post<BibTex>)post, "", true);
		
		// all done.
		return queryId;
	}
	
	/**
	 * Add recommender to given query.
	 * 
	 * @param queryId
	 * @param recId
	 * @param recMeta
	 * @return unique identifier for given recommender settings
	 * @throws SQLException 
	 */
	public static Long addRecommender(Long queryId, String recId, byte[] recMeta ) throws SQLException {
		Long settingId = null;

		SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
    		sqlMap.startTransaction();
    		
    		// insert recommender settings
    		settingId = insertRecommenderSetting(recId, recMeta);
    		// connect query with setting
    		RecQuerySettingParam queryMap = new RecQuerySettingParam();
    		queryMap.setQid(queryId);
    		queryMap.setSid(settingId);
    		sqlMap.insert("addRecommenderQuerySetting", queryMap);
    		
    		sqlMap.commitTransaction();
    	} finally {
    		sqlMap.endTransaction();
    	}		
		
		return settingId;
	}		

	/**
	 * Add result selector to given query.
	 * @param qid query id
	 * @param resultSelector
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public static Long addResultSelector(Long qid, String selectorInfo, byte[] selectorMeta ) throws SQLException {
		Long selectorID = null;

		SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
    		sqlMap.startTransaction();
    		
    		// insert recommender settings
    		selectorID = insertSelectorSetting(selectorInfo, selectorMeta);
    		// connect query with setting
    		SelectorQueryMapParam queryMap = new SelectorQueryMapParam();
    		queryMap.setQid(qid);
    		queryMap.setSid(selectorID);
    		sqlMap.insert("addSelectorQuerySetting", queryMap);
    		
    		sqlMap.commitTransaction();
    	} finally {
    		sqlMap.endTransaction();
    	}		
		
		return selectorID;
	}


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
	public static int addRecommendation(
			Long queryId, Long settingsId,
			SortedSet<RecommendedTag> tags,
			long latency ) throws SQLException {

		SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
    		sqlMap.startTransaction();
    		
    		// insert recommender response
    		// #qid#, #sid#, #latency#, #score#, #confidence#, #tagName# )
    		RecResponseParam response = new RecResponseParam();
    		response.setQid(queryId);
    		response.setSid(settingsId);
			response.setLatency(latency);
    		for( RecommendedTag tag : tags ) {
    			response.setTagName( tag.getName() );
    			response.setConfidence( tag.getConfidence() );
    			response.setScore( tag.getScore() );
    			sqlMap.insert("addRecommenderResponse", response);
    		}    		
    		
    		sqlMap.commitTransaction();
    	} finally {
    		sqlMap.endTransaction();
    	}		
		
		return tags.size();
	}

	/**
	 * Get sorted list of tags recommended in a given query by a given recommender. 
	 * 
	 * @param qid
	 * @param sid
	 * @return tags recommended in query identified by qid and recommender identified by sid
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static SortedSet<RecommendedTag> getRecommendations(Long qid, Long sid) throws SQLException {
		// TODO ugly inefficient implementation
		log.warn("Inefficient implementation");
		
		// print out newly added recommendations
		RecQuerySettingParam queryMap = new RecQuerySettingParam();
		queryMap.setQid(qid);
		queryMap.setSid(sid);
	    List<RecommendedTag> queryResult = sqlMap.queryForList("getRecommendationsByQidSid", queryMap);
	    SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
	    result.addAll(queryResult);
	    
	    // all done.
	    return result;
	}	

	/**
	 * Get sorted list of tags recommended in a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static SortedSet<RecommendedTag> getRecommendations(Long qid) throws SQLException {
		// TODO ugly inefficient implementation
		log.warn("Inefficient implementation");
	    List<RecommendedTag> queryResult = sqlMap.queryForList("getRecommendationsByQid", qid);
	    SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
	    result.addAll(queryResult);
	    // all done.
	    return result;
	}		

	/**
	 * Get (unsorted) list of selected tags for a given query. 
	 * 
	 * @param qid
	 * @return tags recommended in query identified by qid and all recommenders 
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static List<RecommendedTag> getSelectedTags(Long qid) throws SQLException {
	    List<RecommendedTag> queryResult = sqlMap.queryForList("getSelectedRecommendationsByQid", qid);
	    // all done.
	    return queryResult;
	}		
	
	
	/**
	 * Get list of newest tas entries
	 * @param offset
	 * @param range
	 * @return list of range number of new entries, starting by offset 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static List<TasEntry> getNewestEntries(Integer offset, Integer range) throws SQLException {
		TasParam param = new TasParam();
		param.setOffset(offset);
		param.setRange(range);
		List<TasEntry> queryResult = (List<TasEntry>)sqlBibMap.queryForList("getNewestEntries", param);
		
		return queryResult;
	}
	
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
	public static List<Pair<String,Integer>> getMostPopularTagsForUser(final String username, final int range) throws SQLException {
		final PostParam param = new PostParam();
		param.setUserName(username);
		param.setRange(range);
		
		return getSqlBibMapInstance().queryForList("getMostPopularTagsForUser", param);
	}
	
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
	public static <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range) throws SQLException {
		final PostParam param = new PostParam();
		param.setIntraHash(intraHash);
		param.setRange(range);
		
		if (BibTex.class.equals(resourceType)) {
			return getSqlBibMapInstance().queryForList("getMostPopularTagsForBibTeX", param);
		} else if (Bookmark.class.equals(resourceType)) {
			return getSqlBibMapInstance().queryForList("getMostPopularTagsForBookmark", param);
		}
		throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
	}
	/**
	 * Get number of tags used by given user. 
	 * @param username
	 * @return number of tags used by given user
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static Integer getNumberOfTagsForUser(String username) throws SQLException {
		return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForUser", username);
	}
	
	/**
	 * Get number of tags attached to a given resource.. 
	 * @param resourceType - type of the resource 
	 * @param intraHash - hash of the resource
	 * 
	 * @return The number of tags attached to the resource.
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash) throws SQLException {
		if (BibTex.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForBibTeX", intraHash);
		} else if (Bookmark.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForBookmark", intraHash);
		}
		throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
	}
	
	/**
	 * Get list of all tags from given recommender and query
	 * @param sid recommender's setting id
	 * @param qid query id
	 * @return list of all tags from given recommender and query
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTagNamesForRecQuery(Long sid, Long qid) throws SQLException {
		RecQuerySettingParam param = new RecQuerySettingParam();
		param.setQid(qid);
		param.setSid(sid);
		return (List<String>)sqlMap.queryForList("getTagNamesForRecQuery", param);
	}
	
	/**
	 * Get list of all tags chosen by user for given post
	 * @param cid post's content_id
	 * @return list of all tags chosen by user for given post
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTagNamesForPost(Integer cid) throws SQLException {
		return (List<String>)sqlBibMap.queryForList("getTagNamesForCID", cid);
	}
	
	/**
	 * Returns details for given recommender.
	 * @param sid Recommender's setting id
	 * @return Details for given recommender if found -- null otherwise
	 * @throws SQLException 
	 */
	public static RecSettingParam getRecommender(Long sid) throws SQLException {
		return (RecSettingParam)getSqlMapInstance().queryForObject("getRecommenderByID", sid);
	}

	/**
	 * Returns details for given selector.
	 * @param sid Result selector's setting id
	 * @return Details for given recommender if found -- null otherwise
	 * @throws SQLException 
	 * @throws SQLException 
	 */
	public static SelectorSettingParam getSelector(Long sid) throws SQLException {
		return (SelectorSettingParam)getSqlMapInstance().queryForObject("getSelectorByID", sid);
	}
	
	/**
	 * Return query information for given query id
	 * @param qid querie's id
	 * @return RecQueryParam on success, null otherwise
	 * @throws SQLException 
	 */
	public static RecQueryParam getQuery(Long qid) throws SQLException {
		return (RecQueryParam)sqlMap.queryForObject("getQueryByID", qid);
	}

	/**
	 * Return list of all queries for given recommender 
	 * @param sid recommender's query
	 * @return list of all queries for given recommender
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static List<RecQueryParam> getQueriesForRecommender(Long sid) throws SQLException {
		return (List<RecQueryParam>)sqlMap.queryForList("getQueriesBySID", sid);
	}
	
	
	/**
	 * Tries to guess query_id from given content id.
	 * 
	 * @param content_id
	 * @return nearest query_id, if guess is possible -- otherwise null
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	static public Long guessQueryFromPost(Integer content_id) throws SQLException {
		Long result  = null;
		TasEntry tas = null;
		
		// get post's timestamp
		List<?> queryresult = sqlBibMap.queryForList("getTasEntryForID", content_id);
		if( queryresult.size()>0 ) {
			tas = (TasEntry)(sqlBibMap.queryForList("getTasEntryForID", content_id).get(0));
			// get nearest recommender queries
			PostParam param = new PostParam();
			param.setTimestamp(tas.getTimeStamp());
			param.setUserName(tas.getUserName());
			List<QueryGuess> qids = (List<QueryGuess>)sqlMap.queryForList("getNearestQueriesForPost", param);
			// select first one if exists
			if( (qids.size()>0)&&(qids.get(0).getDiff()<300) )
				result = qids.get(0).getQid();			
		};
		
		// all done.
		return result; 
	}

	/**
	 * Guess content_id for given query_id.
	 * 
	 * @param query_id
	 * @return nearest content_id if found -- otherwise null
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	static public Integer guessPostFromQuery(Long query_id) throws SQLException {
		Integer result  = null;
		
		// get query information
		RecQueryParam recQuery = (RecQueryParam)sqlMap.queryForObject("getQueryByID", query_id);
		
		if( recQuery != null ) {
			// look for nearest post
			PostParam param = new PostParam();
			param.setTimestamp(recQuery.getTimeStamp());
			param.setUserName(recQuery.getUserName());
			List<PostGuess> cids = (List<PostGuess>)sqlBibMap.queryForList("getNearestPostsForQuery", param);
			// select first one if exists
			// FIXME: think of something usefull
			if( (cids.size()>0)&&(cids.get(0).getDiff()<300) )
				result = cids.get(0).getContentID();
		} else 
			log.info("Content ID for query " + query_id + " not found.");
		
		// all done.
		return result; 		
	}
	/**
	 * insert recommender setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * 
	 * @return unique identifier for given settings
	 */
	private static Long insertRecommenderSetting(String recId, byte[] recMeta) throws SQLException {
		Long settingId = null;

		SqlMapClient sqlMap = getSqlMapInstance();

		RecSettingParam setting = new RecSettingParam();
		setting.setRecId(recId);
		setting.setRecMeta(recMeta);

		// determine which lookup sql statement we have to use.
		String lookupFunction;
		if( recMeta==null )
			lookupFunction = "lookupRecommenderSetting2";
		else
			lookupFunction = "lookupRecommenderSetting";

		settingId = (Long) sqlMap.queryForObject(lookupFunction, setting);
		if( settingId==null ) {
			log.debug("Given setting not found -> adding new");
			settingId = (Long) sqlMap.insert("addRecommenderSetting", setting);    			
			log.debug("Setting added @" + settingId);
		} else {
			log.debug("Given setting found in DB at " + settingId);
		}

		return settingId;
	}

	
	/**
	 * insert selector setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * 
	 * @return unique identifier for given settings
	 * @throws SQLException 
	 */
	private static Long insertSelectorSetting(String selectorInfo, byte[] selectorMeta) throws SQLException {
		Long selectorID = null;

		SqlMapClient sqlMap = getSqlMapInstance();

		SelectorSettingParam setting = new SelectorSettingParam();
		setting.setInfo(selectorInfo);
		setting.setMeta(selectorMeta);

		// determine which lookup sql statement we have to use.
		String lookupFunction;
		if( selectorMeta==null )
			lookupFunction = "lookupSelectorSetting2";
		else
			lookupFunction = "lookupSelectorSetting";

		selectorID = (Long) sqlMap.queryForObject(lookupFunction, setting);
		if( selectorID==null ) {
			log.debug("Given setting not found -> adding new");
			selectorID = (Long) sqlMap.insert("addSelectorSetting", setting);    			
			log.debug("Setting added @" + selectorID);
		} else {
			log.debug("Given setting found in DB at " + selectorID);
		}

		return selectorID;
	}	
	/**
	 * Store selected recommended tags.
	 * 
	 * @param qid query id
	 * @param rid result selector id
	 * @param result set of recommended tags
	 * @throws SQLException 
	 */
	public static int storeRecommendation(Long qid, Long rid, SortedSet<RecommendedTag> result) throws SQLException {
		SqlMapClient sqlMap = getSqlMapInstance();
		try {
			sqlMap.startTransaction();

			// insert recommender response
			// #qid#, #score#, #confidence#, #tagName# )
			SelectorTagParam response = new SelectorTagParam();
			response.setQid(qid);
			response.setRid(rid);
			for( RecommendedTag tag : result ) {
				response.setTagName( tag.getName() );
				response.setConfidence( tag.getConfidence() );
				response.setScore( tag.getScore() );
				sqlMap.insert("addSelectedTag", response);
			}    		

			sqlMap.commitTransaction();
		} finally {
			sqlMap.endTransaction();
		}		

		return result.size();
	}
	

	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 * @return true on success, false otherwise
	 */
	static private boolean storeBibTexPost(String userName, Long qid, Post<BibTex> post,
			String oldHash, boolean update ) {
		// TODO Auto-generated method stub
		log.warn("storeBibTexPost not implemented yet.");
		return false;
	}

	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 * @return true on success, false otherwise
	 * @throws SQLException 
	 */
	static private boolean storeBookmarkPost(String userName, Long qid, Post<Bookmark> post,
			String oldHash, boolean update ) throws SQLException {
		final BookmarkParam param = new BookmarkParam();
		param.setResource(post.getResource());
		param.setDate(post.getDate());
		param.setRequestedContentId(qid.intValue());
		param.setHash(post.getResource().getIntraHash());
		param.setDescription(post.getDescription());
		param.setUserName(post.getUser().getName());
		param.setUrl(post.getResource().getUrl());
		//in field group in table bookmark, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		if( (post.getGroups()!=null)&&(!post.getGroups().isEmpty()) ) {
			final int groupId =  post.getGroups().iterator().next().getGroupId();
			param.setGroupId(groupId);
		};
		try {
			sqlMap.startTransaction();
   			sqlMap.insert("insertBookmark", param);
    		sqlMap.commitTransaction();
		} finally {
			// all done -> close session
			sqlMap.endTransaction();
		}
		
		return false;
	}
	
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
	static public boolean logRecommendation(
			Long qid,
			Long sid,
			long latency,
			SortedSet<RecommendedTag> tags,
			SortedSet<RecommendedTag> preset
	) throws SQLException {
		// get a new session
		SqlMapClient sqlMap = getSqlMapInstance();
		try {
			sqlMap.startTransaction();

			// log each recommended tag
			RecResponseParam response = new RecResponseParam();
			response.setQid(qid);
			response.setSid(sid);
			response.setLatency(latency);
    		for( RecommendedTag tag : tags ) {
    			response.setTagName(tag.getName());
    			response.setConfidence(tag.getConfidence());
    			response.setScore(tag.getScore());
    			sqlMap.insert("addRecommenderResponse", response);
    		}

    		sqlMap.commitTransaction();
		} finally {
			// all done -> close session
			sqlMap.endTransaction();
		}
		return false;
	}


}
