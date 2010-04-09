package org.bibsonomy.testutil;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.params.beans.TagIndex;

/**
 * @author dzo
 * @version $Id$
 */
public final class DBTestUtils {
	
	/**
	 * builds a list of tag indices string 1 gets index 1, string 2 index 2 and so on
	 * @param tagsString
	 * @return a list of tag indices
	 */
	public static List<TagIndex> getTagIndex(String... tagsString) {
		final List<TagIndex> indexList = new LinkedList<TagIndex>();
		
		DBTestUtils.addToTagIndex(indexList, tagsString);
		
		return indexList;
	}

	/**
	 * adds tag indices to the indexList at the end of the list starting by index size of list + 1
	 * @param indexList
	 * @param tagsString
	 */
	public static void addToTagIndex(final List<TagIndex> indexList, String... tagsString) {
		if (indexList != null) {
			int index = indexList.size() + 1;
			for (final String tagString : tagsString) {
				indexList.add(new TagIndex(tagString, index++));
			}
		}
	}
}
