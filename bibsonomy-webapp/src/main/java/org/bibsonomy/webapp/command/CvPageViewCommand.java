package org.bibsonomy.webapp.command;


/**
 * @author philipp
 * @version $Id$
 */
public class CvPageViewCommand extends UserResourceViewCommand {
	private boolean isGroup = false;
	private String wikiText;
	private String renderedWikiText;

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
	 * @return the isGroup
	 */
	public boolean getIsGroup() {
		return isGroup;
	}

	/**
	 * @param isGroup the isGroup to set
	 */
	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	
}
