package org.bibsonomy.util;

import java.util.Collection;

import org.bibsonomy.common.enums.GroupID;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ValidationUtils {

	public static boolean present(final String s) {
		return ((s != null) && (s.trim().length() > 0));
	}

	public static boolean present(final Collection<?> c) {
		return ((c != null) && (c.size() > 0));
	}

	public static boolean present(final Object o) {
		return (o != null);
	}

	public static boolean present(final GroupID gid) {
		return ((gid != null) && (gid != GroupID.INVALID));
	}

	public static boolean presentValidGroupId(final int gid) {
		return (gid != GroupID.INVALID.getId());
	}

	public static boolean nullOrEqual(final Object requested, final Object supported) {
		return ((requested == null) || (requested == supported));
	}
}