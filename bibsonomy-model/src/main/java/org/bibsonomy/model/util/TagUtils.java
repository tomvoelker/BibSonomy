package org.bibsonomy.model.util;

import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class TagUtils {

	/**
	 * Get the maximum user count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum user count
	 */
	public static int getMaxUserCount(List<Tag> tags) {
		int maxUserCount = 0;
		for (final Tag tag : tags) {
			if (tag.getUsercount() > maxUserCount) {
				maxUserCount = tag.getUsercount();
			}
		}
		return maxUserCount;
	}

	/**
	 * Get the maximum global count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum global count
	 */
	public static int getMaxGlobalcountCount(List<Tag> tags) {
		int maxGlobalCount = 0;
		for (final Tag tag : tags) {
			if (tag.getGlobalcount() > maxGlobalCount) {
				maxGlobalCount = tag.getGlobalcount();
			}
		}
		return maxGlobalCount;
	}
}