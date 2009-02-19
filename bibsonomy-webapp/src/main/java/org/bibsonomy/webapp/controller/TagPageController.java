package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.RelatedUserCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for tag pages
 * /tag/TAGNAME
 * 
 * 
 * @author Michael Wagner
 * @version $Id$
 */

public class TagPageController extends SingleResourceListControllerWithTags implements MinimalisticController<TagResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(TagPageController.class);
	
	public View workOn(final TagResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// if no tags given return
		if (command.getRequestedTags() == null || command.getRequestedTags().length() == 0) {
			LOGGER.error("Invalid query /tag without tag");
			throw new MalformedURLSchemeException("error.tag_page_without_tag");
		}
		
		final List<String> requTags = command.getRequestedTagsList();
		
		// retrieve only tags of the user
		if (command.getRestrictToTags()) {
			/*
			 * get only the tags of the user
			 * TODO: minFreq is in HTML pages handled in the view, i.e., we get ALL tags
			 * and prune in the JSP. This is not done in the JSON view ... to be done! 
			 */
			this.setTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, null, null, 0, Integer.MAX_VALUE, null);
			
			// TODO: other output formats
			return Views.JSON;
		}	
		
		// requested order
		Order order = Order.ADDED;
		try {
			order = Order.getOrderByName(command.getOrder());
		} catch (IllegalArgumentException ex) {
			// TODO: why rethrowing the exception and not just don't catch it?
			throw new MalformedURLSchemeException(ex.getMessage());
		}

				
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		int totalNumPosts = 1; 
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();

			this.setList(command, resourceType, GroupingEntity.ALL, null, requTags, null, order, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
			
			this.setTotalCount(command, resourceType, GroupingEntity.ALL, null, requTags, null, null, null, null, entriesPerPage, null);
			totalNumPosts += listCommand.getTotalCount();
		}	
		
		/*
		 *  if order = folkrank - retrieve related users
		 *  
		 *  TODO: in practice, this is (currently) only neccessary for HTML and SWRC 
		 *  (burst, publrss, swrc) related pages
		 */
		if (order.equals(Order.FOLKRANK)) {
			this.setRelatedUsers(command, requTags, order, 0, Parameters.NUM_RELATED_USERS);
		}
		
		// html format - retrieve relted tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.setRelatedTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, order, 0, Parameters.NUM_RELATED_TAGS, null);
			// similar tags only make sense for a single requested tag
			if (command.getRequestedTagsList().size() == 1) {
				this.setSimilarTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, order, 0, Parameters.NUM_RELATED_TAGS, null);
			}
			// set total nr. of posts 
			command.getRelatedTagCommand().setTagGlobalCount(totalNumPosts);
			this.endTiming();
			return Views.TAGPAGE;			
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
		
	}
	
	public TagResourceViewCommand instantiateCommand() {
		return new TagResourceViewCommand();
	}
		
	/**
	 * retrieve related user by tag
	 * 
	 * @param <T>
	 * @param <V>
	 * @param cmd
	 * @param tags
	 * @param order
	 * @param start
	 * @param end
	 */
	protected <T extends Resource, V extends TagResourceViewCommand> void setRelatedUsers(V cmd, List<String> tags, Order order, int start, int end) {
		RelatedUserCommand relatedUserCommand = cmd.getRelatedUserCommand();
		relatedUserCommand.setRelatedUsers(this.logic.getUsers(tags,order,start,end));
	}
	
}
