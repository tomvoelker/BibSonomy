package org.bibsonomy.database.systemstags.executable;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ErrorSource;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;

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
		String groupName = getValue();
		try {
			getPermissionDb().ensureMemberOfNonSpecialGroup(post.getUser().getName(), groupName, session);
		} catch (ValidationException ve) {
			ErrorMessage errorMessage = new ErrorMessage(ErrorSource.SYSTEM_TAG, ve.getMessage());
			session.addError(post.getResource().getIntraHash(), errorMessage);
			// the user can not use this tag at all, therefore we omit trying anything else with this tag
			return;
		}
		
		
		// get group and corresponding user
		Group dbGroup = getLogicInterface().getGroupDetails(groupName);
		if( dbGroup==null ) {
			log.debug("Unknown group!");
			ErrorMessage errorMessage = new ErrorMessage(ErrorSource.SYSTEM_TAG, "You can not use "+this.getName()+" because the group does not exist.");
			session.addError(post.getResource().getIntraHash(), errorMessage);
			// the user can not use this tag at all, therefore we omit trying anything else with this tag
			return;
		}
		User dbUser = getLogicInterface().getUserDetails(groupName);
		
		//--------------------------------------------------------------------
		// check if post is already owned by group
		//--------------------------------------------------------------------
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		List<String> tags = new LinkedList<String>();
		List<Post<T>> groupPosts = getLogicInterface().getPosts(
				(Class<T>)post.getResource().getClass(), groupingEntity, groupName, tags, 
				post.getResource().getIntraHash(), null, null, 0, Integer.MAX_VALUE, "");
		log.debug("Got " + groupPosts.size() + " posts for group "+groupName);
		// skip this post if it is already owned by given group
		if( groupPosts.size()>0 ) {
			log.debug("Given post already owned by group. Skipping...");
		} else {
			//----------------------------------------------------------------
			// post is not owned by group -> create a copy
			//----------------------------------------------------------------
			log.debug("Old post: "+post.toString());
			// alter tags: replace for:group by from:user
			Set<Tag> tagsCopy = new HashSet<Tag>(post.getTags());
			replaceTags(tagsCopy, getTag().getName(), "from:"+post.getUser().getName());
			// FIXME: should other system tags be executed or removed???\
			//        We have to consider possible side effects
			SystemTagFactory.removeSystemTag(tagsCopy, getName());
			Set<Group> groupsCopy = new HashSet<Group>();
			groupsCopy.add(dbGroup);
			// FIXME: how do we properly clone a post?
			Post<T> postCopy = new Post();
			postCopy.setContentId(post.getContentId());
			postCopy.setDate(post.getDate());
			postCopy.setDescription(post.getDescription());
			postCopy.setGroups(groupsCopy);
			postCopy.setResource(post.getResource());
			postCopy.setUser(dbUser);
			postCopy.setTags(tagsCopy);
			
			log.debug("New post: "+postCopy.toString());
			
			// Now store copied post - we have to create our own database session, 
			// as new post has to be owned by given group.
			// FIXME: this is ugly!
			DBLogicNoAuthInterfaceFactory logicFactory = new DBLogicNoAuthInterfaceFactory();
			logicFactory.setDbSessionFactory(getDbSessionFactory());
			LogicInterface groupDBLogic = logicFactory.getLogicAccess(groupName, "");
			List<Post<?>> posts = new LinkedList<Post<?>>();
			posts.add(postCopy);
			groupDBLogic.createPosts(posts);
		}

		// all done.
		return;
	}



	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	public PermissionDatabaseManager getPermissionDb() {
		return permissionDb;
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

}
