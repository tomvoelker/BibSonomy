package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * A minialistic controller that knows nothing about being invoked
 * by a servlet request or a testcase or whatever. It only communicates
 * via the command object (argument of {@link #workOn(BaseCommand)} and result
 * of {@link #instantiateCommand()}).
 * 
 * @param <T> type of the command object
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface MinimalisticController<T extends BaseCommand> {
	/**
	 * @return a command object to be filled by the framework
	 */
	public T instantiateCommand();
	
	/**
	 * @param command a command object initialized by the framework based on
	 *                the parameters of som request-event like a http-request
	 * @return some symbol that describes the next state of the
	 *         application (the view)
	 */
	public View workOn(T command);
}
