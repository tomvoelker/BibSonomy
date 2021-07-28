package org.bibsonomy.database.managers.util.cris;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Linkable;

/**
 * checks the link direction between a {@link Group} and a {@link Person}
 * @author dzo
 */
public class GroupPersonLinkDirectionChecker implements LinkDirectionChecker {

	@Override
	public boolean requiresSwap(final Linkable source, final Linkable target) {
		if (checkType(source) && checkType(target)) {
			if (source instanceof Person) {
				return true;
			}
		}

		return false;
	}

	private boolean checkType(final Linkable linkable) {
		return linkable instanceof Group || linkable instanceof Person;
	}
}
