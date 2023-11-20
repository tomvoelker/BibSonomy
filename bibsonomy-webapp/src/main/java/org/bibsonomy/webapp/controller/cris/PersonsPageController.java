/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.PersonsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * the controller for
 * - /persons
 *
 * if the system is configured to cris mode
 *
 * @author dzo
 */
public class PersonsPageController implements MinimalisticController<PersonsPageCommand> {

	private LogicInterface logicInterface;
	private String crisCollege;

	@Override
	public PersonsPageCommand instantiateCommand() {
		final PersonsPageCommand personsPageCommand = new PersonsPageCommand();
		personsPageCommand.getPersons().setEntriesPerPage(30);
		return personsPageCommand;
	}

	@Override
	public View workOn(final PersonsPageCommand command) {
		final boolean isUserLoggedin = command.getContext().isUserLoggedIn();

		final ListCommand<Person> personListCommand = command.getPersons();
		final String search = command.getSearch();
		final PersonQuery query = new PersonQuery(search);
		query.setUsePrefixMatch(true);
		query.setPrefix(command.getPrefix());
		final int personListStart = personListCommand.getStart();
		query.setStart(personListStart);
		query.setEnd(personListStart + personListCommand.getEntriesPerPage());
		query.setOrder(present(search) ? null : PersonSortKey.MAIN_NAME_LAST_NAME);
		query.setUsePrefixMatch(true);
		if (!isUserLoggedin || !command.isShowAllPersons()) {
			query.setCollege(this.crisCollege);
		}

		final List<Person> persons = this.logicInterface.getPersons(query);
		personListCommand.setList(persons);

		return Views.PERSON_INTRO;
	}

	/**
	 * @param logicInterface the logicInterface to set
	 */
	public void setLogicInterface(LogicInterface logicInterface) {
		this.logicInterface = logicInterface;
	}

	/**
	 * @param crisCollege the crisCollege to set
	 */
	public void setCrisCollege(String crisCollege) {
		this.crisCollege = crisCollege;
	}

}
