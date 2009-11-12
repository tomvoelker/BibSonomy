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
	protected HashMap<String, String> getContentFields() {
		HashMap<String, String> contentFields = new HashMap<String, String>();
		
		contentFields.put("content_id", "");
		contentFields.put("group", "");
		contentFields.put("date", "");
		contentFields.put("user_name", "");
		contentFields.put("desc", "");
		contentFields.put("ext", "");
		contentFields.put("url", "");
		contentFields.put("tas", "");
		
		return contentFields;
	}

	@Override
	protected ResourcesParam<Bookmark> getResourcesParam() {
		return new BookmarkParam();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Post<Bookmark>> getPostsForUserInternal(ResourcesParam<Bookmark> param) {
		List<Post<Bookmark>> retVal = null;
		try {
			retVal = (List<Post<Bookmark>>)this.sqlMap.queryForList("getBookmarkForUser", param);
		} catch (SQLException e) {
			log.error("Error fetching bookmarks for user " + param.getUserName(), e);
		}

		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Post<Bookmark>> getUpdatedPostsForTimeRange(ResourcesParam<Bookmark> param) throws SQLException {
		List<Post<Bookmark>> retVal = null;
		retVal = (List<Post<Bookmark>>)this.sqlMap.queryForList("getUpdatedBookmarkPostsForTimeRange", param);
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Integer> getContentIdsToDeleteInternal(Pair<Date, Date> param) throws SQLException {
		return (List<Integer>)this.sqlMap.queryForList("getBookmarkContentIdsToDelete", param);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<HashMap<String, Object>> getPostsForTimeRange(Date fromDate, Date toDate) {
		Pair<Date,Date> param = new Pair<Date,Date>();
		param.setFirst(fromDate);
		param.setSecond(toDate);
		
		List<HashMap<String,Object>> retVal = null;
		try {
			retVal = (List<HashMap<String,Object>>)this.sqlMap.queryForList("getBookmarkPostsForTimeRange", param);
		} catch (SQLException e) {
			log.error("Error fetching publications for given time range", e);
		}
		
		log.debug("retrieveRecordsFromDatabase: " + retVal.size());
		return retVal;
	}

	//------------------------------------------------------------------------
	// methods for building the index
	// TODO: maybe we should introduce a special class hierarchy
	//------------------------------------------------------------------------
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
	public List<Post<Bookmark>> getPostEntries(Integer skip, Integer max) {
		BookmarkParam param = new BookmarkParam();
		param.setOffset(skip);
		param.setLimit(max);
		
		List<Post<Bookmark>> retVal = null;
		try {
			retVal = (List<Post<Bookmark>>)sqlMap.queryForList("getBookmarksForIndex", param);
		} catch (SQLException e) {
			log.error("Error getting bookmark entries.", e);
			retVal = new LinkedList<Post<Bookmark>>();
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
			retVal = (List<Post<Bookmark>>)sqlMap.queryForList("getBookmarkForTimeRange2", param);
		} catch (SQLException e) {
			log.error("Error getting bookmark entries.", e);
			retVal = new LinkedList<Post<Bookmark>>();
		}
		
		return retVal;
	}
}
