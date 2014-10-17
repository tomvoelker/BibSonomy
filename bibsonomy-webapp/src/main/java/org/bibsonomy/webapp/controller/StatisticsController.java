package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.StatisticsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

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
		final int count;
		SpamStatus spamStatus = null;
		final GroupingEntity grouping = command.getGrouping();
		if (command.isSpammers() && !command.isAll()) {
			spamStatus = SpamStatus.SPAMMER;
		}
		switch (command.getType()) {
		case USERS:
			count = this.logic.getUserStatistics(command.getContraint(), null, spamStatus, command.getInterval(), command.getUnit()).getCount();
			break;
		case TAGS:
			count = this.logic.getTagStatistics(null, grouping, null, null, null, null, null, null, 0, 1000);
			break;
		case POSTS:
			count = this.logic.getPostStatistics(command.getResourceType(), grouping, null, null, null, null, null, command.getContraint(), null, null, null, 0, 1000).getCount();
			break;
		default:
			throw new UnsupportedOperationException(command.getType() + " is not supported");
		}
		
		command.setResponseString(String.valueOf(count));
		return Views.AJAX_JSON;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
