/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.webapp.controller.ajax;


import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPublicationCommand;
import org.bibsonomy.webapp.controller.PersonPageController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * TODO: document controller
 *
 * œauthor mho
 */
public class PersonPublicationAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPublicationCommand> {

	private LogicInterface adminLogic;

	@Override
	public AjaxPersonPublicationCommand instantiateCommand() {
		return new AjaxPersonPublicationCommand();
	}

	@Override
	public View workOn(final AjaxPersonPublicationCommand command) {
		final String requestedPersonId = command.getRequestedPersonId();

		final Person person = this.getPersonById(requestedPersonId);
		if (!present(person)) {
			return Views.AJAX_ERRORS;
		}

		final int postsPerPage = command.getPersonPostsPerPage();
		final int start = postsPerPage * command.getPage();
		PersonPageController.fillCommandWithPersonResourceRelations(this.logic, command, person, start, postsPerPage);

		return Views.AJAX_PERSON_PUBLICATIONS;
	}

	private Person getPersonById(final String requestedPersonId) {
		try {
			/*
			 * get the person; if person with the requested id was merged with another person, this method
			 * throws a ObjectMovedException and the wrapper would render the redirect, that we do not want
			 */
			return this.logic.getPersonById(PersonIdType.PERSON_ID, requestedPersonId);

		} catch (final ObjectMovedException e) {
			final String newPersonId = e.getNewId();
			return this.logic.getPersonById(PersonIdType.PERSON_ID, newPersonId);
		}
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
