package org.bibsonomy.recommender.tags.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
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

/**
 * Class for encapsulating database access of recommenders. Implements {@link DBLogic}.
 * 
 * @author fei
 * @version $Id$
 */
public class DBAccess extends AbstractDatabaseManager implements DBLogic {
	private static final Log log = LogFactory.getLog(DBAccess.class);
	
	private DBSessionFactory recommenderFactory;
	private DBSessionFactory mainFactory;
	
	private DBSession openRecommenderSession() {
		return this.recommenderFactory.getDatabaseSession();
	}
	
	private DBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}

	//------------------------------------------------------------------------
	// database access interface
	//------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addQuery(java.lang.String, java.sql.Timestamp, org.bibsonomy.model.Post, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Long addQuery(final String userName, final Date date, final Post<? extends Resource> post, final int postID, final int queryTimeout) {
		// construct parameter
		final RecQueryParam recQuery = new RecQueryParam();
		recQuery.setTimeStamp(new Timestamp(date.getTime()));
		recQuery.setUserName(userName);
		recQuery.setPid(postID);
		recQuery.setQueryTimeout(queryTimeout);
		
		// TODO: use ContentTypeTypeHandlerCallback
		if (Bookmark.class.isAssignableFrom(post.getResource().getClass())) {
			recQuery.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE.getId());
		} else if (BibTex.class.isAssignableFrom(post.getResource().getClass())) {
			recQuery.setContentType(ConstantID.BIBTEX_CONTENT_TYPE.getId());
		}
		
		// insert recommender query
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final Long queryId = (Long) recommenderSession.insert("addRecommenderQuery", recQuery);

			// store post
			if (Bookmark.class.isAssignableFrom(post.getResource().getClass())) {
				this.storeBookmarkPost(userName, queryId, (Post<Bookmark>) post, "", true, recommenderSession);
			} else if (BibTex.class.isAssignableFrom(post.getResource().getClass())) {
				this.storeBibTexPost(userName, queryId, (Post<BibTex>) post, "", true, recommenderSession);
			}
			return queryId;
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addRecommender(java.lang.Long, java.lang.String, java.lang.String, byte[])
	 * TODO: remove call to 'insertRecommenderSetting' 
	 *       and put a corresponding foreign key constraint to the db
	 */
	@Override
	public Long addRecommender(final Long queryId, final String recId, final String recDescr, final byte[] recMeta) {
		Long settingId = null;
		
		final DBSession recommenderSession = this.openRecommenderSession();
	   	try {
	   		recommenderSession.beginTransaction();
    		// insert recommender settings
    		settingId = this.insertRecommenderSetting(recId, recDescr, recMeta);
    		// connect query with setting
    		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
    		queryMap.setQid(queryId);
    		queryMap.setSid(settingId);
    		recommenderSession.insert("addRecommenderQuerySetting", queryMap);
    		recommenderSession.commitTransaction();
    	} finally {
    		recommenderSession.endTransaction();
    	}
		return settingId;
	}

	/**
	 * adds given recommender (identified by it's id) to given query
	 * 
	 * @param qid query's id
	 * @param sid recommender's id
	 */
	@Override
	public void addRecommenderToQuery(final Long qid, final Long sid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			// connect query with setting
	        final RecQuerySettingParam queryMap = new RecQuerySettingParam();
	        queryMap.setQid(qid);
	        queryMap.setSid(sid);
	        recommenderSession.insert("addRecommenderQuerySetting", queryMap);
		} finally {
			recommenderSession.close();
		}
	}			
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addResultSelector(java.lang.Long, java.lang.String, byte[])
	 */
	@Override
	public Long addResultSelector(final Long qid, final String selectorInfo, final byte[] selectorMeta) {
		final DBSession recommenderSession = this.openRecommenderSession();
	   	try {
	   		recommenderSession.beginTransaction();
    		// insert recommender settings
	   		final Long selectorID = this.insertSelectorSetting(selectorInfo, selectorMeta, recommenderSession);
    		// connect query with setting
    		final SelectorQueryMapParam queryMap = new SelectorQueryMapParam();
    		queryMap.setQid(qid);
    		queryMap.setSid(selectorID);
    		recommenderSession.insert("addSelectorQuerySetting", queryMap);
    		
    		recommenderSession.commitTransaction();
    		return selectorID;
    	} finally {
    		recommenderSession.endTransaction();
    	}
	}

	/**
	 * set selection strategy which was applied in given query
	 * 
	 * @param qid query's id
	 * @param rid result selector's id
	 */
	@Override
	public void setResultSelectorToQuery(final Long qid, final Long rid ){
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			// connect query with setting
			final SelectorQueryMapParam queryMap = new SelectorQueryMapParam();
			queryMap.setQid(qid);
			queryMap.setSid(rid);
			log.info("Storing selection for " + qid + " (" + rid + ")");
			recommenderSession.insert("addSelectorQuerySetting", queryMap);	
		} finally {
			recommenderSession.close();
		}
	}

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addSelectedRecommender(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void addSelectedRecommender(final Long qid, final Long sid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();
    		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
    		queryMap.setQid(qid); 
    		queryMap.setSid(sid);
    		
    		// insert recommender settings
    		recommenderSession.insert("addQuerySelection", queryMap);
    		recommenderSession.commitTransaction();
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}			
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#addRecommendation(java.lang.Long, java.lang.Long, java.util.SortedSet, long)
	 */
	@Override
	public int addRecommendation(final Long queryId, final Long settingsId, final SortedSet<RecommendedTag> tags, final long latency) {
		if (tags == null) {
			return 0;
		}
		
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();
    		recommenderSession.startBatch();
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
    			recommenderSession.insert("addRecommenderResponse", response);
    		}    		
    		recommenderSession.executeBatch();
    		recommenderSession.commitTransaction();
    		return tags.size();
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#connectWithPost(org.bibsonomy.model.Post, int)
	 */
	@Override
	public void connectWithPost(final Post<? extends Resource> post, final int postID) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final PostRecParam postMap = new PostRecParam();
			postMap.setUserName(post.getUser().getName());
			postMap.setDate(post.getDate());
			postMap.setPostID(postID);
			postMap.setHash(post.getResource().getIntraHash());

			// insert data
			recommenderSession.insert("connectWithPost", postMap);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long)
	 */
	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid, final Long sid) {
	    final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
	    this.getRecommendations(qid, sid, result);
		
	    return result;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(final Long qid, final Long sid, final Collection<RecommendedTag> recommendedTags) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			// TODO ugly inefficient implementation
			log.warn("Inefficient implementation");
			
			// print out newly added recommendations
			final RecQuerySettingParam queryMap = new RecQuerySettingParam();
			queryMap.setQid(qid);
			queryMap.setSid(sid);
			final List<RecommendedTag> queryResult = this.queryForList("getRecommendationsByQidSid", queryMap, RecommendedTag.class, recommenderSession);
			recommendedTags.addAll(queryResult);
		} finally {
			recommenderSession.close();
		}		
	}	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long)
	 */
	@Override
	public SortedSet<RecommendedTag> getRecommendations(final Long qid) {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		this.getRecommendations(qid, result);
		return result;
	}		

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommendations(java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(final Long qid, final Collection<RecommendedTag> recommendedTags) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			// TODO ugly inefficient implementation
			log.warn("Inefficient implementation");
			final List<RecommendedTag> queryResult = this.queryForList("getRecommendationsByQid", qid, RecommendedTag.class, recommenderSession);
			recommendedTags.addAll(queryResult);
		} finally {
			recommenderSession.close();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelectedTags(java.lang.Long)
	 */
	@Override
	public List<RecommendedTag> getSelectedTags(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSelectedRecommendationsByQid", qid, RecommendedTag.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}		
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelectedRecommenderIDs(java.lang.Long)
	 */
	@Override
	public List<Long> getSelectedRecommenderIDs(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getQuerySelection", qid, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNewestEntries(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<TasEntry> getNewestEntries(final Integer offset, final Integer range) {
		final DBSession mainSession = this.openMainSession();
		try {
			final TasParam param = new TasParam();
			param.setOffset(offset);
			param.setRange(range);
			return this.queryForList("getNewestEntries", param, TasEntry.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getMostPopularTagsForUser(java.lang.String, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Pair<String,Integer>> getMostPopularTagsForUser(final String username, final int range) {
		final DBSession mainSession = this.openMainSession();
		try {
			final PostParam param = new PostParam();
			param.setUserName(username);
			param.setRange(range);
			
			return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForUser", param, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getMostPopularTagsForResource(java.lang.Class, java.lang.String, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Pair<String, Integer>> getMostPopularTagsForResource(final Class<T> resourceType, final String intraHash, final int range) {
		final DBSession mainSession = this.openMainSession();
		try {
			final PostParam param = new PostParam();
			param.setIntraHash(intraHash);
			param.setRange(range);
			
			if (BibTex.class.equals(resourceType)) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBibTeX", param, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return (List<Pair<String, Integer>>) this.queryForList("getMostPopularTagsForBookmark", param, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTagsForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTagsForUser(final String username) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTagsForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTasForUser(java.lang.String)
	 */
	@Override
	public Integer getNumberOfTasForUser(final String username) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getNumberOfTasForUser", username, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTagsForResource(java.lang.Class, java.lang.String)
	 */	
	@Override
	public <T extends Resource> Integer getNumberOfTagsForResource(final Class<T> resourceType, final String intraHash) {
		final DBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBibTeX", intraHash, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTagsForBookmark", intraHash, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getNumberOfTasForResource(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends Resource> Integer getNumberOfTasForResource(final Class<T> resourceType, final String intraHash) {
		final DBSession mainSession = this.openMainSession();
		try {
			if (BibTex.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBibTeX", intraHash, Integer.class, mainSession);
			} else if (Bookmark.class.equals(resourceType)) {
				return this.queryForObject("getNumberOfTasForBookmark", intraHash, Integer.class, mainSession);
			}
			throw new UnsupportedResourceTypeException("Unknown resource type " + resourceType);
		} finally {
			mainSession.close();
		}
	}
	
	/**
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getUserIDByName(String)
	 */
	@Override
	public Integer getUserIDByName(final String userName) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserIDByName", userName, Integer.class, mainSession);
		} finally {
			mainSession.close();
		}
	}

	/**
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getUserNameByID(int)
	 */
	@Override
	public String getUserNameByID(final int userID) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserNameByID", userID, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getTagNamesForRecQuery(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<String> getTagNamesForRecQuery(final Long sid, final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final RecQuerySettingParam param = new RecQuerySettingParam();
			param.setQid(qid);
			param.setSid(sid);
			return this.queryForList("getTagNamesForRecQuery", param, String.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getTagNamesForPost(java.lang.Integer)
	 */
	@Override
	public List<String> getTagNamesForPost(final Integer cid) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForList("getTagNamesForCID", cid, String.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommender(java.lang.Long)
	 */
	@Override
	public RecSettingParam getRecommender(final Long sid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForObject("getRecommenderByID", sid, RecSettingParam.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getActiveRecommenderIDs(java.lang.Long)
	 */
	@Override
	public List<Long> getActiveRecommenderIDs(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getActiveRecommenderIDsForQuery", qid, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAllRecommenderIDs(java.lang.Long)
	 */
	@Override
	public List<Long> getAllRecommenderIDs(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getAllRecommenderIDsForQuery", qid, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderSelectionCount(java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Pair<Long,Long>> getRecommenderSelectionCount(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return (List<Pair<Long, Long>>) this.queryForList("getRecommenderSelectionCount", qid, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAllNotSelectedRecommenderIDs(java.lang.Long)
	 */
	@Override
	public List<Long> getAllNotSelectedRecommenderIDs(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getAllNotSelectedRecommenderIDsForQuery", qid, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getSelector(java.lang.Long)
	 */
	@Override
	public SelectorSettingParam getSelector(final Long sid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForObject("getSelectorByID", sid, SelectorSettingParam.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQuery(java.lang.Long)
	 */
	@Override
	public RecQueryParam getQuery(final Long qid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForObject("getQueryByID", qid, RecQueryParam.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQueriesForRecommender(java.lang.Long)
	 */
	@Override
	public List<RecQueryParam> getQueriesForRecommender(final Long sid) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getQueriesBySID", sid, RecQueryParam.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderAdminOverview(java.lang.String)
	 */
	@Override
	public RecAdminOverview getRecommenderAdminOverview(final String id) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForObject("recAdminOverview", id, RecAdminOverview.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getAverageLatencyForRecommender(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Long getAverageLatencyForRecommender(final Long sid, final Long numberOfQueries) {
		if (numberOfQueries <= 0) {
			return null;
		}
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final LatencyParam param = new LatencyParam(sid, numberOfQueries);
			return this.queryForObject("getAverageLatencyForSettingID", param, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getLocalRecommenderSettingIds()
	 */
	@Override
	public List<Long> getLocalRecommenderSettingIds() {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSettingIdsByType", 1, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getDistantRecommenderSettingIds()
	 */
	@Override
	public List<Long> getDistantRecommenderSettingIds() {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSettingIdsByType", 0, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getActiveRecommenderSettingIds()
	 */
	@Override
	public List<Long> getActiveRecommenderSettingIds() {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSettingIdsByStatus", 1, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getDisabledRecommenderSettingIds()
	 */
	@Override
	public List<Long> getDisabledRecommenderSettingIds() {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSettingIdsByStatus", 0, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getRecommenderIdsForSettingIds(java.util.List)
	 */
	@Override
	public Map<Long, String> getRecommenderIdsForSettingIds(final List<Long> sids) {
		final Map<Long, String> resultmap = new TreeMap<Long, String>();
		for (final Long sid: sids) {
			resultmap.put(sid, this.getRecommender(sid).getRecId());
		}
		return resultmap;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#updateRecommenderstatus(java.util.List, java.util.List)
	 */
	@Override
	public void updateRecommenderstatus(final List<Long> activeRecs, final List<Long> disabledRecs ) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			if (activeRecs != null) {
			    for (final Long p: activeRecs) {
			    	this.update("activateRecommender", p, recommenderSession);
			    }
			}
			
			if (disabledRecs != null) {
			    for(final Long p: disabledRecs) {
			    	this.update("deactivateRecommender", p, recommenderSession);
			    }
			}
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#removeRecommender(java.lang.String)
	 */
	@Override
	public void removeRecommender(final String url) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			this.insert("unlinkRecommender", url, recommenderSession);
			this.delete("deleteFromRecommenderStatus", url, recommenderSession);
			this.delete("deleteFromRecommenderSettings", url, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#updateRecommenderUrl(java.lang.Long, java.net.URL)
	 */
	@Override
	public void updateRecommenderUrl(final long sid, final URL url) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final RecSettingParam param = new RecSettingParam();
			param.setRecId(url.toString());
			param.setSetting_id(sid);
			param.setRecMeta(url.toString().getBytes());
			
			final Long settingId = this.queryForObject("lookupRecommenderSetting", param, Long.class, recommenderSession);
			if ((settingId != null) && (settingId != sid)) {
			    throw new RuntimeException("Cannot edit recommender-url because recommender-Setting with url '" + url.toString() + "' already exists!");
			}
			
			this.update("updateRecommenderStatusUrl", param, recommenderSession);
			this.update("updateRecommenderSettingUrl", param, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* 
	 * FIXME: remove unused?
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#guessQueryFromPost(java.lang.Integer)
	 */
	@Override
	public Long guessQueryFromPost(final Integer content_id) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			Long result  = null;
			TasEntry tas = null;
			
			// get post's timestamp
			final List<?> queryresult = this.queryForList("getTasEntryForID", content_id, recommenderSession);
			if (queryresult.size() > 0) {
				tas = this.queryForList("getTasEntryForID", content_id, TasEntry.class, recommenderSession).get(0);
				// get nearest recommender queries
				final PostParam param = new PostParam();
				param.setTimestamp(tas.getTimeStamp());
				param.setUserName(tas.getUserName());
				final List<QueryGuess> qids = this.queryForList("getNearestQueriesForPost", param, QueryGuess.class, recommenderSession);
				// select first one if exists
				if ((qids.size() > 0) && (qids.get(0).getDiff() < 300)) {
					result = qids.get(0).getQid();
				}			
			}
			return result;
		} finally {
			recommenderSession.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#guessPostFromQuery(java.lang.Long)
	 */
	@Override
	public Integer guessPostFromQuery(final Long query_id) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			Integer result  = null;
			
			// get query information
			final RecQueryParam recQuery = this.queryForObject("getQueryByID", query_id, RecQueryParam.class, recommenderSession);
			
			if (recQuery != null) {
				// look for nearest post
				final PostParam param = new PostParam();
				param.setTimestamp(recQuery.getTimeStamp());
				param.setUserName(recQuery.getUserName());
				final List<PostGuess> cids = this.queryForList("getNearestPostsForQuery", param, PostGuess.class, recommenderSession);
				// select first one if exists
				// FIXME: think of something usefull
				if ((cids.size() > 0) && (cids.get(0).getDiff() < 300)) {
					result = cids.get(0).getContentID();
				}
			} else {
				log.info("Content ID for query " + query_id + " not found.");
			}
			return result; 
		} finally {
			recommenderSession.close();
		}	
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getQueryForPost(java.lang.String, java.util.Date, java.lang.Integer)
	 */
	@Override
	public Long getQueryForPost(final String user_name, final Date date, final Integer postID) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			final PostRecParam postParam = new PostRecParam();
			postParam.setDate(date);
			postParam.setUserName(user_name);
			postParam.setPostID(postID);
			
			log.debug("Looking up queryID for " + user_name + ", " + date + ", " + postID);
			return this.queryForObject("getQueryForPost", postParam, Long.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#getContentIDForQuery(java.lang.Long)
	 */
	@Override
	public Integer getContentIDForQuery(final Long queryID) {
		final DBSession recommenderSession = this.openRecommenderSession();
		final DBSession mainSession = this.openMainSession();
		try {
			Integer retVal = null;
			
			// first get hash, username and post date
			final PostRecParam postParam = this.queryForObject("lookupPostForQuery", queryID, PostRecParam.class, recommenderSession);
			
			// now get content_id
			if (postParam.getContentType()== ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
				// look for new entry
				retVal = this.queryForObject("lookupBookmarkContentID-1", postParam, Integer.class, mainSession);
				if (retVal == null) {
					// look for logged entry
					retVal = this.queryForObject("lookupBookmarkContentID-2", postParam, Integer.class, mainSession);
				}
			} else if (postParam.getContentType()==2) {
				log.error("BIBTEX LOOKUP NOT TESTED");
				// look for new entry
				retVal = this.queryForObject("lookupBibTeXContentID-1", postParam, Integer.class, mainSession);
				if (retVal == null) {
					// look for logged entry
					retVal = this.queryForObject("lookupBibTeXContentID-2", postParam, Integer.class, mainSession);
				}
			} else {
				log.error("INVALID POST: CONTENT_TYPE");
			}
			return retVal;
		} finally {
			recommenderSession.close();
			mainSession.close();
		}
	}
	
	/**
	 * insert recommender setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * 
	 * @return unique identifier for given settings
	 */
	@Override
	public Long insertRecommenderSetting(final String recId, final String recDescr, final byte[] recMeta) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			Long settingId = null;

			final RecSettingParam setting = new RecSettingParam();
			setting.setRecId(recId);
			setting.setRecMeta(recMeta);
			setting.setRecDescr(recDescr);

			// determine which lookup sql statement we have to use.
			String lookupFunction;
			if (recMeta == null) {
				lookupFunction = "lookupRecommenderSetting2";
			} else {
				lookupFunction = "lookupRecommenderSetting";
			}

			settingId = this.queryForObject(lookupFunction, setting, Long.class, recommenderSession);
			if (settingId == null) {
				log.debug("Given setting not found -> adding new");
				settingId = (Long) this.insert("addRecommenderSetting", setting, recommenderSession);
				setting.setSetting_id(settingId);
				
				if (setting.getRecMeta() == null) {
				    this.insert("createStatusForNewLocalRecommender", setting, recommenderSession);
				} else {
					this.insert("createStatusForNewDistantRecommender", setting, recommenderSession);
				}
				
				log.debug("Setting and status added @" + settingId);
			} else {
				log.debug("Given setting found in DB at " + settingId +" -> setting status to 'active'");
				this.update("activateRecommender", settingId, recommenderSession);
			}

			return settingId;
		} finally {
			recommenderSession.close();
		}
	}

	/**
	 * insert selector setting into table - if given setting already exists,
	 * return its id. This should always be embedded into a transaction.
	 * 
	 * @return unique identifier for given settings
	 */
	@Override
	public Long insertSelectorSetting(final String selectorInfo, final byte[] selectorMeta) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.insertSelectorSetting(selectorInfo, selectorMeta, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}

	private Long insertSelectorSetting(final String selectorInfo, final byte[] selectorMeta, final DBSession session) {
		Long selectorID = null;

		final SelectorSettingParam setting = new SelectorSettingParam();
		setting.setInfo(selectorInfo);
		setting.setMeta(selectorMeta);

		// determine which lookup sql statement we have to use.
		String lookupFunction;
		if (selectorMeta == null) {
			lookupFunction = "lookupSelectorSetting2";
		} else {
			lookupFunction = "lookupSelectorSetting";
		}

		selectorID = this.queryForObject(lookupFunction, setting, Long.class, session);
		if (selectorID == null) {
			log.debug("Given setting not found -> adding new");
			selectorID = (Long) this.insert("addSelectorSetting", setting, session);    			
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
	public int storeRecommendation(final Long qid, final Long rid, final Collection<RecommendedTag> result) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();
			recommenderSession.startBatch();
			
			// set store applied selection strategie's id
			this.setResultSelectorToQuery(qid, rid);
			
			// insert recommender response
			// #qid#, #score#, #confidence#, #tagName# )
			final SelectorTagParam response = new SelectorTagParam();
			response.setQid(qid);
			response.setRid(rid);
			for( final RecommendedTag tag : result ) {
				response.setTagName( tag.getName() );
				response.setConfidence( tag.getConfidence() );
				response.setScore( tag.getScore() );
				this.insert("addSelectedTag", response, recommenderSession);
			}    		
			recommenderSession.executeBatch();
			recommenderSession.commitTransaction();

			return result.size();
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}

	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 */
	private void storeBibTexPost(final String userName, final Long qid, final Post<BibTex> post, final String oldHash, final boolean update, final DBSession session) {
		log.warn("storeBibTexPost not tested.");
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(qid.intValue());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
		try {
			session.beginTransaction();
			session.insert("insertBibTex", param);
			session.commitTransaction();
		} finally {
			// all done -> close session
			session.endTransaction();
		}
	}

	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 */
	private void storeBookmarkPost(final String userName, final Long qid, final Post<Bookmark> post, final String oldHash, final boolean update, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setResource(post.getResource());
		param.setDate(post.getDate());
		param.setRequestedContentId(qid.intValue());
		param.setHash(post.getResource().getIntraHash());
		param.setDescription(post.getDescription());
		param.setUserName(post.getUser().getName());
		param.setUrl(post.getResource().getUrl());
		// TODO: adapt to multiple groups?
		// in field group in table bookmark, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		final Set<Group> groups = post.getGroups();
		if (present(groups)) {
			final int groupId =  groups.iterator().next().getGroupId();
			param.setGroupId(groupId);
		}
		
		try {
			session.beginTransaction();
			session.insert("insertBookmark", param);
			session.commitTransaction();
		} finally {
			// all done -> close session
			session.endTransaction();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.tags.database.DBLogic#logRecommendation(java.lang.Long, java.lang.Long, long, java.util.SortedSet, java.util.SortedSet)
	 */
	@Override
	public boolean logRecommendation(final Long qid, final Long sid, final long latency, final SortedSet<RecommendedTag> tags, final SortedSet<RecommendedTag> preset) {
		final DBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();

			// log each recommended tag
			final RecResponseParam response = new RecResponseParam();
			response.setQid(qid);
			response.setSid(sid);
			response.setLatency(latency);
    		for (final RecommendedTag tag : tags) {
    			response.setTagName(tag.getName());
    			response.setConfidence(tag.getConfidence());
    			response.setScore(tag.getScore());
    			this.insert("addRecommenderResponse", response, recommenderSession);
    		}

    		recommenderSession.commitTransaction();
    		return false;
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}

	/**
	 * @param recommenderFactory the recommenderFactory to set
	 */
	public void setRecommenderFactory(final DBSessionFactory recommenderFactory) {
		this.recommenderFactory = recommenderFactory;
	}

	/**
	 * @param mainFactory the mainFactory to set
	 */
	public void setMainFactory(final DBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}
}
