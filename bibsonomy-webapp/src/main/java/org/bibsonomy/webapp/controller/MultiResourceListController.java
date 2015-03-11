/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.MultiResourceViewCommand;

/**
 * controller for retrieving multiple windowed lists with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class MultiResourceListController extends ResourceListController {
	private static final Log log = LogFactory.getLog(MultiResourceListController.class);

	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 * @param resourceType 
	 */
	@SuppressWarnings("unchecked")
	protected void postProcessAndSortList(final MultiResourceViewCommand cmd, Class<? extends Resource> resourceType) {
		for (ListCommand<?> listCommand: cmd.getListCommand(resourceType)) {
			if (resourceType == BibTex.class) {
				// TODO: how can we do this in a clean way without SuppressWarnings?
				postProcessAndSortList(cmd, (List<Post<BibTex>>) listCommand.getList());	
			}
		}
	}

	/**
	 * retrieve a list of posts from the database logic and add them to the command object
	 * 
	 * @param <T> extends Resource
	 * @param <V> extends ResourceViewComand
	 * @param cmd the command object
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param tags 
	 * @param hash 
	 * @param order 
	 * @param filter 
	 * @param search 
	 * @param itemsPerPage number of items to be displayed on each page
	 */
	protected <T extends Resource> void addList(final MultiResourceViewCommand cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, String search, int itemsPerPage) {
		// new list command to put result list into
		final ListCommand<Post<T>> listCommand = new ListCommand<Post<T>>(cmd);
		// retrieve posts		
		log.debug("getPosts " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + itemsPerPage + " " + filter);
		listCommand.setList(this.logic.getPosts(resourceType, groupingEntity, groupingName, tags, hash, search,SearchType.LOCAL, Sets.<Filter>asSet(filter), order, null, null, listCommand.getStart(), listCommand.getStart() + itemsPerPage) );
		cmd.getListCommand(resourceType).add(listCommand);

		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
	}
	
	protected void addDescription(final MultiResourceViewCommand cmd, Class<? extends Resource> resourceType, String description) {
		cmd.getListsDescription(resourceType).add(description);
	}

}