package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
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
		final StatisticsConstraint contraint = command.getContraint();
		switch (command.getType()) {
		case USERS:
			count = this.logic.getUserStatistics(contraint, null, spamStatus, command.getInterval(), command.getUnit()).getCount();
			break;
		case TAGS:
			count = this.logic.getTagStatistics(command.getResourceType(), grouping, null, null, null, null, contraint, null, null, 0, 1000);
			break;
		case POSTS:
			count = this.logic.getPostStatistics(command.getResourceType(), grouping, null, null, null, null, null, contraint, null, null, null, 0, 1000).getCount();
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
