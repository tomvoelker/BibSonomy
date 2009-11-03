package org.bibsonomy.lucene.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;

/**
 * abstract parent class for lucene search
 * 
 * FIXME: better split org.bibsonomy.service.searcher.ResourceSearch into search/management tasks 
 * 
 * @author fei
 *
 * @param <R> resource type
 */
public abstract class LuceneResourceSearch<R extends Resource> {
	private static final Logger log = Logger.getLogger(LuceneResourceSearch.class);
	
	/** logic interface for retrieving data from bibsonomy */
	private LuceneDBInterface<R> dbLogic;
	
	/** known resource types */
	List<Class<? extends Resource>> resourceTypes = new LinkedList<Class<? extends Resource>>(); 

	/** reload the index -- has to be called after each index change */
	public abstract void reloadIndex();


	/** inserts all posts for given spammer into the index */
	abstract protected Class<R> getResourceType();

	
	//------------------------------------------------------------------------
	// search interface
	//------------------------------------------------------------------------
	/**
	 * TODO: document me
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param limit
	 * @param offset
	 * @return
	 */
	public abstract ResultList<Post<R>> searchPosts(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset);

	
	/**
	 * TODO: document me
	 * FIXME: This should be just a variant of searchPosts
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit
	 * @param offset
	 * @return
	 */
	public abstract ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset);
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setDbLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	public LuceneDBInterface<R> getDbLogic() {
		return dbLogic;
	}
}
