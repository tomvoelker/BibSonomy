package org.bibsonomy.database.systemstags.executable;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;

/**
 * System tag 'sys:for:&lt;groupname&gt;'
 * Description: 
 *   If user tags a post with [sys:]for:&lt;groupname&gt;, a copy of the resource
 *   is created which is owned by the group. Furthermore, the copied resource is tagged 
 *   with from:&lt;username&gt; instead of for:&lt;groupname&gt;.
 *   
 *  Precondition: 
 *   User is member of given group 
 * @author fei
 * @version $Id$
 */
public class ForGroupTag extends SystemTag {
	
	private static final Log log = LogFactory.getLog(ForGroupTag.class);
	private final PermissionDatabaseManager permissionDb; // needed to check permission to post for the given group

	/**
	 * Constructor
	 */
	public ForGroupTag() {
		log.debug("initializing");
		// get database manager singleton
		this.permissionDb = PermissionDatabaseManager.getInstance();
	}

	@Override
	public SystemTag newInstance() {
		return new ForGroupTag();
	}

	@Override
	public <T extends Resource> void performBeforeCreate(final Post<T> post, final DBSession session) {
		log.debug("performing after access");
		// we assume, that the post itself is valid and therefore perform therefore we do the same as in a regular update
		this.performBeforeUpdate(post, PostUpdateOperation.UPDATE_ALL, session);
	}
	
	@Override
	public <T extends Resource> void performAfterCreate(final Post<T> post, final DBSession session) {
		// nothing is performed after post is created
		log.debug("performing after access");
	}

	
	@Override
	public <T extends Resource> void performAfterUpdate(final Post<T> post, final PostUpdateOperation operation, final DBSession session) {
		// nothing is performed after post was updated
		log.debug("performing after access");
	}

	@Override
	public <T extends Resource> void performBeforeUpdate(final Post<T> post, final PostUpdateOperation operation, final DBSession session) {
		log.debug("performing after access");
		final String groupName = getValue(); // the group's name
		final String userName = post.getUser().getName();
		final String intraHash = post.getResource().getIntraHash();
		
		if (!this.hasPermissions(intraHash, session, groupName, userName)) {
			// user is not allowed to use this tag, errorMessages were added
			return;
		}
		/*
		 * Make a DBLogic for the group
		 */
		DBLogicNoAuthInterfaceFactory logicFactory = new DBLogicNoAuthInterfaceFactory();
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface groupDBLogic = logicFactory.getLogicAccess(groupName, "");
		/*
		 *  check if the group exists and whether it owns the post already
		 */
		if (!present(groupDBLogic.getGroupDetails(groupName))) {
			log.debug("Unknown group!");
			String defaultMessage = this.getName()+": " + groupName + "does not exist.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.noSuchGroup", new String[] {groupName}));
			return; // this tag can not be used => abort
		}
		if (present( groupDBLogic.getPostDetails(intraHash, groupName) )) {
			log.debug("Given post already owned by group. Skipping...");
			return;
		}
		/*
		 *  Permissions are granted and the group doesn't own the post yet
		 *  => copy/create post and store it for the group
		 */
		log.debug("Old post: "+post.toString());
		Post<? extends Resource> groupPost;
		if (operation == PostUpdateOperation.UPDATE_TAGS) {
			/*
			 *  We assume, that the post is not valid and contains only it's intraHash
			 *  therefore we retrive it from the DB first
			 */
			LogicInterface userDBLogic = logicFactory.getLogicAccess(userName, "");
			groupPost = userDBLogic.getPostDetails(intraHash, userName);
		} else {
			/*
			 *  We assume, that the post ist valid and most of it can be copied
			 */
			//FIXME: How do we properly clone a post?
			// This construction is very strange, but the newPost instance is needed to call the setResource method (Generics)
			Post<T> newPost = new Post<T>();
			newPost.setResource(post.getResource());
			groupPost=newPost;
			groupPost.setDescription(post.getDescription());
		}
		this.addPostDetails(post, groupPost, userName, groupName);
		log.debug("New post: "+groupPost.toString());
		/*
		 * groupPost is complete and can be stored for the group
		 */
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		posts.add(groupPost);
		try {
			groupDBLogic.createPosts(posts);
		} catch (DatabaseException dbex) {
			// add the DatabaseException of the copied post to the Exception of the original one
			for (String hash: dbex.getErrorMessages().keySet()) {
				for (ErrorMessage errorMessage: dbex.getErrorMessages(hash)) {
					errorMessage.setDefaultMessage("This error occured while executing the for: tag: "+errorMessage.getDefaultMessage());
					errorMessage.setErrorCode("database.exception.systemTag.forGroup.copy");
					session.addError(post.getResource().getIntraHash(), errorMessage);
				}
			}
		}
	}


	/**
	 * Checks the preconditions to this tags usage, adds errorMessages
	 * @param intraHash
	 * @param session
	 * @param groupName
	 * @param userName
	 * @return true iff user is allowed to use the tag
	 */
	private boolean hasPermissions(final String intraHash, final DBSession session, final String groupName, final String userName) {
		if (permissionDb.isSpecialGroup(groupName) ) {
			final String defaultMessage = this.getName() + ": "+ groupName + ": is a special group. You are not allowed to forward posts to special groups.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.specialGroup", new String[] {groupName}));
			return false;			
		} 
		if (!permissionDb.isMemberOfGroup(userName, groupName,  session) ) {
			final String defaultMessage =this.getName() + ": You are not a member of " + groupName + ".";
				session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.member", new String[] {groupName}));
			return false;			
		}
		return true;
	}


	/**
	 * Adds important details to the groupPost
	 * @param <T>
	 * @param userPost the post that was tagged with for:...
	 * @param groupPost the post we want to give to the group
	 * @param userName 
	 * @param groupName
	 */
	private <T extends Resource> void addPostDetails(Post<T> userPost, Post<?> groupPost, String userName, String groupName) {
		groupPost.setDate(new Date());
		groupPost.setUser(new User(groupName));
		/* 
		 * Copy Tags: 
		 * remove all systemTags to avoid any side effects and contradictions 
		 */
		final Set<Tag> groupTags = new HashSet<Tag>(userPost.getTags());
		getSystemTagFactory().removeAllSystemTags(groupTags);
		groupTags.add(new Tag("from:"+userName));
		groupPost.setTags(groupTags);
		/*
		 *  2. Groups: the visibility of the postCopy is:
		 *  original == public => copy = public
		 *  original != public => copy = dbGroup
		 *  => check if post.groups has only the public group
		 */
		// TODO: Find a better way to check for "public" (e.g. via GroupUtils)
		if (userPost.getGroups().size()==1 && userPost.getGroups().contains(GroupUtils.getPublicGroup())) {
			// public is the only group (if visibility was public, there should be only one group)
			groupPost.setGroups(new HashSet<Group>());
			groupPost.getGroups().add(GroupUtils.getPublicGroup());
		} else {
			// visibility is different from public => post is only visible for dbGroup
			groupPost.addGroup(groupName);
		}
	}

}

