package org.bibsonomy.database.systemstags;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedSystemTagException;

/**
 * Helper class to encapsulate methods to create / work with system tags
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class SystemTagsUtil {
		
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
	 * @see SystemTagsUtil.buildSystemTagString
	 */
	public static String buildSystemTagString(SystemTags sysTagPrefix, Integer sysTagValue) {
		return buildSystemTagString(sysTagPrefix, String.valueOf(sysTagValue));
	}
	
	/**
	 * Parses a given string for system tags; first, the input strins is tokenized by
	 * the given delimiter (delimi), then for each token it checked whether the token
	 * is a system tag
	 * 
	 * @param search -
	 * 			a string to be searched for system tags
	 * @param delim - 
	 * 			the delimiter by which the string is to be tokenized
	 * @return
	 */
	public static List<String> extractSystemTagsFromString(String search, String delim) {
		ArrayList<String> sysTags = new ArrayList<String>();
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
					}
					catch (UnsupportedSystemTagException ex) {
						// do nothing - there is no system tag like this in this case
					}
				} // end if				
			} // end if
		} // end for
		
		return sysTags;
	}
	
	
}
