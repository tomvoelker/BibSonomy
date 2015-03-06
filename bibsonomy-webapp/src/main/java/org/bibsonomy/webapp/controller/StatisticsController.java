package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.Set;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.StatisticsUnit;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.StatisticsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
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
		if (!Role.ADMIN.equals(command.getContext().getLoginUser().getRole())) {
			throw new AccessDeniedException("only admins can retrieve stats");
		}
		final int count;
		SpamStatus spamStatus = null;
		final GroupingEntity grouping = command.getGrouping();
		if (command.isSpammers() && !command.isAll()) {
			spamStatus = SpamStatus.SPAMMER;
		}
		final Set<StatisticsConstraint> contraints = command.getContraints();
		final Integer interval = command.getInterval();
		final StatisticsUnit unit = command.getUnit();
		final Date startDate = convertToStartDate(interval, unit);
		final FilterEntity filter = command.getFilter();
		
		switch (command.getType()) {
		case USERS:
			count = this.logic.getUserStatistics(contraints, null, spamStatus, interval, unit).getCount();
			break;
		case TAGS:
			count = this.logic.getTagStatistics(command.getResourceType(), grouping, null, null, null, command.getConceptStatus(), null, contraints, startDate, null, 0, 1000);
			break;
		case POSTS:
			count = this.logic.getPostStatistics(command.getResourceType(), grouping, null, null, null, null, filter, contraints, null, startDate, null, 0, 1000).getCount();
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
		final Date startDate;
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
			startDate = dateTime.toDate();
		} else {
			startDate = null;
		}
		return startDate;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
