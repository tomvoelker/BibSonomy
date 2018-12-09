package org.bibsonomy.webapp.controller.reporting;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.reporting.PersonReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

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
		List<Person> personList = logic.getPersons(query);
		command.setPersonList(personList);
		if (ValidationUtils.present(command.getFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}
		return Views.PERSONS_REPORTING;
	}
}
