package org.bibsonomy.scraper.converter.picatobibtex.rules;

/**
 * @author daill
 * @version $Id$
 */
public interface Rules {
	/**
	 * Checks if the requested field is available
	 * 
	 * @return boolean
	 */
	public boolean isAvailable();
	
	/**
	 * Gets the bibtex string part
	 * 
	 * @return string
	 */
	public String getContent();
}
