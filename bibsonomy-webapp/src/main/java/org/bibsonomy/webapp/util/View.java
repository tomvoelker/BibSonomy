/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util;

/**
 * interface of symbols describing an representational applicationstate.
 * This is the communication protocoll between a controller and a viewresolver,
 * which maps view symbols to renderers  
 * 
 * @author Jens Illig
 */
public interface View {
	/**
	 * @return the name of the view. may be used by a viewresolver to resolve
	 *         the renderer to be used
	 */
	public String getName();
}
