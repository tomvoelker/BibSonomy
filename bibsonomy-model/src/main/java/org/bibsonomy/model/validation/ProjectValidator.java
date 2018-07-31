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
			errorMessages.add(new MissingFieldErrorMessage("externalId"));
		}

		if (!present(project.getType())) {
			errorMessages.add(new MissingFieldErrorMessage("type"));
		}

		return errorMessages;
	}
}
