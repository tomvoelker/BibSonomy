package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.RelatedTagCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;

/**
 * Convenience class to provide the functionality of setting related tags
 * to all controllers handling tags (e.g. userPageController, GroupPageController, ...)
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class SingleResourceListControllerWithTags extends SingleResourceListController {
	
	/**
     * Retrieve a set of related tags to a list of given tags 
     * from the database logic and add them to the command object
     * 
	 * @param cmd the command
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param regex regular expression for tag filtering
	 * @param tags list of tags
	 * @param start start parameter
	 * @param end end parameter
	 **/
	protected void setRelatedTags(final TagResourceViewCommand cmd, Class<? extends Resource> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, int start, int end, String search) {
		final RelatedTagCommand relatedTagCommand = cmd.getRelatedTagCommand();
		relatedTagCommand.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, null, order, start, end, search, null));		
	}
	
	/**
	 * Retrieve a set of similar tags
	 * 
	 * @param cmd
	 * @param resourceType
	 * @param groupingEntity
	 * @param groupingName
	 * @param regex
	 * @param tags
	 * @param order
	 * @param start
	 * @param end
	 * @param search
	 */
	protected void setSimilarTags(final TagResourceViewCommand cmd, Class<? extends Resource> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, int start, int end, String search) {
		final RelatedTagCommand similarTags = cmd.getSimilarTags();
		similarTags.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, null, order, start, end, search, TagSimilarity.COSINE));		
	}

}
