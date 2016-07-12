/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import java.util.Date;
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
	protected void setRelatedTags(final TagResourceViewCommand cmd, Class<? extends Resource> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Date startDate, Date endDate, Order order, int start, int end, String search) {
		final RelatedTagCommand relatedTagCommand = cmd.getRelatedTagCommand();
		relatedTagCommand.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, tags, null, search, regex, null, order, startDate, endDate, start, end));		
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
	protected void setSimilarTags(final TagResourceViewCommand cmd, Class<? extends Resource> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, final Date startDate, final Date endDate, int start, int end, String search) {
		final RelatedTagCommand similarTags = cmd.getSimilarTags();
		similarTags.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, tags, null, search, regex, TagSimilarity.COSINE, order, startDate, endDate, start, end));		
	}

}
