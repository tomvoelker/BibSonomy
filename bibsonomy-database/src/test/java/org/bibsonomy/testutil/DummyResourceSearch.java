package org.bibsonomy.testutil;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 * @version $Id$
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {
	
	@Override
	public ResultList<Post<Resource>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final int limit, final int offset) {
		return null;
	}
	
	@Override
	public List<Tag> getTags(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final int limit, final int offset) {
		return null;
	}
}
