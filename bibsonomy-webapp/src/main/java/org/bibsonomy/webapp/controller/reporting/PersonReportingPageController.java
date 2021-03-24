package org.bibsonomy.webapp.controller.reporting;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.PersonReportingCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * reporting controller for persons
 *
 * - /reporting/persons
 *
 * @author pda
 * @author dzo
 */
public class PersonReportingPageController extends AbstractReportingPageController<PersonReportingCommand> {

	private String college;

	@Override
	protected PersonReportingCommand instantiateReportingCommand() {
		return new PersonReportingCommand();
	}

	@Override
	protected void workOn(PersonReportingCommand command, Person person, Group organization) {
		PersonQuery query = new PersonQuery(command.getQuery());
		query.setOrganization(organization);
		final ListCommand<Person> personListCommand = command.getPersons();
		final int start = personListCommand.getStart();
		query.setStart(start);
		query.setCollege(this.college);
		query.setEnd(start + personListCommand.getEntriesPerPage());
		query.setUsePrefixMatch(true);
		personListCommand.setList(this.logic.getPersons(query));
	}

	@Override
	protected View reportingView() {
		return Views.PERSONS_REPORTING;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}
}
