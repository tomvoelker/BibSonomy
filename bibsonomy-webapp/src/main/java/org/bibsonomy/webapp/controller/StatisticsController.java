package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.statistics.Statistics;
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
	public View workOn(StatisticsCommand command) {
		final Statistics stats;
		switch (command.getType()) {
		case USERS:
			stats = this.logic.getUserStatistics(null, null, -1);
			break;

		default:
			throw new UnsupportedOperationException(command.getType() + " is not supported");
		}
		
		command.setResponseString(String.valueOf(stats.getCount()));
		return Views.AJAX_JSON;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
