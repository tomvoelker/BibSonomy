package org.bibsonomy.webapp.command;


/**
 * @author philipp
 * @version $Id$
 */
public class CvPageViewCommand extends UserResourceViewCommand {

	String wikiText;

	/**
	 * @return the wiki
	 */
	public String getWikiText() {
		return this.wikiText;
	}

	/**
	 * @param wiki the wiki to set
	 */
	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}
	
}
