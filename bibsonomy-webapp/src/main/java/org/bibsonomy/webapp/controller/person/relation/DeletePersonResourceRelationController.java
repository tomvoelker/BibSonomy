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
package org.bibsonomy.webapp.controller.person.relation;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.person.relation.PersonResourceRelationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.springframework.validation.Errors;

/**
 * TODO: add documentetion to this controller
 *
 * @author tok
 */
public class DeletePersonResourceRelationController implements MinimalisticController<PersonResourceRelationCommand>, ErrorAware {
	private LogicInterface logic;
	private URLGenerator urlGenerator;
	private Errors errors;

	@Override
	public PersonResourceRelationCommand instantiateCommand() {
		return new PersonResourceRelationCommand();
	}

	@Override
	public View workOn(final PersonResourceRelationCommand command) {
		final String personId = command.getPerson().getPersonId();
		final PersonResourceRelationType typeToDelete = command.getType();
		final String interhashToDelete = command.getInterhash();
		final int indexToDelete = command.getIndex();

		/*
		 * check the ckey
		 */
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}

		try {
			this.logic.removeResourceRelation(personId, interhashToDelete, indexToDelete, typeToDelete);
		} catch (Exception e) {
			errors.reject("person.error.deleteRelation");
		}

		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getPersonUrl(personId));
		if (this.errors.hasErrors()) {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		} else {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "person.success.deleteRelation");
		}
		return redirect;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
