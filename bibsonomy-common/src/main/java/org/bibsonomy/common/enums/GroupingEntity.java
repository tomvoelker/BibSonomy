package org.bibsonomy.common.enums;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedGroupingException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public enum GroupingEntity {

	USER, GROUP, VIEWABLE, ALL, FRIEND;

	/**
	 * Returns the corresponding GroupingEntity-enum for the given string.
	 */
	public static GroupingEntity getGroupingEntity(final String groupingEntity) {
		if (groupingEntity == null) throw new InternServerException("GroupingEntity is null");
		final String entity = groupingEntity.toLowerCase().trim();
		if ("user".equals(entity)) {
			return USER;
		} else if ("group".equals(entity)) {
			return GROUP;
		} else if ("friend".equals(entity)) {
			return FRIEND;
		} else if ("viewable".equals(entity)) {
			return VIEWABLE;
		} else if ("all".equals(entity)) {
			return ALL;
		} else {
			throw new UnsupportedGroupingException(groupingEntity);
		}
	}
}