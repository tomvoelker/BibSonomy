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

import java.util.Set;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author pba
 * @author Nasim Nabavi
 */
public class PostHistoryController <R extends Resource> extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<R>> {

	
	@Override
	public ResourcePageCommand<R> instantiateCommand() {
		return new ResourcePageCommand<R>();
	}

	@Override
	public View workOn(ResourcePageCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		final String requUser = command.getRequestedUser();
		final String resourceClassName = command.getResourceClass();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;
		

		final FilterEntity filter = FilterEntity.POSTS_HISTORY;
		Class<? extends Resource> resourceClass = null;
		if (resourceClassName.equals("bibtex")) {
			resourceClass = BibTex.class;
		} else if(resourceClassName.equals("bookmark")) {
			resourceClass = Bookmark.class;
		}
		else if(resourceClassName.equals("goldstandardpublication")){
			resourceClass = GoldStandardPublication.class;
		}
		else if(resourceClassName.equals("goldstandardbookmark")){
			resourceClass = GoldStandardBookmark.class;
		}
	
		this.setList(command, resourceClass, groupingEntity, requUser, null, longHash, null, filter, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceClass).getEntriesPerPage());
		this.postProcessAndSortList(command, resourceClass);

		//redirect to HistoryBM.jspx or HistoryBib.jspx
		if ("html".equals(format)) {
			this.endTiming();	
			if(BibTex.class.equals(resourceClass)){
				return Views.HISTORYBIB;
			}
			else if(GoldStandardPublication.class.equals(resourceClass)){
				return Views.HISTORYGOLDBIB;
			}
			else if(GoldStandardBookmark.class.equals(resourceClass)){
				return Views.HISTORYGOLDBM;
			}
			else{
				return Views.HISTORYBM;
			}
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

}
