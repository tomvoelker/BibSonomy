package org.bibsonomy.util;

import java.util.Collection;

import org.bibsonomy.common.enums.GroupID;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ValidationUtils {

	private static final ValidationUtils singleton = new ValidationUtils();

	private ValidationUtils() {
	}

	public static ValidationUtils getInstance() {
		return singleton;
	}

	public boolean present(final String s) {
		return ((s != null) && (s.trim().length() > 0));
	}

	public boolean present(final Collection<?> c) {
		return ((c != null) && (c.size() > 0));
	}

	public boolean present(final Object o) {
		return (o != null);
	}

	public boolean present(final GroupID gid) {
		return ((gid != null) && (gid != GroupID.INVALID));
	}

	public boolean presentValidGroupId(final int gid) {
		return (gid != GroupID.INVALID.getId());
	}

	public boolean nullOrEqual(final Object requested, final Object supported) {
		return ((requested == null) || (requested == supported));
	}
}