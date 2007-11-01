/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util;


/**
 * A minialistic controller that knows nothing about being invoked
 * by a servlet request or a testcase or whatever. It only communicates
 * via the command object (argument of {@link #workOn(Object)} and result
 * of {@link #instantiateCommand()}).
 * 
 * @param <T> type of the command object
 * 
 * @author Jens Illig
 */
public interface MinimalisticController<T> {
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
