package org.bibsonomy.database.systemstags.executable;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.SystemTagErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
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
public class ForGroupTag extends AbstractSystemTagImpl implements ExecutableSystemTag {
	
	private static final String NAME = "for";
	
	private DBSessionFactory dbSessionFactory = null;

	@Override
	public ForGroupTag newInstance() {
		return new ForGroupTag();
	}

	@Override
	public String getName() {
		return NAME;
	}

	public void setDBSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}
	
	@Override
	public <T extends Resource> void performBeforeCreate(final Post<T> post, final DBSession session) {
		log.debug("performing after access");
		// we assume, that the post itself is valid
		this.perform(post, post.getTags(), session);
	}
	
	@Override
	public <T extends Resource> void performAfterCreate(final Post<T> post, final DBSession session) {
		// nothing is performed after post is created
		log.debug("performing after access");
	}

	@Override
	public <T extends Resource> void performBeforeUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {
		if (operation == PostUpdateOperation.UPDATE_TAGS) {
			/*
			 *  in this case, newPost is not a valid post but contains the new tags, while oldPost is a valid post containing the old tags
			 */
			this.perform(oldPost, newPost.getTags(), session);
		} else {
			/*
			 *  in this case, newPost is a valid post containing the new tags
			 */
			this.perform(newPost, newPost.getTags(), session);
		}
	}

	@Override
	public <T extends Resource> void performAfterUpdate(Post<T> oldPost, final Post<T> newPost, final PostUpdateOperation operation, final DBSession session) {
		// nothing is performed after post was updated
		log.debug("performing after access");
	}

	/**
	 * Make post for the group and store it in the database
	 * @param <T>
	 * @param userPost the post to store (we ignore its tags)
	 * @param userTags the tags for the post
	 * @param session
	 */
	private <T extends Resource> void perform(Post<T> userPost, Set<Tag> userTags, final DBSession session) {
		log.debug("performing after access");
		final String groupName = this.getArgument(); // the group's name
		final String userName = userPost.getUser().getName();
		final String intraHash = userPost.getResource().getIntraHash();
		
		if (!this.hasPermissions(intraHash, session, groupName, userName)) {
			// user is not allowed to use this tag, errorMessages were added
			return;
		}
		/*
		 * Make a DBLogic for the group
		 */
		DBLogicNoAuthInterfaceFactory logicFactory = new DBLogicNoAuthInterfaceFactory();
		logicFactory.setDbSessionFactory(this.dbSessionFactory);
		LogicInterface groupDBLogic = logicFactory.getLogicAccess(groupName, "");
		/*
		 *  Check if the group exists and whether it owns the post already
		 */
		if (!present(groupDBLogic.getGroupDetails(groupName))) {
			String defaultMessage = this.getName()+": " + groupName + "does not exist.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.noSuchGroup", new String[] {groupName}));
			log.warn("Added SystemTagErrorMessage (for group: Unknown Group) for post " + intraHash);
			return; // this tag can not be used => abort
		}
		try {
			if (present( groupDBLogic.getPostDetails(intraHash, groupName) )) {
				log.debug("Given post already owned by group. Skipping...");
				return;
			}
		} catch (Exception ex) {
			// ignore
		}
		/*
		 *  Permissions are granted and the group doesn't own the post yet
		 *  => Copy the post and store it for the group
		 */
		//FIXME: How do we properly clone a post?
		Post<T> groupPost = new Post<T>();
		groupPost.setResource(userPost.getResource());
		groupPost.setDescription(userPost.getDescription());
		groupPost.setDate(new Date());
		groupPost.setUser(new User(groupName));
		/* 
		 * Copy Tags: 
		 * remove all systemTags to avoid any side effects and contradictions 
		 */
		final Set<Tag> groupTags = new HashSet<Tag>(userTags);
		SystemTagsUtil.removeAllSystemTags(groupTags);
		groupTags.add(new Tag("from:"+userName));
		groupPost.setTags(groupTags);
		/*
		 *  Copy Groups: the visibility of the postCopy is:
		 *  original == public => copy = public
		 *  original != public => copy = dbGroup
		 *  => check if post.groups has only the public group
		 */
		if (userPost.getGroups().size()==1 && userPost.getGroups().contains(GroupUtils.getPublicGroup())) {
			// public is the only group (if visibility was public, there should be only one group)
			groupPost.setGroups(new HashSet<Group>());
			groupPost.getGroups().add(GroupUtils.getPublicGroup());
		} else {
			// visibility is different from public => post is only visible for dbGroup
			groupPost.addGroup(groupName);
		}
		/*
		 * groupPost is complete and can be stored for the group
		 */
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		posts.add(groupPost);
		try {
			groupDBLogic.createPosts(posts);
		} catch (DatabaseException dbex) {
			/*
			 *  Add the DatabaseException of the copied post to the Exception of the original one
			 */
			for (String hash: dbex.getErrorMessages().keySet()) {
				for (ErrorMessage errorMessage: dbex.getErrorMessages(hash)) {
					errorMessage.setDefaultMessage("This error occured while executing the for: tag: "+errorMessage.getDefaultMessage());
					errorMessage.setErrorCode("database.exception.systemTag.forGroup.copy");
					session.addError(intraHash, errorMessage);
					log.warn("Added SystemTagErrorMessage (for group: errors while storing group's post) for post " + intraHash);
				}
			}
		}
		log.debug("copied post was stored successfully");
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
		PermissionDatabaseManager permissionDb = PermissionDatabaseManager.getInstance();
		if (permissionDb.isSpecialGroup(groupName) ) {
			final String defaultMessage = this.getName() + ": "+ groupName + ": is a special group. You are not allowed to forward posts to special groups.";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.specialGroup", new String[] {groupName}));
			log.warn("Added SystemTagErrorMessage (for group: permission denied) for post " + intraHash);
			return false;			
		} 
		if (!permissionDb.isMemberOfGroup(userName, groupName,  session) ) {
			final String defaultMessage =this.getName() + ": You are not a member of " + groupName + ".";
			session.addError(intraHash, new SystemTagErrorMessage(defaultMessage, "database.exception.systemTag.forGroup.member", new String[] {groupName}));
			log.warn("Added SystemTagErrorMessage (for group: not member) for post " + intraHash);
			return false;			
		}
		return true;
	}
}

