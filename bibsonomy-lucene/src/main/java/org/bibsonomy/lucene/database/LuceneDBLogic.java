package org.bibsonomy.lucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.print.attribute.standard.PresentationDirection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.params.BibTexParam;
import org.bibsonomy.lucene.database.params.BookmarkParam;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.lucene.database.results.Pair;
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
public abstract class LuceneDBLogic<R extends Resource> extends LuceneDBGenerateLogic<R> {
	private final Log log = LogFactory.getLog(LuceneDBLogic.class);

	/**
	 * constructor disabled for enforcing singleton pattern 
	 */
	protected LuceneDBLogic() {
		super();
	}
	
	//------------------------------------------------------------------------
	// db interface implementation
	//------------------------------------------------------------------------
	public List<Post<R>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset) {
		final ResourcesParam<R> param = getResourcesParam();
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(simHash);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		
		List<Post<R>> retVal;
		retVal = getPostsForUserInternal(param);
		if( retVal!=null )
			return retVal;
		else 
			return new LinkedList<Post<R>>();
	}

	public Date getNewestRecordDateFromTas(){
		Date retVal = null;
		try {
			retVal = (Date)this.sqlMap.queryForObject("getNewestRecordDateFromTas");
		} catch (SQLException e) {
			log.error("Error determining last tas entry", e);
		}
		
		return retVal;
	}
	
	public List<Integer> getContentIdsToDelete(Date fromDate, Date toDate) {
		List<Integer> retVal;
		
		Pair<Date,Date> param = new Pair<Date,Date>();
		param.setFirst(fromDate);
		param.setSecond(toDate);
		
		try {
			retVal = getContentIdsToDeleteInternal(param);
		} catch (SQLException e) {
			log.error("Error getting content ids to delete", e);
			retVal = new LinkedList<Integer>();
		}
		
		log.debug("getContentIdsToDelete - count: " + retVal.size());

		return retVal;
	}
	
	/**
	 * get all public posts within a given time range
	 * 
	 * FIXME: use bibsonomy model classes
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<HashMap<String, String>> retrieveRecordsFromDatabase(Date fromDate, Date toDate) {
		List<HashMap<String, Object>> posts  = getPostsForTimeRange(fromDate, toDate);
		List<HashMap<String, String>> retVal = new LinkedList<HashMap<String,String>>();
		
		// ugly conversion: convert each object to the corresponding string representation
		for( HashMap<String,Object> post : posts ) {
			HashMap<String,String> newPost = new HashMap<String, String>();
			for( String key : post.keySet() ) {
				Object oldVal = post.get(key);
				String newVal = (oldVal==null)?"":oldVal.toString();
				newPost.put(key, newVal);
			}
			retVal.add(newPost);
		}
		
		log.debug("retrieveRecordsFromDatabase: " + posts.size());
		return retVal;
	}
	

	//------------------------------------------------------------------------
	// abstract interface definition
	//------------------------------------------------------------------------
	protected abstract List<Post<R>> getPostsForUserInternal(ResourcesParam<R> param);
	protected abstract List<Integer> getContentIdsToDeleteInternal(Pair<Date, Date> param) throws SQLException;
	protected abstract ResourcesParam<R> getResourcesParam();
	
	protected abstract HashMap<String, String> getContentFields();
	protected abstract List<HashMap<String, Object>> getPostsForTimeRange(Date fromDate, Date toDate);


}
