package org.bibsonomy.database.newImpl.content;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.getpostsqueries.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.getpostsqueries.RequestHandlerForGetPosts;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

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
		//List test =getPostsHandler.perform("jaeschke", GroupingEntity.USER, "mio", null, "acdcbe9350f5061732d0353a8deea172", false, false, 0, 1);
		List test =getPostsHandler.perform("d", GroupingEntity.USER, "jaeschke", null, "89e66a897c99ccfdd328f197f60625c8", false, false, 0, 1);
		System.out.println("test="+test.size());
		System.out.println("authUser = " + authUser);
		System.out.println("grouping = " + grouping);
		System.out.println("groupingName = " + groupingName);
		System.out.println("tags = " + tags);
		System.out.println("hash = " + hash);
		System.out.println("start = " + start);
		System.out.println("end = " + end);
		List<Post<? extends Resource>> posts = getPostsHandler.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, 1);
		System.out.println("BoookmarkDbManager posts.size= " + posts.size());
		return posts;
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
