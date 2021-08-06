/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for history page of posts
 *
 * responsible for the flowing urls
 *
 * - /history/RESOURCE_CLASS/HASH/USER
 * - /history/RESOURCE_CLASS/HASH
 *
 * @author pba
 * @author Nasim Nabavi
 * @param <R>
 */
public class PostHistoryController<R extends Resource> extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<R>> {

	@Override
	public ResourcePageCommand<R> instantiateCommand() {
		return new ResourcePageCommand<>();
	}

	@Override
	public View workOn(final ResourcePageCommand<R> command) {
		final String format = command.getFormat();
		this.startTiming(format);

		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		final String requUser = command.getRequestedUser();
		final Class<R> resourceClass = command.getResourceClass();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;

		final ListCommand<Post<R>> resourceCommand = command.getListCommand(resourceClass);
		this.setList(command, resourceClass, groupingEntity, requUser, null, longHash, null, FilterEntity.HISTORY, null, command.getStartDate(), command.getEndDate(), resourceCommand.getEntriesPerPage());

		this.postProcessAndSortList(command, resourceClass);
		if (!present(resourceCommand.getList())) {
			return Views.ERROR;
		}

		// redirect to the correct view
		if ("html".equals(format)) {
			this.endTiming();
			return Views.HISTORY;
		}

		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}
}
