package org.bibsonomy.webapp.command.events;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author rja
 * @version $Id: EventRegistrationCommand.java,v 1.1 2010-08-03 07:17:44 rja Exp
 *          $
 */
public class EventRegistrationCommand extends BaseCommand {

	private Event event;
	private User user;
	private String profilePrivlevel;

	private String badgename;
	private boolean registered;
	private boolean isPresenter;
	private boolean hasPoster;
	private String subEvent;
	private String address;

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getProfilePrivlevel() {
		return this.profilePrivlevel;
	}

	public void setProfilePrivlevel(String profilePrivlevel) {
		this.profilePrivlevel = profilePrivlevel;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean getRegistered() {
		return this.registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public String getSubEvent() {
		return this.subEvent;
	}

	public void setSubEvent(String subEvent) {
		this.subEvent = subEvent;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getHasPoster() {
		return this.hasPoster;
	}

	public void setHasPoster(boolean poster) {
		this.hasPoster = poster;
	}

	public String getBadgename() {
		return this.badgename;
	}

	public void setBadgename(String badgename) {
		this.badgename = badgename;
	}

	public boolean getIsPresenter() {
		return this.isPresenter;
	}

	public void setPresenter(boolean isPresenter) {
		this.isPresenter = isPresenter;
	}

}
