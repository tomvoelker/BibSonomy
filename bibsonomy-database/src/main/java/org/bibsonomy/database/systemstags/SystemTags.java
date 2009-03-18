package org.bibsonomy.database.systemstags;

import org.bibsonomy.common.exceptions.UnsupportedSystemTagException;
import org.bibsonomy.util.EnumUtils;

/**
 * Defines possible system tags
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public enum SystemTags {
	/** Author */
	AUTHOR("author"),
	/** User */
	USER("user"),
	/** BibTex key */
	BIBTEXKEY("bibtexkey"),
	/** days */
	DAYS("days"),
	/** relevant for */
	RELEVANTFOR("relevantfor"),
	/** year restriction */
	YEAR("year"),
	/** define search type */
	SEARCH("search");
		
	/** stores the prefix */
	private final String prefix;
	
	/** global system tag prefix */
	public static final String GLOBAL_PREFIX = "sys";
	/** system tag delimiter */
	public static final String SYSTAG_DELIM = ":";	
	
	/** constructor */
	private SystemTags(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * get system tag by name
	 * 
	 * @param systemTag  -
	 * 			name of the requested system tag

	 * @return the corresponding SystemTags-enum
	 */
	public static SystemTags getSystemTag(final String systemTag) {
		if (systemTag == null) throw new UnsupportedSystemTagException("NULL");
		final SystemTags p = EnumUtils.searchEnumByName(SystemTags.values(), systemTag);
		if (p == null) throw new UnsupportedSystemTagException(systemTag);
		return p;
	}
		
	/**
	 * Return the prefix for the given enum
	 * 
	 * @return the prefix
	 */
	public String getPrefix() {
		return SystemTags.GLOBAL_PREFIX + SystemTags.SYSTAG_DELIM + this.prefix;
	}
}