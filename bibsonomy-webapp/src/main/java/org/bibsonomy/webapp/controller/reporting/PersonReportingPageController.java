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
package org.bibsonomy.webapp.controller.reporting;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
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
		final ListCommand<Person> personListCommand = command.getPersons();

		final PersonQueryBuilder queryBuilder = new PersonQueryBuilder()
				.search(command.getQuery())
				.byOrganization(organization)
				.byCollege(this.college)
				.start(personListCommand.getStart())
				.end(personListCommand.getStart() + personListCommand.getEntriesPerPage())
				.prefixMatch(true);

		personListCommand.setList(this.logic.getPersons(queryBuilder.build()));
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
