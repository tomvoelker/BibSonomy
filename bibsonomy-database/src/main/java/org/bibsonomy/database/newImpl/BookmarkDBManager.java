package org.bibsonomy.database.newImpl;

import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

public class BookmarkDBManager extends AbstractContentDBManager {

	@Override
	public Set<Post<Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
		
		/*
		 * TODO: hier über die Chain of Responsibility für Bookmarks iterieren!
		 */
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
