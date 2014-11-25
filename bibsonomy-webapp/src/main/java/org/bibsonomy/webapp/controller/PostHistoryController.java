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

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author pba
 */
public class PostHistoryController extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<BibTex>> {

	
	@Override
	public ResourcePageCommand<BibTex> instantiateCommand() {
		return new ResourcePageCommand<BibTex>();
	}

	@Override
	public View workOn(ResourcePageCommand<BibTex> command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;
		

		final FilterEntity filter = FilterEntity.POSTS_HISTORY_BIBTEX;
		Class<? extends Resource> resourceType = BibTex.class;

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity,requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

		if ("html".equals(format)) {
			this.endTiming();	
			return Views.POSTHISTORYPAGE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

}
