package org.bibsonomy.lucene;

import java.util.Set;

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
}
