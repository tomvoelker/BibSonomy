package org.bibsonomy.model.util;

/**
 * Place for static util methods regarding multiple types of resources.
 */
public class ResourceUtils {

	/** To set groupId in case of spam detection. Use logical OR to set 2nd bit */
	private static final int CONST_SET_1ST_BIT = 0x80000000;
	/** To set/clear first bit of an integer. Use logical AND to clear 2nd bit */
	private static final int CONST_CLEAR_1ST_BIT = 0x7FFFFFFF;

	/**
	 * merges spaminformation into the groupId (MSB set iff isSpammer == true)
	 * @param groupId the original groupId
	 * @param isSpammer
	 * @return groupId with potentially modified MSB
	 */
	public static int getGroupId(final int groupId, final boolean isSpammer) {
		if (isSpammer) return groupId | CONST_SET_1ST_BIT;
		return groupId & CONST_CLEAR_1ST_BIT;
	}
}