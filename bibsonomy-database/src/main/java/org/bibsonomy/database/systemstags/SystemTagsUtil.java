package org.bibsonomy.database.systemstags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.exceptions.UnsupportedSystemTagException;
import org.bibsonomy.database.systemstags.xml.Attribute;
import org.bibsonomy.database.systemstags.xml.SystemTagType;
import org.bibsonomy.model.Tag;

/**
 * Helper class to encapsulate methods to create / work with system tags
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class SystemTagsUtil {
	private final static Pattern sysPrefix = Pattern.compile("^(sys:|system:)?(.*):(.*)");

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
	
	/**
	 * Count the number of "normal" (i.e., non-system) tags
	 * within a list of tags
	 * 
	 * @param tags - a list of tag strings
	 * @return - the number of non-system tags
	 */
	public static int countNonSystemTags(List<String> tags) {
		int numNonSysTags = 0;
		for (String tag : tags) {
			if (tag != null && !isSystemTag(tag)) {
				numNonSysTags++;
			}			
		}
		return numNonSysTags;
	}
	
	/**
	 * returns all system tags which are contained in a given collection of tags
	 * 
	 * @param tags collection of tags
	 * @return all system tags which are contained in input tags
	 */
	public static List<String> extractSystemTags(Collection<String> tags) {
		List<String> sysTags = new LinkedList<String>();
		
		for( String tag : tags ) {
			if( isSystemTag(tag) ) {
				sysTags.add(tag);
			}
		}
		
		return sysTags;
	}
	
	/**
	 * Check whether a given string is a system tag
	 * 
	 * @param tag - tag string
	 * @return true if the given string is a systemtag, false otherwise
	 */
	public static boolean isSystemTag(String tag) {
		if (tag == null) return false;
		
		final Matcher action = sysPrefix.matcher(tag);
		return action.lookingAt();
	}
	
	/**
	 * Extract system tag's argument.
	 * @param tagName the system tag string
	 * @return tag's argument, if found.
	 */
	public static String extractArgument(String tagName) {
		final Matcher action = sysPrefix.matcher(tagName);
		if( action.lookingAt() )
			return action.group(3);
		return null;
	}

	/**
	 * Extract system tag's name.
	 * @param tagName the system tag string
	 * @return tag's name, if found, null otherwise.
	 */
	public static String extractName(final String tagName) {
		final Matcher action = sysPrefix.matcher(tagName);
		if( action.lookingAt() )
			return action.group(2);
		return null;
	}	

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
	 * For name = systemTag.getName(), removes all occurrences of system tags sys:&lt;name&gt;:&lt;argument&gt;,
	 * system:&lt;name&gt;:&lt;argument&gt; and &lt;name&gt;:&lt;argument&gt;
	 * 
	 * @param tags collection of tags to alter 
	 * @param systemTag the system tag to be removed. 
	 * @return number of occurrences removed.
	 */
	public static int removeSystemTag(final Set<Tag> tags, final SystemTag systemTag) {
		final Iterator<Tag> iterator = tags.iterator();
		int nr = 0;
		
		while (iterator.hasNext()) {
			final Tag tag = iterator.next();
			if (systemTag.getName().equals(SystemTagsUtil.extractName(tag.getName()))) {
				iterator.remove();
				nr++;
			}
		}
		return nr;
	}
}
