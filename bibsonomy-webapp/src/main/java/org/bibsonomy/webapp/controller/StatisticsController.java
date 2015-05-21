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

import java.util.Date;
import java.util.Set;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsUnit;
import org.bibsonomy.common.enums.UserFilter;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.StatisticsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.joda.time.DateTime;
import org.springframework.security.access.AccessDeniedException;

/**
 * command to return statistics about the system
 * 
 * @author dzo
 */
public class StatisticsController implements MinimalisticController<StatisticsCommand> {
	
	private LogicInterface logic;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public StatisticsCommand instantiateCommand() {
		return new StatisticsCommand();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final StatisticsCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(command.getContext().getLoginUser().getRole())) {
			throw new AccessDeniedException("only admins can retrieve stats");
		}
		final int count;
		SpamStatus spamStatus = null;
		final GroupingEntity grouping = command.getGrouping();
		final Set<Filter> filters = command.getFilters();
		if (command.isSpammers() && !command.isAll()) {
			spamStatus = SpamStatus.SPAMMER;
			filters.add(UserFilter.SPAMMER);
		}
		final Integer interval = command.getInterval();
		final StatisticsUnit unit = command.getUnit();
		final Date startDate = convertToStartDate(interval, unit);
		
		switch (command.getType()) {
		case USERS:
			count = this.logic.getUserStatistics(grouping, filters, null, spamStatus, startDate, null, null, null).getCount();
			break;
		case TAGS:
			count = this.logic.getTagStatistics(command.getResourceType(), grouping, null, null, null, command.getConceptStatus(), filters, startDate, null, 0, 1000);
			break;
		case POSTS:
			count = this.logic.getPostStatistics(command.getResourceType(), grouping, null, null, null, null, filters, null, startDate, null, 0, 1000).getCount();
			break;
		case DOCUMENTS:
			count = this.logic.getDocumentStatistics(grouping, null, filters, startDate, null).getCount();
			break;
		default:
			throw new UnsupportedOperationException(command.getType() + " is not supported");
		}
		
		command.setResponseString(String.valueOf(count));
		return Views.AJAX_JSON;
	}

	/**
	 * @param interval
	 * @param unit
	 * @return
	 */
	private static Date convertToStartDate(final Integer interval, final StatisticsUnit unit) {
		if (present(interval)) {
			DateTime dateTime = new DateTime();
			final int intervalAsInt = interval.intValue();
			switch (unit) {
			case HOUR:
				dateTime = dateTime.minusHours(intervalAsInt);
				break;
			case MONTH:
				dateTime = dateTime.minusMonths(intervalAsInt);
				break;
			default:
				throw new IllegalArgumentException(unit.toString());
			}
			return dateTime.toDate();
		}
		return null;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
