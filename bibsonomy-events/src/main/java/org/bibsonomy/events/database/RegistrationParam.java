package org.bibsonomy.events.database;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;

public class RegistrationParam {

	private User user;
	private Event event;
	private String subEvent;
	private String address;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getSubEvent() {
		return subEvent;
	}
	public void setSubEvent(String subEvent) {
		this.subEvent = subEvent;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	
}
