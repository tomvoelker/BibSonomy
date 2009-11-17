package org.bibsonomy.lucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.params.BibTexParam;
import org.bibsonomy.lucene.database.params.BookmarkParam;
import org.bibsonomy.lucene.database.params.ListParam;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.lucene.database.results.Pair;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 *
 */
public class LuceneBibTexLogic extends LuceneDBLogic<BibTex> {
	private final Log log = LogFactory.getLog(LuceneBibTexLogic.class);
	
	/** singleton pattern's instance reference */
	protected static LuceneDBLogic<BibTex> instance = null;
	
	/**
	 * constructor disabled for enforcing singleton pattern 
	 */
	private LuceneBibTexLogic() {
		super();
	}
	
	/**
	 * @return An instance of this implementation of {@link LuceneDBInterface}
	 */
	public static LuceneDBInterface<BibTex> getInstance() {
		if (instance == null) instance = new LuceneBibTexLogic();
		return instance;
	}
	
	//------------------------------------------------------------------------
	// db interface implementation
	//------------------------------------------------------------------------
	
	//------------------------------------------------------------------------
	// abstract LuceneDBLogic interface implemetation
	//------------------------------------------------------------------------
	@Override
	protected ResourcesParam<BibTex> getResourcesParam() {
		return new BibTexParam();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<LucenePost<BibTex>> getPostsForUserInternal(ResourcesParam<BibTex> param) {
		List<LucenePost<BibTex>> retVal = null;
		try {
			retVal = (List<LucenePost<BibTex>>)this.sqlMap.queryForList("getBibTexForUser", param);
		} catch (SQLException e) {
			log.error("Error fetching publications for user " + param.getUserName(), e);
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Integer> getContentIdsToDeleteInternal(Pair<Date, Date> param) throws SQLException {
		return (List<Integer>)this.sqlMap.queryForList("getBibTexContentIdsToDelete", param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getContentIdsToDeleteInternal(Date lastLogDate) throws SQLException {
		return (List<Integer>)this.sqlMap.queryForList("getBibTexContentIdsToDelete2", lastLogDate);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<HashMap<String, Object>> getPostsForTimeRange(Date fromDate, Date toDate) {
		Pair<Date,Date> param = new Pair<Date,Date>();
		param.setFirst(fromDate);
		param.setSecond(toDate);
		List<HashMap<String,Object>> retVal = null; 
		try {
			retVal = (List<HashMap<String,Object>>)this.sqlMap.queryForList("getBibTexPostsForTimeRange", param);
		} catch (SQLException e) {
			log.error("Error fetching publications for given time range", e);
		}
		
		log.debug("retrieveRecordsFromDatabase: " + retVal.size());
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Post<BibTex>> getUpdatedPostsForTimeRange(ResourcesParam<BibTex> param) throws SQLException {
		List<Post<BibTex>> retVal = null;
		retVal = (List<Post<BibTex>>)this.sqlMap.queryForList("getUpdatedBibTexPostsForTimeRange", param);
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
			retVal = (Date)sqlMap.queryForObject("getLastLogBibTex");
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
			retVal = (Integer)sqlMap.queryForObject("getBibTexCount");
		} catch (SQLException e) {
			log.error("Error determining bibtex size.", e);
		}
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<BibTex>> getPostEntries(Integer skip, Integer max) {
		BibTexParam param = new BibTexParam();
		param.setOffset(skip);
		param.setLimit(max);
		
		List<LucenePost<BibTex>> retVal = null;
		try {
			retVal = (List<LucenePost<BibTex>>)sqlMap.queryForList("getBibTexForIndex3", param);
		} catch (SQLException e) {
			log.error("Error getting bibtex entries.", e);
			retVal = new LinkedList<LucenePost<BibTex>>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Post<BibTex>> getPostsForTimeRange2(Date fromDate, Date toDate) {
		BibTexParam param = new BibTexParam();
		param.setFromDate(fromDate);
		param.setToDate(toDate);
		param.setLimit(Integer.MAX_VALUE);
		
		List<Post<BibTex>> retVal = null;
		try {
			retVal = (List<Post<BibTex>>)sqlMap.queryForList("getBibTexPostsForTimeRange2", param);
		} catch (SQLException e) {
			log.error("Error getting bibtex entries.", e);
			retVal = new LinkedList<Post<BibTex>>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LucenePost<BibTex>> getNewPosts(Integer lastTasId) {
		BibTexParam param = new BibTexParam();
		param.setLastTasId(lastTasId);
		param.setLimit(Integer.MAX_VALUE);
		
		List<LucenePost<BibTex>> retVal = null;
		try {
			retVal = (List<LucenePost<BibTex>>)sqlMap.queryForList("getBibTexPostsForTimeRange3", param);
		} catch (SQLException e) {
			log.error("Error getting bibtex entries.", e);
			retVal = new LinkedList<LucenePost<BibTex>>();
		}
		
		return retVal;
	}

}
