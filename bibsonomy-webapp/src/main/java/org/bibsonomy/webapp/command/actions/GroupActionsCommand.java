package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author schwass
 * @version $Id$
 */
public abstract class GroupActionsCommand extends BaseCommand {

	/**
	 * @return a String that represents the query
	 */
	public String toQueryString() {
		return "";
	}
}
