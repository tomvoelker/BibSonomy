package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.Person;

import java.util.List;

public class PersonReportingCommand extends ReportingCommand {
	private List<Person> personList;
	private String query;

	public List<Person> getPersonList() {
		return personList;
	}

	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}

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
}
