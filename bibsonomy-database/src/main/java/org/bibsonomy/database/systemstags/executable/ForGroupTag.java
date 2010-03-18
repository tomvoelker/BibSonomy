package org.bibsonomy.database.systemstags.executable;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	//------------------------------------------------------------------------
	/**
	 * This database manager is needed to ensure that a user is allowed to write
	 * posts to given group.
	 */
	private final PermissionDatabaseManager permissionDb;

	/**
	 * Constructor
	 */
	public ForGroupTag() {
		log.debug("initializing");
		// initialize database manager
		this.permissionDb = PermissionDatabaseManager.getInstance();
	}

	@Override
	public SystemTag newInstance() {
		return new ForGroupTag();
	}

	@Override
	public <T extends Resource> void performAfter(Post<T> post, final DBSession session) {
		log.debug("performing after access");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Resource> void performBefore(Post<T> post, final DBSession session) {
		log.debug("performing before acess");
		//--------------------------------------------------------------------
		// first check preconditions: user is member of given group
		//--------------------------------------------------------------------
		final String groupName = getValue();
		if (permissionDb.isSpecialGroup(groupName) ) {
			this.setError(Reason.SPECIAL, post, groupName, session);
			// the user can not use this tag at all, therefore we omit trying anything else with this tag
			return;			
		} 
		if (!permissionDb.isMemberOfGroup(post.getUser().getName(), groupName,  session) ) {
			this.setError(Reason.MEMBER, post, groupName, session);
			// the user can not use this tag at all, therefore we omit trying anything else with this tag
			return;			
		}
		
		/*
		 * Make a DBLogic for the group
		 */
		DBLogicNoAuthInterfaceFactory logicFactory = new DBLogicNoAuthInterfaceFactory();
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface groupDBLogic = logicFactory.getLogicAccess(groupName, "");
		
		// get group and corresponding user
		Group dbGroup = groupDBLogic.getGroupDetails(groupName);
		if( dbGroup == null ) {
			log.debug("Unknown group!");
			this.setError(Reason.EXIST, post, groupName, session);
			// the user can not use this tag at all, therefore we omit trying anything else with this tag
			return;
		}
		
		//--------------------------------------------------------------------
		// check if post is already owned by group
		//--------------------------------------------------------------------
		//final GroupingEntity groupingEntity = GroupingEntity.USER;
		//List<String> tags = new LinkedList<String>();
		/*
		 * FIXME: use getPostDetails() instead
		 */
		//getPosts((Class<T>)post.getResource().getClass(), groupingEntity, groupName, tags, post.getResource().getIntraHash(), null, null, 0, Integer.MAX_VALUE, "");
		//log.debug("Got " + groupPosts.size() + " posts for group "+groupName);
		// skip this post if it is already owned by given group
		//if( groupPosts.size()>0 ) {
		if(groupDBLogic.getPostDetails(post.getResource().getIntraHash(), groupName)!=null) {
			log.debug("Given post already owned by group. Skipping...");
		} else {
			//----------------------------------------------------------------
			// post is not owned by group -> create a copy
			//----------------------------------------------------------------
			log.debug("Old post: "+post.toString());
			// alter tags: replace for:group by from:user
			final Set<Tag> tagsCopy = new HashSet<Tag>(post.getTags());
			replaceTags(tagsCopy, getTag().getName(), "from:"+post.getUser().getName());
			// FIXME: should other system tags be executed or removed???\
			//        We have to consider possible side effects
			getSystemTagFactory().removeAllSystemTags(tagsCopy);
			/*
			 *  the visibility of the postCopy is:
			 *  original == public => copy = public
			 *  original != public => copy = dbGroup
			 *  => check if post.groups has only the public group
			 */
			Set<Group> groupsCopy = new HashSet<Group>();
			// TODO: Find a better way to check for "public" (e.g. via GroupUtils)
			if (post.getGroups().size()==1 && post.getGroups().contains(GroupUtils.getPublicGroup())) {
				// public is the only group (if visibility was public, there should be only one group)
				groupsCopy.add(GroupUtils.getPublicGroup());
			} else {
				// visibility is different from public => post is only visible for dbGroup
				groupsCopy.add(dbGroup);
			}
			// FIXME: how do we properly clone a post?
			final Post<T> postCopy = new Post();
			postCopy.setDate(post.getDate());
			postCopy.setDescription(post.getDescription());
			postCopy.setGroups(groupsCopy);
			postCopy.setResource(post.getResource());
			postCopy.setUser(new User(groupName));
			postCopy.setTags(tagsCopy);
			
			log.debug("New post: "+postCopy.toString());

			final List<Post<?>> posts = new LinkedList<Post<?>>();
			posts.add(postCopy);
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

		// all done.
		return;
	}



	/**
	 * Removes all tags with given old name and adds new tag with given new name.
	 * 
	 * @param tags
	 * @param oldName
	 * @param newName
	 */
	private void replaceTags(Set<Tag> tags, String oldName, String newName) {
		Collection<Tag> toRemove = new HashSet<Tag>();
		for( Tag tag : tags ) {
			if( tag.getName()==oldName )
				toRemove.add(tag);
		}
		tags.removeAll(toRemove);
		tags.add(new Tag(newName));
	}

	/**
	 * creates an errorMessage and adds it to the database exception in the session
	 * @param reason
	 * @param post
	 * @param groupName
	 * @param session
	 */
	private void setError(Reason reason, Post<? extends Resource> post, String groupName, DBSession session){
		String error=this.getName()+": ";
		String localizedMessageKey="";
		switch(reason) {
			case SPECIAL: {
				error+=groupName+" is a special group. You are not allowed to forward posts to special groups.";
				localizedMessageKey = "database.exception.systemTag.forGroup.specialGroup";
				break;
			}
			case EXIST: {
				error+=groupName+"does not exist.";
				localizedMessageKey = "database.exception.systemTag.forGroup.noSuchGroup";
				break;
			}
			case MEMBER: {
				error+="You are not a member of "+groupName+".";
				localizedMessageKey = "database.exception.systemTag.forGroup.member";
				break;
			}
		}
		session.addError(post.getResource().getIntraHash(), new SystemTagErrorMessage(error, localizedMessageKey, new String[] {groupName}));
	}

	/**
	 * small enum, to reduce code around the creation of errorMessages
	 * @author sdo
	 *
	 */
	private enum Reason {
		SPECIAL,
		EXIST,
		MEMBER;		
	}
}

