package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Person;

public class DeletePublicationFromPersonCommand extends BaseCommand {

	private Person person;

	private String intrahash;

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getIntrahash() {
		return this.intrahash;
	}

	public void setIntrahash(String intrahash) {
		this.intrahash = intrahash;
	}
}
