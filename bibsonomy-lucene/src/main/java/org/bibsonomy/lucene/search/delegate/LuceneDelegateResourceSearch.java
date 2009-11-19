package org.bibsonomy.lucene.search.delegate;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.LuceneResourceManager;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * parent class for lucene search, coupling index management (spam) and search by delegation
 * 
 * @author fei
 *
 */
public class LuceneDelegateResourceSearch<R extends Resource> implements ResourceSearch<R> {
	private final static Log log = LogFactory.getLog(LuceneDelegateResourceSearch.class);
	
	/** resource manager for delegating index management (e.g., spam) */
	private LuceneResourceManager<R> resourceManager;

	/** resource searcher for delegating index search */
	private LuceneResourceSearch<R> resourceSearcher;

	//------------------------------------------------------------------------
	// ResourceSearch interface implementation
	//------------------------------------------------------------------------
	@Override
	public void flagSpammer(User user) {
		if(resourceManager!=null) 
			resourceManager.flagSpammer(user);
		else
			log.error("Trying to update spam status, but no index manager available");
	}

	@Override
	public ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset) {
		if(resourceSearcher!=null) 
			return resourceSearcher.searchAuthor(
					group, 
					search, 
					requestedUserName, requestedGroupName, 
					year, firstYear, lastYear, 
					tagList, 
					limit, offset);
		else {
			log.error("Trying to search authors, but no searcher available");
			return new ResultList<Post<R>>();
		}
	}

	@Override
	public ResultList<Post<R>> searchPosts(String group, String searchTerms,
			String requestedUserName, String UserName, Set<String> GroupNames,
			int limit, int offset) {
		if(resourceSearcher!=null) 
			return resourceSearcher.searchPosts(
					group, 
					searchTerms, 
					requestedUserName, UserName, GroupNames, 
					limit, offset);
		else {
			log.error("Trying to search for posts, but no searcher available");
			return new ResultList<Post<R>>();
		}
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setResourceManager(LuceneResourceManager<R> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public LuceneResourceManager<R> getResourceManager() {
		return resourceManager;
	}

	public void setResourceSearcher(LuceneResourceSearch<R> resourceSearcher) {
		this.resourceSearcher = resourceSearcher;
	}

	public LuceneResourceSearch<R> getResourceSearcher() {
		return resourceSearcher;
	}
}
