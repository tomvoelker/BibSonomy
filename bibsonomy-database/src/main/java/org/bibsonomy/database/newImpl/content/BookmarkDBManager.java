package org.bibsonomy.database.newImpl.content;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

public class BookmarkDBManager extends AbstractContentDBManager {

	@Override
	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
		
		/*
		 * TODO: hier über die Chain of Responsibility für Bookmarks iterieren!
		 */
		
		
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public Post<Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean deletePost(String userName, String resourceHash) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean storePost(String userName, Post post, boolean update) {
		// TODO Auto-generated method stub
		return false;
	}

}
