package org.bibsonomy.services.searcher;

import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;

/**
 * Interface for resource search operations
 * 
 * @author dzo
 *
 * @param <R>
 */
public interface ResourceSearch<R extends Resource> {
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
	public ResultList<Post<R>> searchPosts(String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset);

	
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
	ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset);


	/**
	 * flags/unflags user as spammer, depending on user.getPrediction()
	 * 
	 * @param user
	 */
	public void flagSpammer(User user);
}
