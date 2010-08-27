package org.bibsonomy.events.database;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;

public class RegistrationParam {

	private User user;
	private Event event;
	private String subEvent;
	private String address;
	private String badgename;
	private String badgeInstitutionName;
	private boolean isPresenter;
	private boolean hasPoster;
	
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
	public String getBadgename() {
	    return badgename;
	}
	public void setBadgename(String badgename) {
	    this.badgename = badgename;
	}
	public boolean getIsPresenter() {
	    return isPresenter;
	}
	public void setPresenter(boolean isPresenter) {
	    this.isPresenter = isPresenter;
	}
	public boolean isHasPoster() {
	    return hasPoster;
	}
	public void setHasPoster(boolean hasPoster) {
	    this.hasPoster = hasPoster;
	}
	public String getBadgeInstitutionName() {
	    return badgeInstitutionName;
	}
	public void setBadgeInstitutionName(String badgeInstitutionName) {
	    this.badgeInstitutionName = badgeInstitutionName;
	}

	
}
