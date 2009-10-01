package org.bibsonomy.services.searcher;

import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;

/**
 * 
 * @author dzo
 *
 * @param <R>
 */
public interface LuceneSearch<R extends Resource> {
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
	public ResultList<Post<R>> searchLucene(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset);

	
	/**
	 * TODO: document me
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
	ResultList<Post<BibTex>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset);
}
