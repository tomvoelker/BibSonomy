package org.bibsonomy.webapp.controller.reporting;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.webapp.command.reporting.PersonReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
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
public class PersonReportingPageController implements MinimalisticController<PersonReportingCommand> {

	private LogicInterface logic;

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	@Override
	public PersonReportingCommand instantiateCommand() {
		return new PersonReportingCommand();
	}

	@Override
	public View workOn(PersonReportingCommand command) {
		PersonQuery query = new PersonQuery(command.getQuery());
		query.setOrganization(command.getOrganization());
		query.setStart(command.getPersons().getStart());
		query.setEnd(command.getPersons().getStart() + command.getPersons().getEntriesPerPage());
		command.getPersons().setList(this.logic.getPersons(query));

		if (present(command.getDownloadFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}

		return Views.PERSONS_REPORTING;
	}
}
