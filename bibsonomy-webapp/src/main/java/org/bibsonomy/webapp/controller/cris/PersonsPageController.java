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

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.PersonsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * the controller for
 * - /persons
 *
 * if the system is configured to cris mode or genealogy is deactivated
 *
 * @author dzo
 */
@Getter
@Setter
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
		final Prefix prefix = command.getPrefix();
		final int personListStart = personListCommand.getStart();

		final PersonQueryBuilder queryBuilder = new PersonQueryBuilder()
				.search(search)
				.byCollege(!isUserLoggedin || !command.isShowAllPersons() ? this.crisCollege : null)
				.byPrefix(prefix)
				.prefixMatch(true)
				.start(personListStart)
				.end(personListStart + personListCommand.getEntriesPerPage())
				.sortBy(present(search) ? PersonSortKey.RANK : PersonSortKey.MAIN_NAME_LAST_NAME)
				.orderBy(present(search) ? SortOrder.DESC : SortOrder.ASC);

		final List<Person> persons = this.logicInterface.getPersons(queryBuilder.build());
		personListCommand.setList(persons);

		return Views.PERSON_INTRO;
	}

}
