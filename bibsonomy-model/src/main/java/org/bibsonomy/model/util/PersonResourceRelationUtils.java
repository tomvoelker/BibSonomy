package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Optional;

import org.bibsonomy.model.ResourcePersonRelation;

/**
 * util functions for person resource relations
 *
 * @author dzo
 */
public final class PersonResourceRelationUtils {
	private PersonResourceRelationUtils() {
		// noop
	}

	/**
	 * get a relation for a specified index
	 * @param relations
	 * @param index
	 * @return the relation
	 */
	public static Optional<ResourcePersonRelation> getRelationForIndex(final List<ResourcePersonRelation> relations, final int index) {
		if (!present(relations)) {
			return Optional.empty();
		}
		return relations.stream().filter(relation -> relation.getPersonIndex() == index).findFirst();
	}
}
