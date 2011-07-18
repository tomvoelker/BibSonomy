package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TaggedFriendResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * controller responsible for the following pages:
 * 		- /taggedfriend/RELATION
 * 		- /taggedfriend/RELATION/TAG
 * 
 * @author Nils Raabe
 * @version $Id$
 */
public class TaggedFriendPageController extends SingleResourceListControllerWithTags implements MinimalisticController<TaggedFriendResourceViewCommand> {

	private static final Log log = LogFactory.getLog(TaggedFriendPageController.class);

	@Override
	public View workOn(final TaggedFriendResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()){
			throw new AccessDeniedException("please log in");
		}
		
		final String requestedUserRelation 		= command.getRequestedUserRelation();
		final String requestedTags				= command.getRequestedTags();
		final User loginUser 					= context.getLoginUser();
		final String format 					= command.getFormat();
		final List<String> requestedUserTags 	= command.getRequestedTagsList();
		final GroupingEntity groupingEntity 	= GroupingEntity.USER;
		final List<Post<Bookmark>> bookmarksPosts = new ArrayList<Post<Bookmark>>();
		final List<Post<BibTex>> bibTexPosts = new ArrayList<Post<BibTex>>();

		
		// if no Userrelation given -> error
		if (!present(requestedUserRelation)) {
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}
		
		// get tagged friends
		final List<User> relatedUsers = this.logic.getUserRelationship(loginUser.getName(), UserRelation.OF_FRIEND, SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, requestedUserRelation));
		
		// if no friends are in this relation -> error
		if (!present(relatedUsers)) {
			throw new MalformedURLSchemeException("error.no_friends_in_this_friendrelation");
		}
		
		// get all bookmarks and publication posts for the requested tag - if no tag given -> relationTags is an empty List
		final List<String> relationTags = new ArrayList<String>();
		if (!present(requestedTags.isEmpty())) {
			relationTags.add(requestedTags);
		}
		
		// retrieve and set the requested resource lists and tags for every user
		for (final User user : relatedUsers) {
			final String userName = user.getName();
			bookmarksPosts.addAll(this.logic.getPosts(Bookmark.class, groupingEntity, userName, relationTags, null, Order.ADDED, null, 0, 20, null));
			bibTexPosts.addAll(this.logic.getPosts(BibTex.class, groupingEntity, userName, relationTags, null, Order.ADDED, null, 0, 20, null));
			/*
			 * FIXME: you are overriding each resource list and every tag list
			 * in every iteration of this loop! this means that you only get
			 * the posts and tags of the last user
			 * hint: chain element GetResourcesByTaggedUserRelation must be called!
			 */
			for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {			
				final ListCommand<?> listCommand = command.getListCommand(resourceType);
				final int entriesPerPage = listCommand.getEntriesPerPage();
				this.setList(command, resourceType, groupingEntity, userName, relationTags, null, null, null, null, entriesPerPage);
				this.postProcessAndSortList(command, resourceType);
			}
			
			this.setTags(command, Resource.class, groupingEntity, userName, null, requestedUserTags, null, 20, null);

			if (present(requestedUserTags)) {
				this.setRelatedTags(command, Resource.class, groupingEntity, userName, null, requestedUserTags, Order.ADDED, 0, 20, null);
			}
		}
		
		// Set all parameters in the command.	 
		// set page title TODO: i18n
		command.setPageTitle("taggedfriend :: " + requestedUserRelation);
		
		// set the related users
		command.setRelatedUsers(relatedUsers);
		
		// set the related bookmarks
		command.setBmPosts(bookmarksPosts);
		
		// set the related publications
		command.setBibPosts(bibTexPosts);
				
		return Views.TAGGEDFRIENDPAGE;
	}
	
	
	@Override
	public TaggedFriendResourceViewCommand instantiateCommand() {
		return new TaggedFriendResourceViewCommand();
	}

}
