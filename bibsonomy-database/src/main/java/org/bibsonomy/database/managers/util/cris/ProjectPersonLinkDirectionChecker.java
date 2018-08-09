package org.bibsonomy.database.managers.util.cris;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

/**
 * checks the link direction of a {@link Project} and {@link Person} link
 * @author dzo
 */
public class ProjectPersonLinkDirectionChecker implements LinkDirectionChecker {

	@Override
	public boolean requiresSwap(Linkable source, Linkable target) {
		if (checkType(source) && checkType(target)) {
			if (source instanceof Person) {
				return true;
			}
		}
		return false;
	}

	private boolean checkType(Linkable linkable) {
		return linkable instanceof Project || linkable instanceof Person;
	}
}
