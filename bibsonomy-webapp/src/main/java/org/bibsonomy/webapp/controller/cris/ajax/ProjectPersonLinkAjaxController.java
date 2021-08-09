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
package org.bibsonomy.webapp.controller.cris.ajax;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.cris.ajax.ProjectPersonLinkAjaxCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for linking projects and persons
 * FIXME: add more error handling
 *
 * @author dzo
 */
public class ProjectPersonLinkAjaxController extends AjaxController implements MinimalisticController<ProjectPersonLinkAjaxCommand> {

	@Override
	public ProjectPersonLinkAjaxCommand instantiateCommand() {
		return new ProjectPersonLinkAjaxCommand();
	}

	@Override
	public View workOn(final ProjectPersonLinkAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			return this.getErrorView();
		}

		if (!context.isValidCkey()) {
			return this.getErrorView();
		}

		final CRISLink link = new CRISLink();
		link.setLinkType(command.getLinkType());
		final Project project = new Project();
		project.setExternalId(command.getProjectId());
		link.setSource(project);
		final Person person = new Person();
		person.setPersonId(command.getPersonId());
		link.setTarget(person);

		// FIXME: check jobresult
		this.logic.createCRISLink(link);

		command.setResponseString("{}");
		return Views.AJAX_JSON;
	}
}
