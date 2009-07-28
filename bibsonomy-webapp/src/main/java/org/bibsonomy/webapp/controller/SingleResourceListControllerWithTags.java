package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
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
	 * @param <T> extends Resource, the resource type
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param regex regular expression for tag filtering
	 * @param tags list of tags
	 * @param start start parameter
	 * @param end end parameter
	 **/
	protected <T extends Resource, V extends TagResourceViewCommand> void setRelatedTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, int start, int end, String search) {
		RelatedTagCommand relatedTagCommand = cmd.getRelatedTagCommand();
		relatedTagCommand.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, null, order, start, end, search, null));		
	}
	
	/**
	 * Retrieve a set of similar tags
	 * 
	 * @param <T>
	 * @param <V>
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
	protected <T extends Resource, V extends TagResourceViewCommand> void setSimilarTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, int start, int end, String search) {
		RelatedTagCommand similarTags = cmd.getSimilarTags();
		similarTags.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, null, order, start, end, search, TagSimilarity.COSINE));		
	}
	
	
	/**
	 * Count the number of "normal" (i.e., non-system) tags
	 * within a list of tags
	 * 
	 * @param tags - a list of tag strings
	 * @return - the number of non-system tags
	 */
	protected int countNonSystemTags(List<String> tags) {
		int numNonSysTags = 0;
		for (String tag : tags) {
			if (tag != null && !SystemTagsUtil.isSystemtag(tag)) {
				numNonSysTags++;
			}			
		}
		return numNonSysTags;
	}
}
