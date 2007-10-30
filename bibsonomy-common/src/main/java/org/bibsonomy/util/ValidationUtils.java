package org.bibsonomy.util;

import java.util.Collection;

import org.bibsonomy.common.enums.GroupID;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ValidationUtils {

	/**
	 * @param s argument to check
	 * @return false iff the argument is null or has zero trimmed length
	 */
	public static boolean present(final String s) {
		return ((s != null) && (s.trim().length() > 0));
	}
	
	/**
	 * @param c argument to check
	 * @return false iff the argument is null or has zero size
	 */
	public static boolean present(final Collection<?> c) {
		return ((c != null) && (c.size() > 0));
	}

	/**
	 * @param o argument to check
	 * @return false iff the argument is null
	 */
	public static boolean present(final Object o) {
		return (o != null);
	}

	/**
	 * @param gid argument to check
	 * @return false iff the argument is null or has invalid value
	 */
	public static boolean present(final GroupID gid) {
		return ((gid != null) && (gid != GroupID.INVALID));
	}

	/**
	 * @param gid argument to check
	 * @return false iff the argument is null or has invalid value
	 */
	public static boolean presentValidGroupId(final int gid) {
		return (gid != GroupID.INVALID.getId());
	}

	/**
	 * @param requested argument to check
	 * @param supported reference argument for comparison
	 * @return true iff the first argument requested is null or equals the second argument
	 */
	public static boolean nullOrEqual(final Object requested, final Object supported) {
		return ((requested == null) || (requested == supported));
	}
}