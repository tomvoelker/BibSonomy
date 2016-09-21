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

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.DaysSystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.webapp.command.MultiResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for the popular page:
 * 
 * @author mwa
 */
public class PopularPageController extends MultiResourceListController implements MinimalisticController<MultiResourceViewCommand>{	
	private static final int MAX_TAGS = 50;
	
	
	private int entriesPerPage;

	/**
	 * @param entriesPerPage the entriesPerPage to set
	 */
	public void setEntriesPerPage(final int entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}

	@Override
	public View workOn(final MultiResourceViewCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		// XXX: only html format, exports are not possible atm 
		if (!"html".equals(format)) {
			throw new UnsupportedMediaTypeException(format + " not supported");
		}
		
		// set the grouping entity and the order
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		final Order order = Order.POPULAR;
		
		// the start parameter for OFFSET
		int begin = 0; 
		
		// the value of the field 'popular_days' in the database
		int days = 0;

		do {
			for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
				// build day systemtag
				final List<String> tags = Collections.singletonList(SystemTagsUtil.buildSystemTagString(DaysSystemTag.NAME, Integer.valueOf(begin)));
				// determine the value of popular days, e.g. the last 10 days
				days = this.logic.getPostStatistics(resourceType, groupingEntity, null, tags, null, null, null, order, command.getStartDate(), command.getEndDate(), 0, this.entriesPerPage).getCount();
				
				// only retrieve and set the requested resource lists if days > 0
				// because otherwise the lists will be empty
				if (days > 0) {
					// retrieve and set the requested resource lists
					this.addList(command, resourceType, groupingEntity, null, tags, null, order, null, null, this.entriesPerPage);
					// FIXME: do this only once outside the "days"-loop
					this.postProcessAndSortList(command, resourceType);
					this.addDescription(command, resourceType, days + "");
				}
			}
			
			begin++;
		} while (days > 0 && begin < 10); // show max 10 entries
		
		this.setTags(command, Resource.class, groupingEntity, null, null, null, null, null, MAX_TAGS, null);
		command.setPageTitle("popular"); // TODO: i18n
		
		this.endTiming();
		return Views.POPULAR;
	}
	
	@Override
	protected int getFixedTagMax(final int tagMax) {
		return MAX_TAGS;
	}
	
	@Override
	public MultiResourceViewCommand instantiateCommand() {
		return new MultiResourceViewCommand();
	}

}
