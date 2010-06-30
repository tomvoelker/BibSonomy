package org.bibsonomy.database.systemstags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.database.systemstags.executable.ExecutableSystemTag;
import org.bibsonomy.database.systemstags.executable.ForFriendTag;
import org.bibsonomy.database.systemstags.executable.ForGroupTag;
import org.bibsonomy.database.systemstags.search.SearchSystemTag;
import org.bibsonomy.model.Tag;

/**
 * Helper class to encapsulate methods to create and work with systemTags
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class SystemTagsUtil {
	/**
	 *  the pattern matches a string and devides it into tree parts
	 *  the first part is the optional prefix "sys" or "system"
	 *  the second part is called the type
	 *  the last part is called the argument
	 * <ul>
	 * <li> someStringWithoutColon:someString 
	 * <li> sys:someStringWithoutColon:someString
	 * <li> system:someStringWithoutColon:someString
	 * </ul>
	 *  WARNING: this pattern identifies any String of the above form
	 *  Thus a string that matches the pattern is not neccessarily a systemTag - it just looks like one
	 */
	private final static Pattern SYS_TAG_PATTERN = Pattern.compile("^(sys:|system:)?([^:]+):(.+)");
	private final static String PREFIX ="sys";
	private final static String DELIM = ":";
	
	public final static String RELEVANT_FOR = "relevantfor";
	public final static String CV_TAG = "myown";
	
	// The systemTagFactory that manages our registered systemTags
	private static final SystemTagFactory sysTagFactory = SystemTagFactory.getInstance();

	
	
	/*
	 * Methods to tell systemTags from "regular" tags 
	 */
	
	/**
	 * Determins whether a tag (given by name) is an executable systemTag
	 * 
	 * @param tag = the tags name
	 * @return true if the tag is an executable systemTag, false otherwise
	 */
	public static boolean isExecutableSystemTag(final String tagName) {
		final String tagType = SystemTagsUtil.extractName(tagName);
		return present(tagType) && containsSystemTagDelim(tagName) && sysTagFactory.isExecutableSystemTag(tagType);
	}
	
	/**
	 * Determins whether a tag (given by name) is a searchSystemTag
	 * 
	 * @param tag = the tags name
	 * @return true if the tag is a searchSystemTag, false otherwise
	 */
	public static boolean isSearchSystemTag(final String tagName) {
		final String tagType = SystemTagsUtil.extractName(tagName);
		return present(tagType) && containsSystemTagDelim(tagName) && sysTagFactory.isSearchSystemTag(tagType);
	}
	
	/**
	 * Determins whether a tag (given by name) is a systemTag
	 * (i. e. if it is registered as a systemTag in BibSonomy)
	 * 
	 * @param tag = the tags name
	 * @return true if the tag is a systemTag, false otherwise
	 */
	public static boolean isSystemTag(final String tagName) {
		final String tagType = extractName(tagName);
		return present(tagType) && containsSystemTagDelim(tagName) && (sysTagFactory.isExecutableSystemTag(tagType) || sysTagFactory.isSearchSystemTag(tagType));
	}
	
	/**
	 * Determins whether a tag (given by name) is a systemTag of a given kind
	 * (i. e. if the extracted tagName matches a given String
	 * @param tagName
	 * @param tagType
	 * @return
	 */
	public static boolean isSystemTag(final String tagName, final String tagType) {
		final String extractedTagType = extractName(tagName);
		return containsSystemTagDelim(tagName) && extractedTagType.toLowerCase().equals(tagType.toLowerCase());
	}

	public static boolean startsWithPrefix(final String tagName) {
		return tagName.toLowerCase().startsWith(PREFIX);
	}
	
	public static boolean isSystemTagWithPrefix(final String tagName, final String tagType) {
		return isSystemTag(tagName, tagType) && startsWithPrefix(tagName);
	}

	/*
	 * FIXME:This is just a fix, the colon should not be a necessity (e. g. myown)
	 * For the long run: Each systemTag should know for itself whether it needs the prefix, the colon or some argument
	 */
	private static boolean containsSystemTagDelim(final String tagName) {
		if (tagName.contains(DELIM)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Checks, if a list of tagNames contains a member, that starts with a given string, ignoring case
	 * @param tagNames = a list of tagNames
	 * @param tagType = the type we are looking for
	 * @param prefixRequired = set true if systemTag must occur with prefix or false if prefix is optional
	 * @return true if tagNames contains a tagName that matches the given tagType as a systemTag
	 */
	public static boolean containsSystemTag(final List<String> tagNames, final String tagType, final boolean prefixRequired) {
		for (String tagName : tagNames) {
			if ( (prefixRequired && isSystemTagWithPrefix(tagName, tagType))
					|| (!prefixRequired && isSystemTag(tagName, tagType)) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Counts the number of "regular" (i.e., non-system) tags
	 * within a list of tags
	 * 
	 * @param tags = a list of tagNames
	 * @return the number of non-systemTags
	 */
	public static int countNonSystemTags(final List<String> tagNames) {
		int numNonSysTags = 0;
		for (String tagName : tagNames) {
			if (!isSystemTag(tagName)) {
				numNonSysTags++;
			}			
		}
		return numNonSysTags;
	}
	
	


	/*
	 * Methods to create systemTags
	 */
	
	/**
	 * Create a new instance of an executable systemTag
	 * 
	 * @param tag = the original Tag from that a systemTag is to be created
	 * @return a new instance of the matching systemTag 
	 * 		   or null, if the given tag does not describe a systemTag
	 */
	public static ExecutableSystemTag createExecutableTag(final Tag tag) {
		final String name = extractName(tag.getName());
		if (present(name)) {
			final ExecutableSystemTag sysTag = sysTagFactory.getExecutableSystemTag(name);
			if (present(sysTag)) {
				sysTag.setArgument(extractArgument(tag.getName()));
				setIndividualFields(sysTag, tag);
			}
			return sysTag;
		}
		return null;
	}

	/**
	 * Sets some fields, that are required by some ExecutableSystemTags but not all of them
	 * @param sysTag
	 */
	private static void setIndividualFields (ExecutableSystemTag sysTag, Tag tag) {
		if (ForGroupTag.class.isAssignableFrom(sysTag.getClass())) {
			// The forGroupTag needs a DBSessionFactory to create a post for the group
			((ForGroupTag)sysTag).setDBSessionFactory(sysTagFactory.getDbSessionFactory());
		} else if (ForFriendTag.class.isAssignableFrom(sysTag.getClass())) {
			// The forFriendTag needs access to the regular Tag of its post
			((ForFriendTag)sysTag).setTag(tag);
		}
	}
	
	/**
	 * Create a new instance of an executable systemTag
	 * 
	 * @param tag = the original Tag from that a systemTag is to be created
	 * @return a new instance of the matching systemTag 
	 * 		   or null, if the given tag does not describe a systemTag
	 */
	public static SearchSystemTag createSearchSystemTag(final String tagName) {
		final String name = extractName(tagName);
		if (present(name)) {
			final SearchSystemTag sysTag = sysTagFactory.getSearchSystemTag(name);
			if (present(sysTag)) {
				sysTag.setArgument(extractArgument(tagName));
			}
			return sysTag;
		}
		return null;
	}

	/**
	 * Builds a system tag string for a given kind of system tag and an argument
	 * @param sysTagName = which kind of system tag
	 * @param sysTagArgument = argument of the system tag
	 * @return the system tag string built
	 */
	public static String buildSystemTagString(String sysTagName, String sysTagArgument) {
		return PREFIX + DELIM + sysTagName + DELIM + sysTagArgument;
	}

	/**
	 * Wrapper method for {SystemTagsUtil.buildSystemTagString(SystemTags sysTagName, String sysTagArgument)}
	 * @param sysTagPrefix 
	 * @param sysTagArgument 
	 * @return @see {SystemTagsUtil.buildSystemTagString(SystemTags sysTagPrefix, String sysTagValue)}
	 */
	public static String buildSystemTagString(String sysTagName, Integer sysTagArgument) {
		return buildSystemTagString(sysTagName, String.valueOf(sysTagArgument));
	}
	
	
	
	/*
	 * Methods to extract or remove systemTags
	 */

	/**
	 * Removes all systemTags from a given set of tags
	 * Warning: the given Collection must support iterator.remove()
	 * @param tags = the set of tags
	 * @return number of tags, that were removed
	 */
	public static int removeAllSystemTags(final Collection<Tag> tags) {
		int removeCounter = 0;
		for (final Iterator<Tag> iter= tags.iterator(); iter.hasNext();) {
			final Tag tag = iter.next();
			if (isSystemTag(tag.getName())) {
				iter.remove();
				removeCounter++;
			}
		}
		return removeCounter;
	}

	/**
	 * Removes all non-systemTags from a given set of tagNames
	 * Warning: the given Collection must support iterator.remove()
	 * @param tagNames = the set of tagNames
	 * @return number of tags, that were removed
	 */
	public static int removeAllNonSystemTags(final Collection<String> tagNames) {
		int removeCounter = 0;
		for  (final Iterator<String> iter = tagNames.iterator(); iter.hasNext();) {
			String tagName = iter.next();
			if ( !isSystemTag( tagName ) ) {
				iter.remove();
				removeCounter++;
			}
		}
		return removeCounter;
	}
	
	/**
	 * Returns a new List containing the names of all systemTags of a given Collection of tagNames
	 * 
	 * @param tags collection of tags
	 * @return a new list with all system tags which are contained in input tags
	 */
	public static List<String> extractSystemTags(final Collection<String> tagNames) {
		final List<String> sysTags = new LinkedList<String>();
		for( String tagName : tagNames ) {
			if (isSystemTag(tagName)) {
				sysTags.add(tagName);
			}
		}
		return sysTags;
	}
	

	/**
	 * Returns a list of all executable systemTags of a post that have not previously been executed
	 * @param alreadyExecutedTags - the list of all tags, that have been executed and therefore shall not be used again
	 * @param post
	 * @return a list 
	 */
	public static List<ExecutableSystemTag> extractExecutableSystemTags(final Set<Tag> tags, final Set<Tag> alreadyExecutedTags) {
		List<ExecutableSystemTag> sysTags = new ArrayList<ExecutableSystemTag>();
		for (final Tag tag : tags) {
			final ExecutableSystemTag stt = createExecutableTag(tag);
			if (present(stt) && !alreadyExecutedTags.contains(stt)) {
				sysTags.add(stt);
			}
		}
		return sysTags;
	}


	/**
	 * Parses a given string for system tags
	 * @param search = a string to be searched for system tags
	 * @param delim = the delimiter by which the string is to be tokenized
	 * @return a list of Strings that were recognized as SearchSystemTags
	 */
	public static List<String> extractSearchSystemTagsFromString(String search, String delim) {
		List<String> sysTags = new ArrayList<String>();
		if (search == null) {
			return sysTags;
		}
		for (String s : search.split(delim)) {
			s = s.trim().toLowerCase();
			if (isSearchSystemTag(s)) {
				sysTags.add(s);
			}
		}
		return sysTags;
	}


	
	
	
	/*
	 * Methods to analyze systemTags
	 */
	
	/**
	 * Extract systemTag's argument i. e. it maps
	 * <ul>
	 * <li> someStringWithoutColon:someString &rarr someString 
	 * <li> sys:someStringWithoutColon:someString &rarr someString
	 * <li> system:someStringWithoutColon:someString &rarr someString
	 * <li> someStringWithoutColon &rarr null
	 * <li> null &rarr null
	 * </ul>
	 * @param tagName the system tag string
	 * @return tag's name
	 */
	public static String extractArgument(final String tagName) {
		if (!present(tagName)) {
			return null;
		}
		final Matcher sysTagMatcher = SYS_TAG_PATTERN.matcher(tagName);
		if( sysTagMatcher.lookingAt() ) {
			return sysTagMatcher.group(3).trim();
		}
		return null;
	}

	/**
	 * Extract system tag's name i. e. it returns someStringWithoutColon for
	 * <ul>
	 * <li> someStringWithoutColon:someString 
	 * <li> sys:someStringWithoutColon:someString
	 * <li> system:someStringWithoutColon:someString
	 * <li> someStringWithoutColon
	 * <li> and null otherwise
	 * </ul>
	 * @param tagName the system tag string
	 * @return tag's name
	 */
	public static String extractName(final String tagName) {
		if (!present(tagName)) {
			return null;
		}
		final Matcher sysTagMatcher = SYS_TAG_PATTERN.matcher(tagName);
		if( sysTagMatcher.lookingAt() ) {
			return sysTagMatcher.group(2);
		} else {
			return tagName;
		}
	}	

}
