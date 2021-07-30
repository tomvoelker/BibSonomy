package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.Person;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * @author pda
 */
public class PersonReportingCommand extends ReportingCommand {
	private final ListCommand<Person> persons = new ListCommand<>(this);
	private String query;

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String getFilename() {
		return "persons";
	}

	/**
	 * @return the persons
	 */
	public ListCommand<Person> getPersons() {
		return persons;
	}
}
