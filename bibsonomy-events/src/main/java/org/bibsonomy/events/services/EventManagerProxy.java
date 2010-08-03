package org.bibsonomy.events.services;

import java.util.List;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;

/**
 * For each method, loops through the list of available event managers
 * and tries to execute the method on the current manager. The first
 * manager that does not throw an {@link UnsupportedOperationException}
 * does the job. If no such manager is found, an 
 * {@link UnsupportedOperationException} is thrown.
 * 
 * 
 * @author rja
 *
 */
public class EventManagerProxy implements EventManager {

	private List<EventManager> eventManagers;
	
	@Override
	public Event getEvent(String name) {
		for (final EventManager eventManager : eventManagers) {
			try {
				return eventManager.getEvent(name);
			} catch (UnsupportedOperationException e) {
				// try next event manager in list
			}
		}
		throw new UnsupportedOperationException("No event manager found to handle this call.");
	}

	public List<EventManager> getEventManagers() {
		return eventManagers;
	}

	public void setEventManagers(List<EventManager> eventManagers) {
		this.eventManagers = eventManagers;
	}

	@Override
	public void registerUser(User user, Event event, String subEvent, String address) {
		for (final EventManager eventManager : eventManagers) {
			try {
				eventManager.registerUser(user, event, subEvent, address);
				return;
			} catch (UnsupportedOperationException e) {
				// try next event manager in list
			}
		}
		throw new UnsupportedOperationException("No event manager found to handle this call.");
	}
}
