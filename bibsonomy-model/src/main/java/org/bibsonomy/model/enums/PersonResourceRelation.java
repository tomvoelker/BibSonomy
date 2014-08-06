package org.bibsonomy.model.enums;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.Resource;

/**
 * relations that may hold between a {@link Person} and a {@link Resource}
 *
 * @author jil
 */
public enum PersonResourceRelation {
	/**
	 * Author
	 */
	AUTHOR,
	/**
	 * thesis advisor
	 */
	THESIS_ADVISOR,
	/**
	 * reviewer of a thesis
	 */
	THESIS_REVIEWER,
	/**
	 * some non-specific relation influence
	 */
	OTHER;
}
