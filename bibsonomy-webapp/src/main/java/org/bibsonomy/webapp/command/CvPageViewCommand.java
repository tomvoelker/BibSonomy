package org.bibsonomy.webapp.command;


/**
 * @author philipp
 * @version $Id$
 */
public class CvPageViewCommand extends UserResourceViewCommand {

	private String wikiText;
	private String renderedWikiText;
	private String requestedGroup;

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

	/**
	 * @return the renderedWikiText
	 */
	public String getRenderedWikiText() {
		return renderedWikiText;
	}

	/**
	 * @param renderedWikiText the renderedWikiText to set
	 */
	public void setRenderedWikiText(String renderedWikiText) {
		this.renderedWikiText = renderedWikiText;
	}

	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}
	
}
