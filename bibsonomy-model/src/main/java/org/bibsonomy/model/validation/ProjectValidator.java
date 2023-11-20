/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.model.cris.Project;

import java.util.LinkedList;
import java.util.List;

/**
 * validator for a {@link Project}
 * @author dzo
 */
public class ProjectValidator {

	/**
	 * validates a project for all required fields
	 * @param project
	 * @return a list of validation errors
	 */
	public List<ErrorMessage> validateProject(final Project project) {
		final List<ErrorMessage> errorMessages = new LinkedList<>();

		if (!present(project.getTitle())) {
			errorMessages.add(new MissingFieldErrorMessage("title"));
		}

		if (!present(project.getStartDate())) {
			errorMessages.add(new MissingFieldErrorMessage("startDate"));
		}

		if (!present(project.getEndDate())) {
			errorMessages.add(new MissingFieldErrorMessage("endDate"));
		}

		if (!present(project.getExternalId())) {
			errorMessages.add(new MissingFieldErrorMessage("internalId"));
		}

		return errorMessages;
	}
}
