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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.function.Function;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.reporting.ReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * abstract controller for reporting pages
 *
 * @author dzo
 */
public abstract class AbstractReportingPageController<C extends ReportingCommand> implements MinimalisticController<C> {

	protected static <T, C> T getField(C command, Function<C, T> fieldAccessor, Function<T, String> checker) {
		final T field = fieldAccessor.apply(command);
		if (present(checker.apply(field))) {
			return field;
		}

		return null;
	}

	protected LogicInterface logic;

	@Override
	public final C instantiateCommand() {
		final C reportingCommand = this.instantiateReportingCommand();
		reportingCommand.setOrganization(new Group());
		reportingCommand.setPerson(new Person());
		return reportingCommand;
	}

	protected abstract C instantiateReportingCommand();

	@Override
	public final View workOn(final C command) {
		final Person person = getField(command, ReportingCommand::getPerson, Person::getPersonId);
		final Group organization = getField(command, ReportingCommand::getOrganization, Group::getName);

		this.workOn(command, person, organization);

		if (present(command.getDownloadFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}

		if (present(person)) {
			final Person personById = this.logic.getPersonById(PersonIdType.PERSON_ID, person.getPersonId());
			command.setPerson(personById);
		}

		if (present(organization)) {
			final Group groupDetails = this.logic.getGroupDetails(organization.getName(), false);
			command.setOrganization(groupDetails);
		}

		return this.reportingView();
	}

	protected abstract void workOn(C command, Person person, Group organization);

	protected abstract View reportingView();

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
