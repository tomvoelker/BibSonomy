package org.bibsonomy.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagRelationType;
import org.bibsonomy.common.exceptions.QueryTimeoutException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.AdminDatabaseManager;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.CrudableContent;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
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
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;

/**
 * @author Jens Illig
 * @author Christian Kramer
 * @version $Id$
 */
public class DBLogic implements LogicInterface {

	private static final Logger log = Logger.getLogger(DBLogic.class);

	private final Map<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> allDatabaseManagers;
	private final DocumentDatabaseManager docDBManager;
	private final PermissionDatabaseManager permissionDBManager;
	private final BookmarkDatabaseManager bookmarkDBManager;
	private final BibTexDatabaseManager bibtexDBManager;
	private final UserDatabaseManager userDBManager;
	private final GroupDatabaseManager groupDBManager;
	private final TagDatabaseManager tagDBManager;
	private final AdminDatabaseManager adminDBManager;
	private final DBSessionFactory dbSessionFactory;
	private final StatisticsDatabaseManager statisticsDBManager;
	private final TagRelationDatabaseManager tagRelationsDBManager;

	private final User loginUser;

	protected DBLogic(final User loginUser, final DBSessionFactory dbSessionFactory) {
		// each user is a member of the public group
		loginUser.addGroup(new Group(GroupID.PUBLIC));
		this.loginUser = loginUser;

		this.allDatabaseManagers = new HashMap<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>>();
		this.bibtexDBManager = BibTexDatabaseManager.getInstance();
		this.allDatabaseManagers.put(BibTex.class, this.bibtexDBManager);
		this.bookmarkDBManager = BookmarkDatabaseManager.getInstance();
		this.allDatabaseManagers.put(Bookmark.class, this.bookmarkDBManager);

		this.docDBManager = DocumentDatabaseManager.getInstance();
		this.userDBManager = UserDatabaseManager.getInstance();
		this.groupDBManager = GroupDatabaseManager.getInstance();
		this.tagDBManager = TagDatabaseManager.getInstance();
		this.adminDBManager = AdminDatabaseManager.getInstance();
		this.permissionDBManager = PermissionDatabaseManager.getInstance();
		this.statisticsDBManager = StatisticsDatabaseManager.getInstance();
		this.tagRelationsDBManager = TagRelationDatabaseManager.getInstance();

		this.dbSessionFactory = dbSessionFactory;		
	}

	/**
	 * Returns a new database session.
	 */
	private DBSession openSession() {
		return this.dbSessionFactory.getDatabaseSession();
	}

	/*
	 * Returns all users of the system
	 */
	public List<User> getUsers(final int start, final int end) {
		this.permissionDBManager.checkStartEnd(start, end, "user");
		final DBSession session = openSession();
		try {
			return this.userDBManager.getAllUsers(start, end, session);
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
			return this.groupDBManager.getGroupMembers(this.loginUser.getName(), groupName, session).getUsers();
		} finally {
			session.close();
		}
	}

	/*
	 * Returns details about the specified user and makes sure that we don't
	 * leak private information like the e-mail-address.
	 * 
	 * TODO: if userName = loginUser.getName() we could just return loginUser.
	 */
	public User getUserDetails(final String userName) {
		final DBSession session = openSession();
		try {
			final User user = this.userDBManager.getUserDetails(userName, session);
			if (userName.equals(this.loginUser.getName()) == false) {
				user.setEmail(null);
				user.setRealname(null);
				user.setHomepage(null);
				user.setPassword(null);
				user.setApiKey(null);
			}			
			user.setGroups(this.groupDBManager.getGroupsForUser(userName, true, session));
			return user;
		} finally {
			session.close();
		}
	}

	/*
	 * Returns a list of posts; the list can be filtered.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final FilterEntity filter, final int start, final int end, String search) {

		// check allowed start-/end-values 
		if (grouping.equals(GroupingEntity.ALL) && !present(tags)) {
			this.permissionDBManager.checkStartEnd(start, end, "post");
		}
		// check maximum number of allowed tags
		if (this.permissionDBManager.exceedsMaxmimumSize(tags)) {
			return new ArrayList<Post<T>>();
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
				final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, loginUser);
				if (filter != null) param.setFilter(filter);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = ((List) this.bibtexDBManager.getPosts(param, session));
			} else if (resourceType == Bookmark.class) {
				final BookmarkParam param = LogicInterfaceHelper.buildParam(BookmarkParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, this.loginUser);
				// this is save because of RTTI-check of resourceType argument which is of class T
				result = ((List) this.bookmarkDBManager.getPosts(param, session));
			} else {
				throw new UnsupportedResourceTypeException();
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Post<T>>();			
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
			for (final CrudableContent<? extends Resource, ? extends GenericParam> manager : this.allDatabaseManagers.values()) {
				rVal = manager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
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
			return this.groupDBManager.getAllGroups(start, end, session);
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
			return this.groupDBManager.getGroupByName(groupName, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param resourceType
	 * @param grouping
	 * @param groupingName
	 * @param regex
	 * @param tags
	 * @param start
	 * @param end
	 * @param search
	 * @return list of tags
	 */
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final String hash, final Order order, final int start, final int end, String search, TagRelationType relation) {
		if (grouping.equals(GroupingEntity.ALL)) {
			this.permissionDBManager.checkStartEnd(start, end, "Tag");
		}				
		final DBSession session = openSession();
		final List<Tag> result;

		try {
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUser.getName(), grouping, groupingName, tags, hash, order, start, end, search, this.loginUser);
			param.setTagRelationType(relation);

			if (resourceType == BibTex.class || resourceType == Bookmark.class || resourceType == Resource.class) {
				// this is save because of RTTI-check of resourceType argument which is of class T
				param.setRegex(regex);
				// need to switch from class to string to ensure legibility of Tags.xml
				param.setContentTypeByClass(resourceType);				
				result = this.tagDBManager.getTags(param, session);
			} else {
				throw new UnsupportedResourceTypeException("The requested resourcetype (" + resourceType.getClass().getName() + ") is not supported.");
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return an empty list
			return new ArrayList<Tag>();
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
			final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, this.loginUser.getName(), null, this.loginUser.getName(), Arrays.asList(tagName), null, null, 0, 1, null, this.loginUser);
			return this.tagDBManager.getTagDetails(param, session); 
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
//		throw new ValidationException("You are not authorized to perform the requested operation");
//		}		
//		final DBSession session = openSession();
//		try {
//		userDBManager.deleteUser(userName, session);
//		} finally {
//		session.close();
//		}
	}

	/*
	 * Removes the given group.
	 */
	public void deleteGroup(final String groupName) {

		throw new UnsupportedOperationException("not yet available");

//		final DBSession session = openSession();
//		try {
//		groupDBManager.deleteGroup(groupName, session);
//		} finally {
//		session.close();
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
//		groupDBManager.removeUserFromGroup(groupName, userName, session);
//		} finally {
//		session.close();
//		}
	}

	/*
	 * Removes the given post - identified by the connected resource's hash -
	 * from the user.
	 */
	public void deletePost(final String userName, final String resourceHash) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(userName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}

		final DBSession session = openSession();
		try {
			boolean resourceFound = false;
			// TODO would be nice to know about the resourcetype or the instance behind this resourceHash
			for (final CrudableContent<? extends Resource, ? extends GenericParam> man : this.allDatabaseManagers.values()) {
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
	private String storeUser(final User user, final boolean update) {	
		if (update == false) throw new UnsupportedOperationException("not yet available");

		final DBSession session = openSession();
		if(user.getName().equals(this.loginUser.getName()) && user.getSpammer() == null && user.getPrediction() == null) {
			return this.userDBManager.changeUser(user, session);
		}
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		return this.userDBManager.changeUser(user, session);

//		TODO check if the following is correct

//		final DBSession session = openSession();
//		try {
//		String errorMsg = null;

//		final User existingUser = userDBManager.getUserDetails(user.getName(), session);
//		if (existingUser != null) {
//		if (update == false) {
//		errorMsg = "user " + existingUser.getName() + " already exists";
//		} else if (existingUser.getName().equals(this.loginUserName) == false) {
//		errorMsg = "user " + this.loginUserName + " is not authorized to change user " + existingUser.getName();
//		log.warn(errorMsg);
//		throw new ValidationException(errorMsg);
//		}
//		} else {
//		if (update == true) {
//		errorMsg = "user " + user.getName() + " does not exist";
//		}
//		}
//		if (errorMsg != null) {
//		log.warn(errorMsg);
//		throw new IllegalStateException(errorMsg);
//		}
//		if (update == false) {
//		return userDBManager.createUser(user, session);
//		}
//		throw new UnsupportedOperationException("update user not implemented yet");
//		} finally {
//		session.close();
//		}
	}

	/*
	 * Adds/updates a post in the database.
	 */
	private <T extends Resource> String storePost(Post<T> post, final boolean update) {
		final DBSession session = openSession();
		try {
			final CrudableContent<T, GenericParam> man = getFittingDatabaseManager(post);
			final String oldIntraHash = post.getResource().getIntraHash();
			post.getResource().recalculateHashes();			
			post = this.validateGroups(post, session);			
			man.storePost(this.loginUser.getName(), post, oldIntraHash, update, session);
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
	private <T extends Resource> Post<T> validateGroups(final Post<T> post, final DBSession session) {
		// retrieve the user's groups
		final List<Integer> groupIds = this.groupDBManager.getGroupIdsForUser(post.getUser().getName(), session);
		// each user can post as public, private or friends
		groupIds.add(GroupID.PUBLIC.getId());
		groupIds.add(GroupID.PRIVATE.getId());
		groupIds.add(GroupID.FRIENDS.getId());

		for (final Group group : post.getGroups()) {
			final Group testGroup = this.groupDBManager.getGroupByName(group.getName().toLowerCase(), session);
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
		CrudableContent<? extends Resource, ? extends GenericParam> man = this.allDatabaseManagers.get(resourceClass);
		if (man == null) {
			for (final Map.Entry<Class<? extends Resource>, CrudableContent<? extends Resource, ? extends GenericParam>> entry : this.allDatabaseManagers.entrySet()) {
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
//		this.groupDBManager.storeGroup(group, update, session);
//		} finally {
//		session.close();
//		}		
	}

	/*
	 * Adds an existing user to an existing group.
	 */
	public void addUserToGroup(final String groupName, final String userName) {

		throw new UnsupportedOperationException("not yet available");

//		final DBSession session = openSession();
//		try {
//		groupDBManager.addUserToGroup(groupName, userName, session);
//		} finally {
//		session.close();
//		}
	}

	private void ensureLoggedIn() {
		if (this.loginUser.getName() == null) {
			throw new ValidationException("You are not authorized to perform the requested operation.");
		}
	}

	public String createGroup(final Group group) {
		this.ensureLoggedIn();		
		return this.storeGroup(group, false);
	}

	public String updateGroup(final Group group) {
		this.ensureLoggedIn();
		return this.storeGroup(group, true);
	}

	public String createPost(final Post<?> post) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		return this.storePost(post, false);
	}

	public String updatePost(final Post<?> post) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(post, this.loginUser);
		return this.storePost(post, true);
	}

	public String createUser(final User user) {
		return this.storeUser(user, false);
	}

	public String updateUser(final User user) {
		// TODO: could we re-use this.permissionDBManager.ensureWriteAccess(post, this.loginUser) here?
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(user.getName()) == false && this.loginUser.getRole() != Role.ADMIN)) {
			final String errorMsg = "user " + ((this.loginUser.getName() != null) ? this.loginUser.getName() : "anonymous") + " is not authorized to change user " + user.getName();
			log.warn(errorMsg);
			throw new ValidationException(errorMsg);
		}
		
		// update spammer settings 
		if ((user.getPrediction() != null || user.getSpammer() != null)) {
			// only admins are allowed to change spammer settings
			this.permissionDBManager.ensureAdminAccess(this.loginUser);
			DBSession session = this.openSession();
			
			String mode = this.adminDBManager.getClassifierSettings(ClassifierSettings.TESTING, session);
			return this.adminDBManager.flagSpammer(user, this.getAuthenticatedUser(), mode, session);			
		}
		
		return this.storeUser(user, true);
	}

	public String getAuthenticatedUser() {
		/* TODO: we should either rename this method to getAuthenticatedUserName or 
		 * return the user directly.
		 */
		return this.loginUser.getName();
	}

	public String addDocument(final Document doc, final String resourceHash) {
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(doc, this.loginUser);
		return this.storeDocument(doc, resourceHash).getFileHash();
	}

	/**
	 * TODO: this method is very probably broken and allows EVERYBODY to upload 
	 * documents for other users.
	 * 
	 * @param doc
	 * @param resourceHash 
	 * @return doc
	 */
	public Document storeDocument(final Document doc, final String resourceHash){
		final DBSession session = openSession();

		try {
			// create a DocumentParam object
			final DocumentParam docParam = new DocumentParam();
			docParam.setUserName(doc.getUserName());
			docParam.setResourceHash(resourceHash);
			docParam.setFileHash(doc.getFileHash());
			docParam.setFileName(doc.getFileName());

			// FIXME remove deprecated method
			final boolean valid = this.docDBManager.validateResource(docParam, session);
			final boolean existingDoc = this.docDBManager.checkForExistingDocuments(docParam, session);

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
					this.docDBManager.updateDocument(docParam, session);
				} else {
					//add
					this.docDBManager.addDocument(docParam, session);
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

	/**
	 * Returns the named document for the given userName and resourceHash.
	 */
	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		this.ensureLoggedIn();

		final DBSession session = openSession();
		try {
			/*
			 * we just forward this task to getPostDetails from the 
			 * BibTeXDatabaseManager and extract the documents.
			 */
			final Post<BibTex> post = this.bibtexDBManager.getPostDetails(this.loginUser.getName(), resourceHash, userName, UserUtils.getListOfGroupIDs(this.loginUser), session);
			if (post != null) {
				for (final Document document : post.getResource().getDocuments()) {
					if (document.getFileName().equals(fileName)) {
						return document;
					}
				}
			}
		} finally {
			session.close();
		}
		return null;
	}

	/**
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public void deleteDocument(final String userName, final String resourceHash, final String fileName){
		this.ensureLoggedIn();
		this.permissionDBManager.ensureWriteAccess(this.loginUser, userName);

		final DBSession session = openSession();
		try {
			// create a DocumentParam object
			final DocumentParam docParam = new DocumentParam();
			docParam.setFileName(fileName);
			docParam.setResourceHash(resourceHash);
			docParam.setUserName(userName);

			// FIXME: remove deprecated method
			final boolean valid = this.docDBManager.validateResource(docParam, session);

			/*
			 * valid means that the resource is a bibtex entry and the given user is
			 * the owner of this entry.
			 */
			if (valid){
				this.docDBManager.deleteDocument(docParam, session);
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

	public void addInetAddressStatus(final InetAddress address, final InetAddressStatus status) {
		this.ensureLoggedIn();
		// only admins are allowed to change the status of an address
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.addInetAddressStatus(address, status, session);
		} finally {
			session.close();
		}
	}

	public void deleteInetAdressStatus(final InetAddress address) {
		this.ensureLoggedIn();
		// only admins are allowed to change the status of an address
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.deleteInetAdressStatus(address, session);
		} finally {
			session.close();
		}
	}

	public InetAddressStatus getInetAddressStatus(final InetAddress address) {
		// everybody is allowed to ask for the status of an address
		/*
		 * TODO: is this really OK? At least it is neccessary, because otherwise the 
		 * RegistrationHandler can not check the status of an address.
		 */
//		this.ensureLoggedIn();
//		this.permissionDBManager.ensureAdminAccess(loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getInetAddressStatus(address, session);
		} finally {
			session.close();
		}
	}

	/** 
	 * Query statistical information
	 * 
	 * TODO: as soon as more statistics are added, a chain should be defined
	 * 
	 * @see org.bibsonomy.model.logic.LogicInterface#getStatistics(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, org.bibsonomy.common.enums.StatisticsConstraint, java.lang.String, List)
	 */
	public int getStatistics(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final StatisticsConstraint constraint, final String search, final List<String> tags) {
		final DBSession session = openSession();
		try {
			if (grouping.equals(GroupingEntity.USER) && groupingName != null && groupingName != "") {
				if (tags != null && tags.size() > 0) {
					return this.statisticsDBManager.getNumberOfResourcesForUserAndTags(resourceType, tags, groupingName, this.loginUser.getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);
				}
				return this.statisticsDBManager.getNumberOfResourcesForUser(resourceType, groupingName, this.loginUser.getName(), UserUtils.getListOfGroupIDs(this.loginUser), session);
			} else if (grouping.equals(GroupingEntity.GROUP) && groupingName != null && groupingName != "") {
				Group group = this.groupDBManager.getGroupByName(groupingName, session);
				if (group == null) {
					log.debug("group " + groupingName + " does not exist");
					return 0;
				}
				return this.statisticsDBManager.getNumberOfResourcesForGroup(resourceType, group.getGroupId(), UserUtils.getListOfGroupIDs(this.loginUser), session);
			} else if (grouping.equals(GroupingEntity.ALL) && tags != null) {
				return this.statisticsDBManager.getNumberOfResourcesForTags(resourceType, tags, UserUtils.getListOfGroupIDs(this.loginUser), session);
			} else {
				throw new RuntimeException("Can't handle statistics request");
			}
		} catch (final QueryTimeoutException ex) {
			// if a query times out, we return 0
			return 0;
		} finally {
			session.close();
		}
	}

	public List<Tag> getConcepts(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final ConceptStatus status, final int start, final int end) {
		final DBSession session = openSession();
		try {
			final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, this.loginUser.getName(), grouping, groupingName, tags, null, null, start, end, null, this.loginUser);
			param.setConceptStatus(status);
			return this.tagRelationsDBManager.getConcepts(param, session);
		} finally {
			session.close();
		}		
	}

	public Tag getConceptDetails(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		final DBSession session = openSession();
		final Tag concept;
		try {
			if (grouping.equals(GroupingEntity.USER) || grouping.equals(GroupingEntity.GROUP) && groupingName != null && groupingName != "") {
				concept = this.tagRelationsDBManager.getConceptForUser(conceptName, groupingName, session);
			} else if (grouping.equals(GroupingEntity.ALL)) {
				concept = this.tagRelationsDBManager.getGlobalConceptByName(conceptName, session);
			} else {
				throw new RuntimeException("Can't handle request");
			}
		} finally {
			session.close();
		}
		return concept;
	}

	public String createConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		return this.storeConcept(concept, grouping, groupingName, false);			
	}

	public void deleteConcept(final String concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}

		final DBSession session = openSession();
		this.tagRelationsDBManager.deleteConcept(concept, groupingName, session);
		// FIXME: close session?
	}

	public void deleteRelation(final String upper, final String lower, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}

		final DBSession session = openSession();
		this.tagRelationsDBManager.deleteRelation(upper, lower, groupingName, session);
		// FIXME: close session?
	}

	public String updateConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		if ((this.loginUser.getName() == null) || (this.loginUser.getName().equals(groupingName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		return this.storeConcept(concept, grouping, groupingName, true);
	}

	private String storeConcept(final Tag concept, final GroupingEntity grouping, final String groupingName, final boolean update) {
		final DBSession session = openSession();
		if (update) {
			this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
		} else {
			this.deleteConcept(concept.getName(), grouping, groupingName);
			this.tagRelationsDBManager.insertRelations(concept, groupingName, session);
		}
		return concept.getName();
	}

	/**
	 * retrieve related user
	 */
	public List<User> getUsers(final List<String> tags, final Order order, final int start, final int end) {
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, tags, null, order, start, end, null, loginUser);
		final DBSession session = openSession();
		try {
			return this.userDBManager.getUserByFolkrank(param, session);
		} finally {
			session.close();
		}
	}

	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int interval) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifiedUsers(classifier, status, interval, session);
		} finally {
			session.close();
		}
	}

	public String getClassifierSettings(final ClassifierSettings key) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierSettings(key, session);
		} finally {
			session.close();
		}
	}

	public void updateClassifierSettings(final ClassifierSettings key, final String value) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			this.adminDBManager.updateClassifierSettings(key, value, session);
		} finally {
			session.close();
		}
	}

	public int getClassifiedUserCount(final Classifier classifier, final SpamStatus status, final int interval) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifiedUserCount(classifier, status, interval, session);
		} finally {
			session.close();
		}
	}

	public List<User> getClassifierHistory(final String userName) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierHistory(userName, session);
		} finally {
			session.close();
		}
	}

	public List<User> getClassifierComparison(final int interval) {
		this.permissionDBManager.ensureAdminAccess(this.loginUser);
		final DBSession session = openSession();
		try {
			return this.adminDBManager.getClassifierComparison(interval, session);
		} finally {
			session.close();
		}
	}
}