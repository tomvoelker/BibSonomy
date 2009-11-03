package org.bibsonomy.lucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
	protected List<Post<BibTex>> getPostsForUserInternal(ResourcesParam<BibTex> param) {
		List<Post<BibTex>> retVal = null;
		try {
			retVal = (List<Post<BibTex>>)this.sqlMap.queryForList("getBibTexForUser", param);
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

	@Override
	protected HashMap<String, String> getContentFields() {
		HashMap<String, String> contentFields = new HashMap<String, String>();
		
		contentFields.put("content_id", "");
		contentFields.put("group", "");
		contentFields.put("date", "");
		contentFields.put("user_name", "");
		contentFields.put("author", "");
		contentFields.put("editor", "");
		contentFields.put("title", "");
		contentFields.put("journal", "");
		contentFields.put("booktitle", "");
		contentFields.put("volume", "");
		contentFields.put("number", "");
		contentFields.put("chapter", "");
		contentFields.put("edition", "");
		contentFields.put("month", "");
		contentFields.put("day", "");
		contentFields.put("howPublished", "");
		contentFields.put("institution", "");
		contentFields.put("organization", "");
		contentFields.put("publisher", "");
		contentFields.put("address", "");
		contentFields.put("school", "");
		contentFields.put("series", "");
		contentFields.put("bibtexKey", "");
		contentFields.put("url", "");
		contentFields.put("type", "");
		contentFields.put("description", "");
		contentFields.put("annote", "");
		contentFields.put("note", "");
		contentFields.put("pages", "");
		contentFields.put("bKey", "");
		contentFields.put("crossref", "");
		contentFields.put("misc", "");
		contentFields.put("bibtexAbstract", "");
		contentFields.put("year", "");
		contentFields.put("tas", "");
		contentFields.put("entrytype", "");
		contentFields.put("intrahash", "");
		contentFields.put("interhash", "");
		
		return contentFields;
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

}
