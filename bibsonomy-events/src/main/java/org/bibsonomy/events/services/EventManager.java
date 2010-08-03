package org.bibsonomy.events.services;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;

/**
 * The manager is responsible for registering, querying, removing, etc. events.
 * 
 * @author rja
 *
 */
public interface EventManager {

	/**
	 * Returns the event with the given name. 
	 * If no such event exists, <code>null</code> is returned.
	 * 
	 * @param name
	 * @return
	 */
	public Event getEvent(final String name);
	
	
	/**
	 * Registers the given user for the given event/subevent. 
	 * 
	 * @param user
	 * @param event
	 * @param subEvent
	 */
	public void registerUser(final User user, final Event event, final String subEvent, final String address);
}
