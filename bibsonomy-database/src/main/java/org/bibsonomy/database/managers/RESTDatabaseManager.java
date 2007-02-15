package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.getpostsqueries.GetPostsByHashForUser;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Adapter for the REST interface.
 * 
 * @author Christian Schenk
 * @author mgr
 */
@Deprecated
public class RESTDatabaseManager extends AbstractDatabaseManager implements LogicInterface {

	private final DatabaseManager db;
	private RequestHandlerForGetPosts getPostsHandler;


	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	RESTDatabaseManager(final DatabaseManager db) {
		this.db = db;
		// chain for get post
		//getPostHandler = new GetPostByHash();
		getPostsHandler=new GetPostsByHashForUser();
		 //postHandler.setNext( new GetBookmarkHandler() );
		getPostsHandler.setNext(new GetPostsByHashForUser());

	}

	public void addUserToGroup(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void deleteGroup(String arg0) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void deletePost(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void deleteUser(String arg0) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Group getGroupDetails(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public List<Group> getGroups(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	/**
	 * Return a post with retaining details (authUser, resourceHash and current
	 * User)
	 */
	public Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String currUser) {
		// get handler chain
		return getPostsHandler.perform(authUser, resourceHash, currUser);
	}

	/**
	 * Return a set of post by given argument types
	 */
	public List<Post<? extends Resource>> getPosts(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return getPostsHandler.perform(authUser, resourceType, grouping, groupingName, tags, hash, popular, added, start, end);
		// resourceType = ResourceType.BOOKMARK; // TODO implement me..
		// switch (resourceType) {
		// case BOOKMARK:
		// // Mapping nicht korrekt! - des BookmarkParams? Wieso?
		// final BookmarkParam param = new BookmarkParam();
		// param.setRequestedUserName(authUser);
		// param.setOffset(start);
		// param.setLimit(end);
		// // return
		// //
		// ModelUtils.putResourcesIntoPosts(this.db.getBookmark().getBookmarkForUser(param));
		// return (Set<Post<Resource>>)
		// this.db.getBookmark().getBookmarkForUser(param);
		// }
		// throw new NotImplementedException();
	}

	public Tag getTagDetails(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public List<Tag> getTags(String arg0, GroupingEntity arg1, String arg2, String arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public User getUserDetails(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public List<User> getUsers(String authUser, int start, int end) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public List<User> getUsers(String arg0, String arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void removeUserFromGroup(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void storeGroup(Group arg0, boolean arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void storePost(String arg0, Post arg1, boolean arg2) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void storeUser(User arg0, boolean arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public boolean validateUserAccess(String arg0, String arg1) {
		// TODO implement me
		return true;
	}
}