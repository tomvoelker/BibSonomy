package org.bibsonomy.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.CrudableContent;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.Order;

/**
 * @author Jens Illig
 * @author Christian Kramer
 * @version $Id$
 */
public class DBLogic implements LogicInterface {

	private static final Logger log = Logger.getLogger(DBLogic.class);

	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> allDatabaseManagers;
	private final GeneralDatabaseManager generalDBManager;
	private final DocumentDatabaseManager docDBManager;
	private final PermissionDatabaseManager permissionDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final UserDatabaseManager userDBManager;
	private final GroupDatabaseManager groupDBManager;
	private final TagDatabaseManager tagDBManager;
	private final DBSessionFactory dbSessionFactory;

	private final String loginUserName;

	protected DBLogic(final String loginUserName, final DBSessionFactory dbSessionFactory) {
		this.loginUserName = loginUserName;

		generalDBManager = GeneralDatabaseManager.getInstance();
		allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>>();
		bibtexDBManager = BibTexDatabaseManager.getInstance();
		allDatabaseManagers.put(BibTex.class, this.bibtexDBManager);
		bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);

		docDBManager = DocumentDatabaseManager.getInstance();
		userDBManager = UserDatabaseManager.getInstance();
		groupDBManager = GroupDatabaseManager.getInstance();
		tagDBManager = TagDatabaseManager.getInstance();
		permissionDBManager = PermissionDatabaseManager.getInstance();

		this.dbSessionFactory = dbSessionFactory;		
	}

	/**
	 * Returns a new database session.
	 */
	private DBSession openSession() {
		return dbSessionFactory.getDatabaseSession();
	}

	/*
	 * Returns all users of the system
	 */
	public List<User> getUsers(final int start, final int end) {
		this.permissionDBManager.checkStartEnd(start, end, "user");
		final DBSession session = openSession();
		try {
			return userDBManager.getAllUsers(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns all users who are members of the specified group
	 */
	public List<User> getUsers(final String groupName, final int start, final int end) {
		final DBSession session = openSession();
		try {
			return groupDBManager.getGroupMembers(this.loginUserName, groupName, session).getUsers();
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details about the specified user and makes sure that we don't
	 * leak private information like the e-mail-address.
	 */
	public User getUserDetails(final String userName) {
		final DBSession session = openSession();
		try {
			final User user = userDBManager.getUserDetails(userName, session);
			if (userName.equals(this.loginUserName) == false) {
				user.setEmail(null);
				user.setRealname(null);
				user.setHomepage(null);
			}			
			user.setGroups(this.groupDBManager.getGroupsForUser(userName, session));
			return user;
		} finally {
			session.close();
		}
	}

	/*
	 * Returns a list of posts; the list can be filtered.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end, String search) {
		
		if (grouping.equals(GroupingEntity.ALL)) {
			this.permissionDBManager.checkStartEnd(start, end, "post");
		}
		
		final List<Post<T>> result;
		final DBSession session = openSession();
		try {
			/*if (resourceType == Resource.class) {
				 * yes, this IS unsave and indeed it BREAKS restrictions on generic-constraints.
				 * it is the result of two designs:
				 *  1. @ibatis: database-results should be accessible as a stream or should at least be saved using the visitor pattern (collection<? super X> arguments would do fine)
				 *  2. @bibsonomy: this method needs runtime-type-checking which is not supported by generics
				 *  so what: copy each and every entry manually or split this method to become
				 *           type-safe WITHOUT falling back to <? extends Resource> (which
				 *           means read-only) in the whole project
				 * result = bibtexDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, false);			
				 * // TODO: solve problem with limit+offset:  result.addAll(bookmarkDBManager.getPosts(authUser, grouping, groupingName, tags, hash, popular, added, start, end, false));
				 * 
			} else */
			if (resourceType == BibTex.class) {
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, this.loginUserName, grouping, groupingName, tags, hash, order, start, end, search);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = ((List) bibtexDBManager.getPosts(param, session));
			} else if (resourceType == Bookmark.class) {
				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, this.loginUserName, grouping, groupingName, tags, hash, order, start, end, search);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = ((List) bookmarkDBManager.getPosts(param, session));
			} else {
				throw new UnsupportedResourceTypeException();
			}
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * Returns details to a post. A post is uniquely identified by a hash of the
	 * corresponding resource and a username.
	 */
	public Post<? extends Resource> getPostDetails(final String resourceHash, final String userName) {
		final DBSession session = openSession();
		try {
			Post<? extends Resource> rVal;
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : allDatabaseManagers.values()) {
				rVal = manager.getPostDetails(this.loginUserName, resourceHash, userName, session);
				if (rVal != null) {
					return rVal;
				}
			}
		} finally {
			session.close();
		}
		return null;
	}

	/*
	 * Returns all groups of the system
	 */
	public List<Group> getGroups(final int start, final int end) {
		final DBSession session = openSession();
		try {
			return groupDBManager.getAllGroups(start, end, session);
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details of one group
	 */
	public Group getGroupDetails(final String groupName) {
		final DBSession session = openSession();
		try {
			return groupDBManager.getGroupByName(groupName, session);
		} finally {
			session.close();
		}
	}

	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final int start, final int end) {
		if (grouping.equals(GroupingEntity.ALL)) {
			this.permissionDBManager.checkStartEnd(start, end, "Tag");
		}		
		
		final DBSession session = openSession();
		final List<Tag> result;
		
		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUserName, grouping, groupingName, tags, null, null, start, end, null);
			
			if (resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				// this is save because of RTTI-check of resourceType argument which is of class T
				param.setRegex(regex);
				// need to switch from class to string to ensure legibility of Tags.xml
				param.setContentTypeByClass(resourceType);				
				result = tagDBManager.getTags(param, session);
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
			
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * Returns details about a tag.
	 */
	public Tag getTagDetails(final String tagName) {
		final DBSession session = openSession();
		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUserName, null, this.loginUserName, Arrays.asList(tagName), null, null, 0, 1, null);
			return tagDBManager.getTagDetails(param, session); 
		} finally {
			session.close();
		}
	}

	/**
	 * Checks if the given software key is valid.
	 * @param softwareKey software key to be validated
	 * @return true iff the given software key is valid.
	 */
	public boolean validateSoftwareKey(@SuppressWarnings("unused") final String softwareKey) {
		// FIXME: impl. a software key
		return true;
	}

	/*
	 * Removes the given user.
	 */
	public void deleteUser(final String userName) {
		
		throw new UnsupportedOperationException("not yet available");
		
//		if ((this.loginUserName == null) || (this.loginUserName.equals(userName) == false)) {
//			throw new ValidationException("You are not authorized to perform the requested operation");
//		}		
//		final DBSession session = openSession();
//		try {
//			userDBManager.deleteUser(userName, session);
//		} finally {
//			session.close();
//		}
	}

	/*
	 * Removes the given group.
	 */
	public void deleteGroup(final String groupName) {
		
		throw new UnsupportedOperationException("not yet available");
		
//		final DBSession session = openSession();
//		try {
//			groupDBManager.deleteGroup(groupName, session);
//		} finally {
//			session.close();
//		}
	}

	/*
	 * Removes an user from a group.
	 */
	public void removeUserFromGroup(final String groupName, final String userName) {
		// FIXME: IMPORTANT: not everybody may do this!
		// better do nothing than anything horribly wrong:
		throw new UnsupportedOperationException("not yet available");
//		final DBSession session = openSession();
//		try {
//			groupDBManager.removeUserFromGroup(groupName, userName, session);
//		} finally {
//			session.close();
//		}
	}

	/*
	 * Removes the given post - identified by the connected resource's hash -
	 * from the user.
	 */
	public void deletePost(final String userName, final String resourceHash) {
		if ((this.loginUserName == null) || (this.loginUserName.equals(userName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		
		final DBSession session = openSession();
		try {
			boolean resourceFound = false;
			// TODO would be nice to know about the resourcetype or the instance behind this resourceHash
			for (final CrudableContent<? extends Resource, ? extends GenericParam> man : allDatabaseManagers.values()) {
				if (man.deletePost(userName, resourceHash, session) == true) {
					resourceFound = true;
					break;
				}
			}
			if (resourceFound == false) {
				throw new IllegalStateException("The resource with ID " + resourceHash + " does not exist and could hence not be deleted.");
			}
		} finally {
			session.close();
		}
	}

	/*
	 * Adds/updates a user in the database.
	 */
	private String storeUser(final User user, boolean update) {
		
		throw new UnsupportedOperationException("not yet available");
		
// TODO check if the following is correct
// 
//		final DBSession session = openSession();
//		try {
//			String errorMsg = null;
//			
//			final User existingUser = userDBManager.getUserDetails(user.getName(), session);
//			if (existingUser != null) {
//				if (update == false) {
//					errorMsg = "user " + existingUser.getName() + " already exists";
//				} else if (existingUser.getName().equals(this.loginUserName) == false) {
//					errorMsg = "user " + this.loginUserName + " is not authorized to change user " + existingUser.getName();
//					log.warn(errorMsg);
//					throw new ValidationException(errorMsg);
//				}
//			} else {
//				if (update == true) {
//					errorMsg = "user " + user.getName() + " does not exist";
//				}
//			}
//			if (errorMsg != null) {
//				log.warn(errorMsg);
//				throw new IllegalStateException(errorMsg);
//			}
//			if (update == false) {
//				return userDBManager.createUser(user, session);
//			}
//			throw new UnsupportedOperationException("update user not implemented yet");
//		} finally {
//			session.close();
//		}
	}

	/*
	 * Adds/updates a post in the database.
	 */
	private <T extends Resource> String storePost(Post<T> post, boolean update) {
		final DBSession session = openSession();
		try {
			final CrudableContent<T, GenericParam> man = getFittingDatabaseManager(post);
			final String oldIntraHash = post.getResource().getIntraHash();
			post.getResource().recalculateHashes();			
			post = this.validateGroups(post, session);			
			man.storePost(this.loginUserName, post, oldIntraHash, update, session);
			// if we don't get an exception here, we assume the resource has been successfully stored
			return post.getResource().getIntraHash();
		} finally {
			session.close();
		}
	}
	
	/**
	 * Check for each group of a post if the groups actually exist and if the posting user is allowed to post.  
	 * If yes, insert the correct group ID
	 * 
	 * @param post the incoming post
	 * @return post the incoming post with the groupIDs filled in
	 */
	private <T extends Resource> Post<T> validateGroups(Post<T> post, DBSession session) {
		
		// retrieve the user's groups
		final List<Integer> groupIds = generalDBManager.getGroupIdsForUser(post.getUser().getName(), session);
		// each user can post as public / private / friends
		groupIds.add(GroupID.PUBLIC.getId());
		groupIds.add(GroupID.PRIVATE.getId());
		groupIds.add(GroupID.FRIENDS.getId());

		for (final Group group : post.getGroups()) {
			final Group testGroup = groupDBManager.getGroupByName(group.getName().toLowerCase(), session);
			if (testGroup == null) {
				// group does not exist
				throw new ValidationException("Group " + group.getName() + " does not exist");
			}
			if (!groupIds.contains(testGroup.getGroupId())) {
				// the posting user is not a member of this group
				throw new ValidationException("User " + post.getUser().getName() + " is not a member of group " + group.getName());
			}
			group.setGroupId(testGroup.getGroupId());
		}		
		
		// no group specified -> make it public
		if (post.getGroups().size() == 0) {
			post.getGroups().add(new Group(GroupID.PUBLIC));
		}
		
		return post;
	}

	@SuppressWarnings("unchecked")
	private <T extends Resource> CrudableContent<T, GenericParam> getFittingDatabaseManager(final Post<T> post) {
		final Class resourceClass = post.getResource().getClass();
		CrudableContent<? extends Resource, ? extends GenericParam> man = allDatabaseManagers.get(resourceClass);
		if (man == null) {
			for (final Map.Entry<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> entry : allDatabaseManagers.entrySet()) {
				if (entry.getKey().isAssignableFrom(resourceClass)) {
					man = entry.getValue();
					break;
				}
			}
			if (man == null) {
				throw new UnsupportedResourceTypeException();
			}
		}
		return ((CrudableContent) man);
	}

	/*
	 * Adds/updates a group in the database.
	 */
	private String storeGroup(@SuppressWarnings("unused") final Group group, @SuppressWarnings("unused") boolean update) {
		
		throw new UnsupportedOperationException("not yet available");
		
//		FIXME: unsure who may change a group -> better doing nothing
//		final DBSession session = this.openSession();
//		try {
//			this.groupDBManager.storeGroup(group, update, session);
//		} finally {
//			session.close();
//		}		
	}

	/*
	 * Adds an existing user to an existing group.
	 */
	public void addUserToGroup(final String groupName, final String userName) {
		
		throw new UnsupportedOperationException("not yet available");
		
//		final DBSession session = openSession();
//		try {
//			groupDBManager.addUserToGroup(groupName, userName, session);
//		} finally {
//			session.close();
//		}
	}

	private void ensureLoggedIn() {
		if (this.loginUserName == null) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}
	
	public String createGroup(Group group) {
		ensureLoggedIn();		
		return this.storeGroup(group, false);
	}

	public String updateGroup(Group group) {
		ensureLoggedIn();
		return this.storeGroup(group, true);
	}

	public String createPost(Post<?> post) {
		ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(post, this.loginUserName);
		return this.storePost(post, false);
	}

	public String updatePost(Post<?> post) {
		ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(post, this.loginUserName);
		return this.storePost(post, true);
	}

	public String createUser(User user) {
		return this.storeUser(user, false);
	}

	public String updateUser(User user) {
		if ((this.loginUserName == null) || (this.loginUserName.equals(user.getName()) == false)) {
			final String errorMsg = "user " + ((this.loginUserName != null) ? this.loginUserName : "anonymous") + " is not authorized to change user " + user.getName();
			log.warn(errorMsg);
			throw new ValidationException(errorMsg);
		}
		return this.storeUser(user, true);
	}

	public String getAuthenticatedUser() {
		return this.loginUserName;
	}

	public String addDocument(Document doc) {
		ensureLoggedIn();
		permissionDBManager.ensureWriteAccess(doc, loginUserName);
		return this.storeDocument(doc).getFileHash();
	}

	/**
	 * @param doc
	 * @return doc
	 */
	public Document storeDocument(final Document doc){
		final DBSession session = openSession();
		
		try {
			//create the docParam object
			DocumentParam docParam = new DocumentParam();
			
			/*
			 * store all necessary informations in the docParam object
			 * i think its not needed to create a method because there are only
			 * 4 fields to save. 
			 */
			docParam.setUserName(doc.getUserName());
			docParam.setResourceHash(doc.getResourceHash());
			docParam.setFileHash(doc.getFileHash());
			docParam.setFileName(doc.getFileName());
			
			final boolean valid = docDBManager.validateResource(docParam, session);
			final boolean existingDoc = docDBManager.checkForExistingDocuments(docParam, session);
			
			/*
			 * valid means that the resource is a bibtex entry and the given user is
			 * the owner of this entry.
			 */
			if (valid){
				/*
				 * we have to handle if there is an existing document or not.
				 * if there is an existing document for this resource we have to update it,
				 * if not just write it to the db.
				 */
				if (existingDoc){
					//update
					docDBManager.updateDocument(docParam, session);
				} else {
					//add
					docDBManager.addDocument(docParam, session);
				}
			} else {
				throw new ValidationException("You are not authorized to perform the requested operation.");
			}
		} finally {
			session.close();
		}
		log.info("API - New file added to db: " + doc.getFileName() + " from User: " + doc.getUserName());
		return doc;
	}

	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		ensureLoggedIn();
		
		final DBSession session = openSession();
		Document doc;
		
		try {
			//create the docParam object
			DocumentParam docParam = new DocumentParam();
			
			//fill the docParam object
			docParam.setFileName(fileName);
			docParam.setResourceHash(resourceHash);
			docParam.setUserName(userName);
			
			final boolean valid = docDBManager.validateResource(docParam, session);
			
			/*
			 * valid means that the resource is a bibtex entry and the given user is
			 * the owner of this entry.
			 */
			if (valid){
				// get the requested document
				doc = docDBManager.getDocument(docParam, session);
				if (doc == null){
					throw new IllegalStateException("No document for this bibtex entry");
				}
			} else {
				throw new ValidationException("You are not authorized to perform the requested operation.");
			}
		} finally {
			session.close();
		}
		log.info("API - New filerequest: " + doc.getFileName() + " from User: " + doc.getUserName());
		return doc;
	}
	
	/**
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public void deleteDocument(final String userName, final String resourceHash, final String fileName){
		ensureLoggedIn();
	
		permissionDBManager.ensureWriteAccess(loginUserName, userName);
		
		final DBSession session = openSession();
		
		try {
			//create the docParam object
			DocumentParam docParam = new DocumentParam();
			
			//fill the docParam object
			docParam.setFileName(fileName);
			docParam.setResourceHash(resourceHash);
			docParam.setUserName(userName);
			
			final boolean valid = docDBManager.validateResource(docParam, session);
			
			/*
			 * valid means that the resource is a bibtex entry and the given user is
			 * the owner of this entry.
			 */
			if (valid){
				docDBManager.deleteDocument(docParam, session);
			} else {
				throw new ValidationException("You are not authorized to perform the requested operation.");
			}
		} catch (NullPointerException e) {
			throw new ResourceNotFoundException("The requested bibtex resource doesn't exists.");
		} finally {
			session.close();
		}
		log.info("API - Document deleted: " + fileName + " from User: " + userName);
	}
}