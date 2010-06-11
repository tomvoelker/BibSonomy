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

import org.bibsonomy.common.exceptions.UnsupportedSystemTagException;
import org.bibsonomy.database.systemstags.executable.ExecutableSystemTag;
import org.bibsonomy.database.systemstags.executable.ForGroupTag;
import org.bibsonomy.database.systemstags.xml.Attribute;
import org.bibsonomy.database.systemstags.xml.SystemTagType;
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
	 *  Thus a string that matches the pattern is not neccessarily a systemTag
	 */
	private final static Pattern SYS_TAG_PATTERN = Pattern.compile("^(sys:|system:)?([^:]+):(.+)");
	
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
		return sysTagFactory.isExecutableSystemTag(tagType);
	}
	
	/**
	 * Determins whether a tag (given by name) is a searchSystemTag
	 * 
	 * @param tag = the tags name
	 * @return true if the tag is a searchSystemTag, false otherwise
	 */
	public static boolean isSearchSystemTag(final String tagName) {
		final String tagType = SystemTagsUtil.extractName(tagName);
		return sysTagFactory.isSearchSystemTag(tagType);
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
		return sysTagFactory.isExecutableSystemTag(tagType) || sysTagFactory.isSearchSystemTag(tagType);
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
				sysTag.setTag(tag);
				sysTag.setName(name);
				sysTag.setArgument(extractArgument(tag.getName()));
				setIndividualFields(sysTag);
			}
			return sysTag;
		}
		return null;
	}


	/**
	 * Sets some fields, that are required by some ExecutableSystemTags but not all of them
	 * @param sysTag
	 */
	private static void setIndividualFields (ExecutableSystemTag sysTag) {
		if (ForGroupTag.class.isAssignableFrom(sysTag.getClass())) {
			// The forGroupTag needs a DBSessionFactory to create a post for the group
			((ForGroupTag)sysTag).setDBSessionFactory(sysTagFactory.getDbSessionFactory());
		}
	}
	
	
	
	/*
	 * Methods to extract or remove systemTags
	 */

	/**
	 * Removes all systemTags from a given set of tags
	 * @param tags = the set of tags
	 * @return number of tags, that were removed
	 */
	public static int removeAllSystemTags(final Set<Tag> tags) {
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
	 * Returns a List containing the names of all systemTags of a given Collection of tagNames
	 * 
	 * @param tags collection of tags
	 * @return all system tags which are contained in input tags
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
			return sysTagMatcher.group(3);
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
	/*
	 * FIXME: rename extractTagType
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

	
	
	
	
	
	
	/*
	 * FIXME: These methods have not been checked by sdo yet
	 */
	
	/**
	 * build a system tag string for a given kind of system tag and a value
	 * 
	 * @param sysTagPrefix - 
	 * 			which kind of system tag
	 * @param sysTagValue -
	 * 			the value of the system tag
	 * 
	 * @return the system tag string built
	 */
	public static String buildSystemTagString(SystemTags sysTagPrefix, String sysTagValue) {
		return sysTagPrefix.getPrefix() + SystemTags.SYSTAG_DELIM + sysTagValue;
	}
	/**
	 * Wrapper method for {SystemTagsUtil.buildSystemTagString(SystemTags sysTagPrefix, String sysTagValue)}
	 * 
	 * @param sysTagPrefix 
	 * @param sysTagValue 
	 * 
	 * @return @see {SystemTagsUtil.buildSystemTagString(SystemTags sysTagPrefix, String sysTagValue)}
	 */
	public static String buildSystemTagString(SystemTags sysTagPrefix, Integer sysTagValue) {
		return buildSystemTagString(sysTagPrefix, String.valueOf(sysTagValue));
	}

	//FIXME: What does this method do?
	// Fortunatelly nobody uses it!
	/**
	 * TODO: improve doc
	 * @param sTag
	 * @param attributeName
	 * @return TODO
	 */
	public static String getAttributeValue(final SystemTagType sTag, final String attributeName) {
		for (final Attribute attribute : sTag.getAttribute()) {
			if (attribute.getName().equals(attributeName)) {
				return attribute.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Parses a given string for system tags; first, the input strins is tokenized by
	 * the given delimiter (delim), then for each token it checked whether the token
	 * is a system tag
	 * 
	 * @param search -
	 * 			a string to be searched for system tags
	 * @param delim - 
	 * 			the delimiter by which the string is to be tokenized
	 * @return TODO: improve doc
	 */
	public static List<String> extractSystemTagsFromString(String search, String delim) {
		List<String> sysTags = new ArrayList<String>();
		if (search == null) return sysTags;
		
		String[] sysTagParts;
		
		for (String s : search.split(delim)) {
			s = s.trim().toLowerCase();
			// check for 'sys:'
			if (s.startsWith(SystemTags.GLOBAL_PREFIX)) {
				sysTagParts = s.split(SystemTags.SYSTAG_DELIM);
				// extract middle part (e.g. 'user' from 'sys:user:dbenz'
				if (sysTagParts.length > 1) {
					try {
						// check if the system tag exists
						SystemTags.getSystemTag(sysTagParts[1].trim());
						// if yes -> add to list (exception is thrown otherwise)
						sysTags.add(s);
					} catch (UnsupportedSystemTagException ex) {
						// do nothing - there is no system tag like this in this case
					}
				} 		
			}
		}
		
		return sysTags;
	}

}
