package org.bibsonomy.database.newImpl.content;

import java.util.List;

import org.bibsonomy.database.managers.getpostsqueries.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.getpostsqueries.RequestHandlerForGetPosts;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

public class BookmarkDBManager extends AbstractContentDBManager {

	/*
	 * TODO: das hier auch als Singleton?!
	 */
	
	private RequestHandlerForGetPosts getPostsHandler;
	
	public BookmarkDBManager() {
		getPostsHandler = new GetBookmarksByHashForUser();
		/*
		 * TODO: hier die Kette aufbauen!
		 */
		
	}
	
	@Override
	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
		return getPostsHandler.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
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
