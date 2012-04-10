package org.bibsonomy.recommender.tags.database;

import java.io.Reader;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.database.params.LatencyParam;
import org.bibsonomy.recommender.tags.database.params.PostGuess;
import org.bibsonomy.recommender.tags.database.params.PostParam;
import org.bibsonomy.recommender.tags.database.params.PostRecParam;
import org.bibsonomy.recommender.tags.database.params.QueryGuess;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.database.params.RecQueryParam;
import org.bibsonomy.recommender.tags.database.params.RecQuerySettingParam;
import org.bibsonomy.recommender.tags.database.params.RecResponseParam;
import org.bibsonomy.recommender.tags.database.params.RecSettingParam;
import org.bibsonomy.recommender.tags.database.params.SelectorQueryMapParam;
import org.bibsonomy.recommender.tags.database.params.SelectorSettingParam;
import org.bibsonomy.recommender.tags.database.params.SelectorTagParam;
import org.bibsonomy.recommender.tags.database.params.TasEntry;
import org.bibsonomy.recommender.tags.database.params.TasParam;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Class for encapsulating database access of recommenders. Implements {@link DBLogic}.
 * 
 * @author fei
 * @version $Id$
 */
public class DBAccess implements DBLogic {
	private static final Log log = LogFactory.getLog(DBAccess.class);
	
	//------------------------------------------------------------------------
	// database logic interface
	//------------------------------------------------------------------------
	/**
	 * Initialize iBatis layer.
	 */
	private final SqlMapClient sqlMap;
	private final SqlMapClient sqlBibMap;  // access to bibsonomy's db

	private static DBLogic instance = null;
	
	private DBAccess() {
		try {
			// initialize database client for recommender logs
			final String resource = "SqlMapConfig_recommender.xml";
			final Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database [1] connection initialized.");
			
			// initialize database client for accessing post data
			final String resource2 = "SqlMapConfig_recommenderBibDB.xml";
			final Reader reader2 = Resources.getResourceAsReader (resource2);
			sqlBibMap = SqlMapClientBuilder.buildSqlMapClient(reader2);
			log.info("Database [2] connection initialized.");			
		} catch (final Exception e) {
			throw new RuntimeException ("Error initializing DBAccess class. Cause: " + e);
		}
	}
	
	/**
	 * @return An instance of this implementation of {@link DBLogic}
	 */
	public static DBLogic getInstance() {
		if (instance == null) instance = new DBAccess();
		return instance;
	}
	

	/**
	 * @return A connection to the recommender SqlMapClient 
	 */
	public SqlMapClient getSqlMapInstance () {
		return sqlMap;
	}

	/**
	 * Get (unique) database handler for querying bibsonomy's database.
	 * @return The SqlMap which can be used to query bibsonomy's database
	 */
	private SqlMapClient getSqlBibMapInstance () {
		return sqlBibMap;
	}

	//------------------------------------------------------------------------
	// database access interface
	//------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addQuery(java.lang.String, java.sql.Timestamp, org.bibsonomy.model.Post, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Long addQuery(final String userName, final Date date, final Post<? extends Resource> post, final int postID, final int queryTimeout ) throws SQLException {
		// construct parameter
		final RecQueryParam recQuery = new RecQueryParam();
		recQuery.setTimeStamp(new Timestamp(date.getTime()));
		recQuery.setUserName(userName);
		recQuery.setPid(postID);
		recQuery.setQueryTimeout(queryTimeout);
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) )
			recQuery.setContentType(new Integer(1));
		else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) )
			recQuery.setContentType(new Integer(2));
		
		// insert recommender query
		final Long queryId = (Long) sqlMap.insert("addRecommenderQuery", recQuery);

		// store post
		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) )
			storeBookmarkPost(userName, queryId, (Post<Bookmark>)post, "", true);
		else if( BibTex.class.isAssignableFrom(post.getResource().getClass()) )
			storeBibTexPost(userName, queryId, (Post<BibTex>)post, "", true);
		
		// all done.
		return queryId;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addRecommender(java.lang.Long, java.lang.String, java.lang.String, byte[])
	 * TODO: remove call to 'insertRecommenderSetting' 
	 *       and put a corresponding foreign key constraint to the db
	 */
	@Override
	public Long addRecommender(final Long queryId, final String recId, final String recDescr, final byte[] recMeta ) throws SQLException {
		Long settingId = null;

		final SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
    		sqlMap.startTransaction();
    		
    		// insert recommender settings
    		settingId = insertRecommenderSetting(recId, recDescr, recMeta);
    		// connect query with setting
    		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
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
	 * adds given recommender (identified by it's id) to given query
	 * 
	 * @param qid query's id
	 * @param sid recommender's id
	 * @throws SQLException
	 */
	@Override
	public void addRecommenderToQuery(final Long qid, final Long sid ) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();

        	// connect query with setting
        	final RecQuerySettingParam queryMap = new RecQuerySettingParam();
        	queryMap.setQid(qid);
        	queryMap.setSid(sid);
        	sqlMap.insert("addRecommenderQuerySetting", queryMap);
	}			
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addResultSelector(java.lang.Long, java.lang.String, byte[])
	 */
	@Override
	public Long addResultSelector(final Long qid, final String selectorInfo, final byte[] selectorMeta ) throws SQLException {
		Long selectorID = null;

		final SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
        		sqlMap.startTransaction();
        		
        		// insert recommender settings
        		selectorID = insertSelectorSetting(selectorInfo, selectorMeta);
        		// connect query with setting
        		final SelectorQueryMapParam queryMap = new SelectorQueryMapParam();
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
	 * set selection strategy which was applied in given query
	 * 
	 * @param qid query's id
	 * @param rid result selector's id
	 */
	@Override
	public void setResultSelectorToQuery(final Long qid, final Long rid )  {
		// connect query with setting
		final SelectorQueryMapParam queryMap = new SelectorQueryMapParam();
		queryMap.setQid(qid);
		queryMap.setSid(rid);
		log.info("Storing selection for " + qid + " ("+rid+")");
		try {
			sqlMap.insert("addSelectorQuerySetting", queryMap);
		} catch (final SQLException ex) {
			log.error("ERROR STORING RECOMMENDER SELECTION");
		}		
	}

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addSelectedRecommender(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void addSelectedRecommender(final Long qid, final Long sid) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
        		sqlMap.startTransaction();
        		
        		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
        		queryMap.setQid(qid); 
        		queryMap.setSid(sid);
        		// insert recommender settings
        		sqlMap.insert("addQuerySelection", queryMap);
        		
        		sqlMap.commitTransaction();
        	} finally {
        		sqlMap.endTransaction();
        	}				
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addRecommendation(java.lang.Long, java.lang.Long, java.util.SortedSet, long)
	 */
	@Override
	public int addRecommendation(final Long queryId, final Long settingsId, final SortedSet<RecommendedTag> tags, final long latency ) throws SQLException {
		if (tags==null) return 0;
		
		final SqlMapClient sqlMap = getSqlMapInstance();
	   	try {
        		sqlMap.startTransaction();
        		sqlMap.startBatch();
        		// insert recommender response
        		// #qid#, #sid#, #latency#, #score#, #confidence#, #tagName# )
        		final RecResponseParam response = new RecResponseParam();
        		response.setQid(queryId);
        		response.setSid(settingsId);
    			response.setLatency(latency);
        		for( final RecommendedTag tag : tags ) {
        			response.setTagName( tag.getName() );
        			response.setConfidence( tag.getConfidence() );
        			response.setScore( tag.getScore() );
        			sqlMap.insert("addRecommenderResponse", response);
        		}    		
        		sqlMap.executeBatch();
        		sqlMap.commitTransaction();
        	} finally {
        		sqlMap.endTransaction();
        	}
		
		return tags.size();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#connectWithPost(org.bibsonomy.model.Post, int)
	 */
	@Override
	public void connectWithPost(final Post<? extends Resource> post, final int postID) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();

		final PostRecParam postMap = new PostRecParam();
		postMap.setUserName(post.getUser().getName());
		postMap.setDate(post.getDate());
		postMap.setPostID(postID);
		postMap.setHash(post.getResource().getIntraHash());

		// insert data
		sqlMap.insert("connectWithPost", postMap);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long)
	 */
	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid, final Long sid) throws SQLException {
	    final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
	    getRecommendations(qid, sid, result);
		
	    return result;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void getRecommendations(final Long qid, final Long sid, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		// TODO ugly inefficient implementation
		log.warn("Inefficient implementation");
		
		// print out newly added recommendations
		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
		queryMap.setQid(qid);
		queryMap.setSid(sid);
		final List<RecommendedTag> queryResult = sqlMap.queryForList("getRecommendationsByQidSid", queryMap);
		recommendedTags.addAll(queryResult);
	}	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long)
	 */
	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid) throws SQLException {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		getRecommendations(qid, result);
		
		return result;
	}		

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void getRecommendations(final Long qid, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		// TODO ugly inefficient implementation
		log.warn("Inefficient implementation");
		final List<RecommendedTag> queryResult = sqlMap.queryForList("getRecommendationsByQid", qid);
		recommendedTags.addAll(queryResult);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelectedTags(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RecommendedTag> getSelectedTags(final Long qid) throws SQLException {
		final List<RecommendedTag> queryResult = sqlMap.queryForList("getSelectedRecommendationsByQid", qid);
	
		return queryResult;
	}		
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelectedRecommenderIDs(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getSelectedRecommenderIDs(final Long qid) throws SQLException {
		final List<Long> queryResult = getSqlMapInstance().queryForList("getQuerySelection", qid);
	
		return queryResult;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNewestEntries(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<TasEntry> getNewestEntries(final Integer offset, final Integer range) throws SQLException {
		final TasParam param = new TasParam();
		param.setOffset(offset);
		param.setRange(range);
		final List<TasEntry> queryResult = sqlBibMap.queryForList("getNewestEntries", param);
		
		return queryResult;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getMostPopularTagsForUser(java.lang.String, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Pair<String,Integer>> getMostPopularTagsForUser(final String username, final int range) throws SQLException {
		final PostParam param = new PostParam();
		param.setUserName(username);
		param.setRange(range);
		
		return getSqlBibMapInstance().queryForList("getMostPopularTagsForUser", param);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getMostPopularTagsForResource(java.lang.Class, java.lang.String, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range) throws SQLException {
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
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTagsForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTagsForUser(final String username) throws SQLException {
		return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForUser", username);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTasForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTasForUser(final String username) throws SQLException {
		return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTasForUser", username);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTagsForResource(java.lang.Class, java.lang.String)
	 */	
	@Override
	public <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash) throws SQLException {
		if (BibTex.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForBibTeX", intraHash);
		} else if (Bookmark.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTagsForBookmark", intraHash);
		}
		throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTasForResource(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends Resource> Integer getNumberOfTasForResource(final Class<T> resourceType, final String intraHash) throws SQLException {
		if (BibTex.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTasForBibTeX", intraHash);
		} else if (Bookmark.class.equals(resourceType)) {
			return (Integer)getSqlBibMapInstance().queryForObject("getNumberOfTasForBookmark", intraHash);
		}
		throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
	}
	
	/**
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getUserIDByName(String)
	 */
	@Override
	public Integer getUserIDByName(final String userName) {
		try {
			return (Integer)getSqlBibMapInstance().queryForObject("getUserIDByName", userName);
		} catch (final SQLException ex) {
			log.error("Couldn't map user " + userName + " to the corresponding id.", ex);
			return null;
		}
	}

	/**
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getUserNameByID(int)
	 */
	@Override
	public String getUserNameByID(final int userID) {
		try {
			return (String)getSqlBibMapInstance().queryForObject("getUserNameByID", userID);
		} catch (final SQLException ex) {
			log.error("Couldn't map user id " + userID+ " to the corresponding user name.", ex);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getTagNamesForRecQuery(java.lang.Long, java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getTagNamesForRecQuery(final Long sid, final Long qid) throws SQLException {
		final RecQuerySettingParam param = new RecQuerySettingParam();
		param.setQid(qid);
		param.setSid(sid);
		return sqlMap.queryForList("getTagNamesForRecQuery", param);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getTagNamesForPost(java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getTagNamesForPost(final Integer cid) throws SQLException {
		return sqlBibMap.queryForList("getTagNamesForCID", cid);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommender(java.lang.Long)
	 */
	@Override
	public RecSettingParam getRecommender(final Long sid) throws SQLException {
		return (RecSettingParam)getSqlMapInstance().queryForObject("getRecommenderByID", sid);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getActiveRecommenderIDs(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getActiveRecommenderIDs(final Long qid) throws SQLException {
		return getSqlMapInstance().queryForList("getActiveRecommenderIDsForQuery", qid);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAllRecommenderIDs(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getAllRecommenderIDs(final Long qid) throws SQLException {
		return getSqlMapInstance().queryForList("getAllRecommenderIDsForQuery", qid);
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderSelectionCount(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Pair<Long,Long>> getRecommenderSelectionCount(final Long qid) throws SQLException {
		return getSqlMapInstance().queryForList("getRecommenderSelectionCount", qid);
	}
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAllNotSelectedRecommenderIDs(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getAllNotSelectedRecommenderIDs(final Long qid) throws SQLException {
		return getSqlMapInstance().queryForList("getAllNotSelectedRecommenderIDsForQuery", qid);
	}
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelector(java.lang.Long)
	 */
	@Override
	public SelectorSettingParam getSelector(final Long sid) throws SQLException {
		return (SelectorSettingParam)getSqlMapInstance().queryForObject("getSelectorByID", sid);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQuery(java.lang.Long)
	 */
	@Override
	public RecQueryParam getQuery(final Long qid) throws SQLException {
		return (RecQueryParam)sqlMap.queryForObject("getQueryByID", qid);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQueriesForRecommender(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RecQueryParam> getQueriesForRecommender(final Long sid) throws SQLException {
		return sqlMap.queryForList("getQueriesBySID", sid);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderAdminOverview(java.lang.String)
	 */
	@Override
	public RecAdminOverview getRecommenderAdminOverview(final String id) throws SQLException{
		return (RecAdminOverview)getSqlMapInstance().queryForObject("recAdminOverview", id);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAverageLatencyForRecommender(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Long getAverageLatencyForRecommender(final Long sid, final Long numberOfQueries) throws SQLException{
		if (numberOfQueries <= 0) return null;
		
		final LatencyParam param = new LatencyParam(sid,numberOfQueries);
		return (Long)getSqlMapInstance().queryForObject("getAverageLatencyForSettingID", param);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getLocalRecommenderSettingIds()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getLocalRecommenderSettingIds() throws SQLException{
		return getSqlMapInstance().queryForList("getSettingIdsByType", 1);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getDistantRecommenderSettingIds()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getDistantRecommenderSettingIds() throws SQLException{
		return getSqlMapInstance().queryForList("getSettingIdsByType", 0);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getActiveRecommenderSettingIds()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getActiveRecommenderSettingIds() throws SQLException{
		return getSqlMapInstance().queryForList("getSettingIdsByStatus", 1);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getDisabledRecommenderSettingIds()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getDisabledRecommenderSettingIds() throws SQLException{
		return getSqlMapInstance().queryForList("getSettingIdsByStatus", 0);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderIdsForSettingIds(java.util.List)
	 */
	@Override
	public Map<Long, String> getRecommenderIdsForSettingIds(final List<Long> sids) throws SQLException{
		final Map<Long, String> resultmap = new TreeMap<Long,String>();
		for (final Long sid: sids) {
			resultmap.put(sid, this.getRecommender(sid).getRecId());
		}
		return resultmap;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#updateRecommenderstatus(java.util.List, java.util.List)
	 */
	@Override
	public void updateRecommenderstatus(final List<Long> activeRecs, final List<Long> disabledRecs ) throws SQLException{
		final SqlMapClient sqlMap = getSqlMapInstance();
		
		if (activeRecs != null) {
		    for(final Long p: activeRecs) {
			sqlMap.update("activateRecommender", p);
		    }
		}
		 
		
		if (disabledRecs != null) {
		    for(final Long p: disabledRecs) {
			sqlMap.update("deactivateRecommender", p);
		    }
		}
		  
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#removeRecommender(java.lang.String)
	 */
	@Override
	public void removeRecommender(final String url) throws SQLException{
		final SqlMapClient sqlMap = getSqlMapInstance();
		sqlMap.insert("unlinkRecommender", url);
		sqlMap.delete("deleteFromRecommenderStatus", url);
		sqlMap.delete("deleteFromRecommenderSettings", url);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#updateRecommenderUrl(java.lang.Long, java.net.URL)
	 */
	@Override
	public void updateRecommenderUrl(final long sid, final URL url) throws SQLException{
		final RecSettingParam param = new RecSettingParam();
		param.setRecId(url.toString());
		param.setSetting_id(sid);
		param.setRecMeta(url.toString().getBytes());
		
		final SqlMapClient sqlMap = getSqlMapInstance();
		
		final Long settingId = (Long) sqlMap.queryForObject("lookupRecommenderSetting", param);
		if (settingId != null && settingId != sid) {
		    throw new SQLException("Cannot edit recommender-url because recommender-Setting with url '" + url.toString() + "' already exists!");
		}
		
		sqlMap.update("updateRecommenderStatusUrl", param);
		sqlMap.update("updateRecommenderSettingUrl", param);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#guessQueryFromPost(java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Long guessQueryFromPost(final Integer content_id) throws SQLException {
		Long result  = null;
		TasEntry tas = null;
		
		// get post's timestamp
		final List<?> queryresult = sqlBibMap.queryForList("getTasEntryForID", content_id);
		if( queryresult.size()>0 ) {
			tas = (TasEntry)(sqlBibMap.queryForList("getTasEntryForID", content_id).get(0));
			// get nearest recommender queries
			final PostParam param = new PostParam();
			param.setTimestamp(tas.getTimeStamp());
			param.setUserName(tas.getUserName());
			final List<QueryGuess> qids = sqlMap.queryForList("getNearestQueriesForPost", param);
			// select first one if exists
			if( (qids.size()>0)&&(qids.get(0).getDiff()<300) )
				result = qids.get(0).getQid();			
		}
		
		// all done.
		return result; 
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#guessPostFromQuery(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Integer guessPostFromQuery(final Long query_id) throws SQLException {
		Integer result  = null;
		
		// get query information
		final RecQueryParam recQuery = (RecQueryParam)sqlMap.queryForObject("getQueryByID", query_id);
		
		if( recQuery != null ) {
			// look for nearest post
			final PostParam param = new PostParam();
			param.setTimestamp(recQuery.getTimeStamp());
			param.setUserName(recQuery.getUserName());
			final List<PostGuess> cids = sqlBibMap.queryForList("getNearestPostsForQuery", param);
			// select first one if exists
			// FIXME: think of something usefull
			if( (cids.size()>0)&&(cids.get(0).getDiff()<300) )
				result = cids.get(0).getContentID();
		} else 
			log.info("Content ID for query " + query_id + " not found.");
		
		// all done.
		return result; 		
	}
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQueryForPost(java.lang.String, java.util.Date, java.lang.Integer)
	 */
	@Override
	public Long getQueryForPost(final String user_name, final Date date, final Integer postID) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();
		
		final PostRecParam postParam = new PostRecParam();
		postParam.setDate(date);
		postParam.setUserName(user_name);
		postParam.setPostID(postID);
		
		log.debug("Looking up queryID for " + user_name + ", " + date + ", " + postID);
		//log.debug("HERE: " + (Long) sqlMap.queryForObject("getQueryForPost", postParam));
		return (Long) sqlMap.queryForObject("getQueryForPost", postParam);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getContentIDForQuery(java.lang.Long)
	 */
	@Override
	public Integer getContentIDForQuery(final Long queryID) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();
		final SqlMapClient bibMap = getSqlBibMapInstance();
		Integer retVal = null;
		
		// first get hash, username and post date
		final PostRecParam postParam = (PostRecParam)sqlMap.queryForObject("lookupPostForQuery", queryID);
		
		// now get content_id
		if( postParam.getContentType()==1 ) {
			// look for new entry
			retVal = (Integer)bibMap.queryForObject("lookupBookmarkContentID-1", postParam);
			if( retVal==null )
				// look for logged entry
				retVal = (Integer)bibMap.queryForObject("lookupBookmarkContentID-2", postParam);
		} else if (postParam.getContentType()==2) {
			log.error("BIBTEX LOOKUP NOT TESTED");
			// look for new entry
			retVal = (Integer)bibMap.queryForObject("lookupBibTeXContentID-1", postParam);
			if( retVal==null )
				// look for logged entry
				retVal = (Integer)bibMap.queryForObject("lookupBibTeXContentID-2", postParam);
		} else
			log.error("INVALID POST: CONTENT_TYPE");
		
		// all done.
		return retVal;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getContentIDForQuery(java.lang.String, java.util.Date, java.lang.Integer)
	 */
	@Override
	public Integer getContentIDForQuery(final String userName, final Date date, final Integer postID) {
		log.error("NOT IMPLEMENTED");
		return null;
	}
	
	/**
	 * insert recommender setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * 
	 * @return unique identifier for given settings
	 */
	@Override
	public Long insertRecommenderSetting(final String recId, final String recDescr, final byte[] recMeta) throws SQLException {
		Long settingId = null;

		final SqlMapClient sqlMap = getSqlMapInstance();

		final RecSettingParam setting = new RecSettingParam();
		setting.setRecId(recId);
		setting.setRecMeta(recMeta);
		setting.setRecDescr(recDescr);

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
			setting.setSetting_id(settingId);
			
			if (setting.getRecMeta() == null) {
			    sqlMap.insert("createStatusForNewLocalRecommender", setting);
			} else sqlMap.insert("createStatusForNewDistantRecommender", setting);
			
			log.debug("Setting and status added @" + settingId);
		} else {
			log.debug("Given setting found in DB at " + settingId +" -> setting status to 'active'");
			sqlMap.update("activateRecommender", settingId);
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
	@Override
	public Long insertSelectorSetting(final String selectorInfo, final byte[] selectorMeta) throws SQLException {
		Long selectorID = null;

		final SqlMapClient sqlMap = getSqlMapInstance();

		final SelectorSettingParam setting = new SelectorSettingParam();
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#storeRecommendation(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public int storeRecommendation(final Long qid, final Long rid, final Collection<RecommendedTag> result) throws SQLException {
		final SqlMapClient sqlMap = getSqlMapInstance();
		try {
			sqlMap.startTransaction();
			sqlMap.startBatch();
			
			// set store applied selection strategie's id
			setResultSelectorToQuery(qid, rid);
			
			// insert recommender response
			// #qid#, #score#, #confidence#, #tagName# )
			final SelectorTagParam response = new SelectorTagParam();
			response.setQid(qid);
			response.setRid(rid);
			for( final RecommendedTag tag : result ) {
				response.setTagName( tag.getName() );
				response.setConfidence( tag.getConfidence() );
				response.setScore( tag.getScore() );
				sqlMap.insert("addSelectedTag", response);
			}    		
			sqlMap.executeBatch();
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
	 * @throws SQLException 
	 */
	private boolean storeBibTexPost(final String userName, final Long qid, final Post<BibTex> post, final String oldHash, final boolean update ) throws SQLException {
		// TODO Auto-generated method stub
		log.warn("storeBibTexPost not tested.");
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(qid.intValue());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
		final SqlMapClient sqlMap = getSqlMapInstance();
		try {
			sqlMap.startTransaction();
   			sqlMap.insert("insertBibTex", param);
    		sqlMap.commitTransaction();
		} finally {
			// all done -> close session
			sqlMap.endTransaction();
		}
		
		// all done.
		return true;
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
	private boolean storeBookmarkPost(final String userName, final Long qid, final Post<Bookmark> post, final String oldHash, final boolean update ) throws SQLException {
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
		}
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
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#logRecommendation(java.lang.Long, java.lang.Long, long, java.util.SortedSet, java.util.SortedSet)
	 */
	@Override
	public boolean logRecommendation(final Long qid, final Long sid, final long latency, final SortedSet<RecommendedTag> tags, final SortedSet<RecommendedTag> preset) throws SQLException {
		// get a new session
		final SqlMapClient sqlMap = getSqlMapInstance();
		try {
			sqlMap.startTransaction();

			// log each recommended tag
			final RecResponseParam response = new RecResponseParam();
			response.setQid(qid);
			response.setSid(sid);
			response.setLatency(latency);
    		for( final RecommendedTag tag : tags ) {
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
