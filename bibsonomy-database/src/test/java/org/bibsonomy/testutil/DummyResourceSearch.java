package org.bibsonomy.testutil;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.es.SearchType;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, org.bibsonomy.model.es.SearchType, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<Resource>> getPosts(String resourceType, String userName,
			String requestedUserName, String requestedGroupName,
			List<String> requestedRelationNames,
			Collection<String> allowedGroups, SearchType searchType,
			String searchTerms, String titleSearchTerms,
			String authorSearchTerms, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			Order order, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	@Override
	public List<Tag> getTags(String userName, String requestedUserName,
			String requestedGroupName, Collection<String> allowedGroups,
			String searchTerms, String titleSearchTerms,
			String authorSearchTerms, Collection<String> tagIndex, String year,
			String firstYear, String lastYear, List<String> negatedTags,
			int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}



}
