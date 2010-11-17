package org.bibsonomy.webapp.command;


/**
 * @author philipp
 * @version $Id$
 */
public class CvPageViewCommand extends UserResourceViewCommand {

	private String wikiText;

	/**
	 * @return the wikiText
	 */
	public String getWikiText() {
		return this.wikiText;
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}
	
}
