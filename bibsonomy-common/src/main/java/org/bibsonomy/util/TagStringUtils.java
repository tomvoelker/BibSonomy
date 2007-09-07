package org.bibsonomy.util;

import java.util.regex.Pattern;

public class TagStringUtils {

	/** Allows in a string of tags to change the delimiter to space. Additionally, tags consisting of
	 * more than one word (separated by whitespace) can be joined with whitespaceSub.
	 * 
	 *  Example:
	 *  
	 *  with the delimiter = "," and whitespace = "_"
	 *  the string 
	 *    computer algebra, maple, math
	 *  would be changed to
	 *    computer_algebra maple math
	 *  
	 * @param tagstring - a string of tags.
	 * @param substitute - <code>true</code>, if the tag string should be modified.
	 * @param delimiter - the character which separates the tags in the tag string.
	 * @param whitespaceSub - the character with which whitespace should be separated. 
	 * This allows to merge tags with more than one word into a tag with one word.
	 *   
	 * @return The cleaned string of tags.
	 */
	public static String cleanTags(String tagstring, boolean substitute, String delimiter, String whitespaceSub) {
		if (tagstring != null) {
			if (substitute && delimiter != null && delimiter.length() == 1 && !delimiter.trim().equals("")) {
				String tmpTags = tagstring.trim();

				// remove whitespace around delimiter
				tmpTags = tmpTags.replaceAll("\\s*" + Pattern.quote(delimiter) + "\\s*", delimiter);

				// substitute whitespace inside tags
				if (whitespaceSub != null && whitespaceSub.length() <= 1) {
					tmpTags = tmpTags.replaceAll("\\s+", whitespaceSub);
				}

				// user wants to have another delimiter than whitespace
				return tmpTags.replaceAll(Pattern.quote(delimiter), " ");
			}
			return tagstring;
		} 
		
		return "";
	}
	
}
