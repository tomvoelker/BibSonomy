package org.bibsonomy.database.managers;

import java.util.Set;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.ModelUtils;
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
public class RESTDatabaseManager extends AbstractDatabaseManager implements LogicInterface {

	private final DatabaseManager db;

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	RESTDatabaseManager(final DatabaseManager db) {
		this.db = db;
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

	public Set<Group> getGroups(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Post<Resource> getPostDetails(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Set<Post<Resource>> getPosts(String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		switch (resourceType) {
		case BOOKMARK:
			final BookmarkParam param = new BookmarkParam();
			param.setRequestedUserName(authUser);
			param.setOffset(start);
			param.setLimit(end);
			// FIXME Type mismatch: cannot convert from Set<Post> to Set<Post> - return type needs to be changed in the interface
			return ModelUtils.putResourcesIntoPosts(this.db.getBookmark().getBookmarkForUser(param));
		}
		throw new NotImplementedException();
	}

	public Tag getTagDetails(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Set<Tag> getTags(String arg0, GroupingEntity arg1, String arg2, String arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public User getUserDetails(String arg0, String arg1) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Set<User> getUsers(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public Set<User> getUsers(String arg0, String arg1, int arg2, int arg3) {
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
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}