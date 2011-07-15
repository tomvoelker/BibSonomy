package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
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
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Nils Raabe
 * @version $Id$
 */

public class TaggedFriendPageController extends SingleResourceListControllerWithTags implements MinimalisticController<TaggedFriendResourceViewCommand> {

	private static final Log log = LogFactory.getLog(TaggedFriendPageController.class);

	@Override
	public View workOn(final TaggedFriendResourceViewCommand command) {
		
		log.debug(this.getClass().getSimpleName());
		
		if (!command.getContext().isUserLoggedIn()){
			log.info("Trying to access a taggedfriend page without being logged in");
			return new ExtendedRedirectView("/login");
		}
		
		final String requestedUserRelation 		= command.getRequestedUserRelation();
		final String requestedTags				= command.getRequestedTags();
		final User loginUser 					= command.getContext().getLoginUser();
		final String format 					= command.getFormat();
		final List<String> requestedUserTags 	= command.getRequestedTagsList();
		final GroupingEntity groupingEntity 	= GroupingEntity.USER;
		List<Post<Bookmark>> bookmarksPosts 	= new ArrayList<Post<Bookmark>>();
		List<Post<BibTex>> bibTexPosts 			= new ArrayList<Post<BibTex>>();

		
		// if no Userrelation given -> error
		if (!present(requestedUserRelation)) {
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}	
		
		// get tagged friends
		List<User> relatedUsers = logic.getUserRelationship(loginUser.getName(), UserRelation.OF_FRIEND, "sys:relation:" + requestedUserRelation);
		
		// if no friends are in this relation -> error
		if(relatedUsers.size() == 0) {
			throw new MalformedURLSchemeException("error.no_friends_in_this_friendrelation");
		}
		
		// get all bookmarks and bibtex posts for the requested tag - if no tag given -> relationTags is an empty List
		List<String> relationTags = new ArrayList<String>();
		if(!requestedTags.isEmpty()) {
			relationTags.add(requestedTags);
		}
		
		// Add for every User the Bookmarks and Bibtexs 
		for(User user : relatedUsers) {
			bookmarksPosts.addAll( 	logic.getPosts(Bookmark.class, groupingEntity, user.getName(), relationTags, null, Order.ADDED, null, 0, 20, null));
			bibTexPosts.addAll( 	logic.getPosts(BibTex.class,   groupingEntity, user.getName(), relationTags, null, Order.ADDED, null, 0, 20, null));
		}
		
		// Set all parameters in the command.	 
		// set page title
		command.setPageTitle("taggedfriend :: " + requestedUserRelation);
		
		//set the related Users
		command.setRelatedUsers(relatedUsers);
		
		//set the related Bookmarks
		command.setBmPosts(bookmarksPosts);
		
		//set the related Publications
		command.setBibPosts(bibTexPosts);
		
		
		// retrieve and set the requested resource lists and tags for every user
		for(User user : relatedUsers) {
			for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {			
				final ListCommand<?> listCommand = command.getListCommand(resourceType);
				final int entriesPerPage = listCommand.getEntriesPerPage();
				this.setList(command, resourceType, groupingEntity, user.getName(), relationTags, null, null, null, null, entriesPerPage);
				this.postProcessAndSortList(command, resourceType);
				System.out.println(resourceType.toString());
			}
			
			this.setTags(command, Resource.class, groupingEntity, user.getName(), null, requestedUserTags, null, 20, null);

			if (requestedUserTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, user.getName(), null, requestedUserTags, Order.ADDED, 0, 20, null);
			}
		}
				
		return Views.TAGGEDFRIENDPAGE;
	}
	
	
	@Override
	public TaggedFriendResourceViewCommand instantiateCommand() {
		return new TaggedFriendResourceViewCommand();
	}

}
