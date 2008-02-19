package org.bibsonomy.util;

import java.util.Collection;

import org.bibsonomy.common.enums.GroupID;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ValidationUtils {

	/**
	 * @param string argument to check
	 * @return false iff the argument is null or has zero trimmed length
	 */
	public static boolean present(final String string) {
		return ((string != null) && (string.trim().length() > 0));
	}

	/**
	 * @param collection argument to check
	 * @return false iff the argument is null or has zero size
	 */
	public static boolean present(final Collection<?> collection) {
		return ((collection != null) && (collection.size() > 0));
	}

	/**
	 * @param object argument to check
	 * @return false iff the argument is null
	 */
	public static boolean present(final Object object) {
		return (object != null);
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
	 * @return true if the first argument requested is null or equals the second argument
	 */
	public static boolean nullOrEqual(final Object requested, final Object supported) {
		return ((requested == null) || (requested == supported));
	}
	
	
	/**
	 * Build this method to make it possible to have i.e. two different order values ADDED & FOLKRANK
	 * 
	 * @param requested
	 * @param supported1
	 * @param supported2
	 * @return true if the first argument requested is null or equals the second or third argument
	 */
	public static boolean nullOrEqual(final Object requested, final Object supported1, final Object supported2) {
		return ((requested == null) || (requested == supported1) || (requested == supported2));
	}
}