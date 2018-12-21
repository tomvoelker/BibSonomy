package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.webapp.command.ListCommand;

public class PersonReportingCommand extends ReportingCommand {
	private final ListCommand<Person> persons = new ListCommand<>(this);
	private String query;
	private Group organization;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String getFilename() {
		return "persons";
	}

	public ListCommand<Person> getPersons() {
		return persons;
	}

	public Group getOrganization() {
		return organization;
	}

	public void setOrganization(Group organization) {
		this.organization = organization;
	}
}
