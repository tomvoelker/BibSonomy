package org.bibsonomy.lucene.database;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.params.BookmarkParam;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.lucene.database.results.Pair;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 *
 */
public class LuceneBookmarkLogic extends LuceneDBLogic<Bookmark> {
	private final Log log = LogFactory.getLog(LuceneBookmarkLogic.class);

	/** singleton pattern's instance reference */
	protected static LuceneDBLogic<Bookmark> instance = null;
	
	/**
	 * constructor disabled for enforcing singleton pattern 
	 */
	private LuceneBookmarkLogic() {
		super();
	}
	
	/**
	 * @return An instance of this implementation of {@link LuceneDBInterface}
	 */
	public static LuceneDBInterface<Bookmark> getInstance() {
		if (instance == null) instance = new LuceneBookmarkLogic();
		return instance;
	}
	
	//------------------------------------------------------------------------
	// db interface implementation
	//------------------------------------------------------------------------

	
	//------------------------------------------------------------------------
	// abstract LuceneDBLogic interface implemetation
	//------------------------------------------------------------------------
	@Override
	protected ResourcesParam<Bookmark> getResourcesParam() {
		return new BookmarkParam();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<LucenePost<Bookmark>> getPostsForUserInternal(ResourcesParam<Bookmark> param) {
		List<LucenePost<Bookmark>> retVal = null;
		try {
			retVal = this.sqlMap.queryForList("getBookmarkForUser", param);
		} catch (SQLException e) {
			log.error("Error fetching bookmarks for user " + param.getUserName(), e);
		}

		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Post<Bookmark>> getUpdatedPostsForTimeRange(ResourcesParam<Bookmark> param) throws SQLException {
		List<Post<Bookmark>> retVal = null;
		retVal = this.sqlMap.queryForList("getUpdatedBookmarkPostsForTimeRange", param);
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Integer> getContentIdsToDeleteInternal(Pair<Date, Date> param) throws SQLException {
		return this.sqlMap.queryForList("getBookmarkContentIdsToDelete", param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getContentIdsToDeleteInternal(Date lastLogDate) throws SQLException {
		return this.sqlMap.queryForList("getBookmarkContentIdsToDelete2", lastLogDate);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<HashMap<String, Object>> getPostsForTimeRange(Date fromDate, Date toDate) {
		Pair<Date,Date> param = new Pair<Date,Date>();
		param.setFirst(fromDate);
		param.setSecond(toDate);
		
		List<HashMap<String,Object>> retVal = null;
		try {
			retVal = this.sqlMap.queryForList("getBookmarkPostsForTimeRange", param);
		} catch (SQLException e) {
			log.error("Error fetching publications for given time range", e);
		}
		
		log.debug("retrieveRecordsFromDatabase: " + ((retVal == null) ? -1 : retVal.size()));
		return retVal;
	}

	//------------------------------------------------------------------------
	// methods for building the index
	// TODO: maybe we should introduce a special class hierarchy
	//------------------------------------------------------------------------
	@Override
	public Date getLastLogDate() {
		Date retVal = null;
		try {
			retVal = (Date)sqlMap.queryForObject("getLastLogBookmark");
		} catch (SQLException e) {
			log.error("Error determining last log date.", e);
		}
		if( retVal==null )
			return new Date(System.currentTimeMillis());
		else
			return retVal;
	}

	@Override
	public int getNumberOfPosts() {
		Integer retVal = 0;
		try {
			retVal = (Integer)sqlMap.queryForObject("getBookmarkCount");
		} catch (SQLException e) {
			log.error("Error determining bookmark size.", e);
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<Bookmark>> getPostEntries(Integer skip, Integer max) {
		BookmarkParam param = new BookmarkParam();
		param.setOffset(skip);
		param.setLimit(max);
		
		List<LucenePost<Bookmark>> retVal = null;
		try {
			retVal = sqlMap.queryForList("getBookmarksForIndex3", param);
		} catch (SQLException e) {
			log.error("Error getting bookmark entries.", e);
			retVal = new LinkedList<LucenePost<Bookmark>>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Post<Bookmark>> getPostsForTimeRange2(Date fromDate, Date toDate) {
		BookmarkParam param = new BookmarkParam();
		param.setFromDate(fromDate);
		param.setToDate(toDate);
		param.setLimit(Integer.MAX_VALUE);
		
		List<Post<Bookmark>> retVal = null;
		try {
			retVal = sqlMap.queryForList("getBookmarkForTimeRange2", param);
		} catch (SQLException e) {
			log.error("Error getting bookmark entries.", e);
			retVal = new LinkedList<Post<Bookmark>>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<Bookmark>> getNewPosts(Integer lastTasId) {
		BookmarkParam param = new BookmarkParam();
		param.setLastTasId(lastTasId);
		param.setLimit(Integer.MAX_VALUE);
		
		List<LucenePost<Bookmark>> retVal = null;
		try {
			retVal = sqlMap.queryForList("getBookmarkForTimeRange3", param);
		} catch (SQLException e) {
			log.error("Error getting bookmark entries.", e);
			retVal = new LinkedList<LucenePost<Bookmark>>();
		}
		
		return retVal;
	}
}
