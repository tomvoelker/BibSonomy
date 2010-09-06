package org.bibsonomy.webapp.command.events;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.events.model.ParticipantDetails;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author rja
 * @version $Id$
 */
public class EventRegistrationCommand extends BaseCommand {

	private Event event;
	private User user;
	private String profilePrivlevel;
	private ParticipantDetails participantDetails;

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

	public ParticipantDetails getParticipantDetails() {
		return this.participantDetails;
	}

	public void setParticipantDetails(ParticipantDetails participantDetails) {
		this.participantDetails = participantDetails;
	}
}
