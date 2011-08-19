package org.bibsonomy.webapp.command.ajax;

/**
 * @author Bernd
 * @version $Id$
 */
public class AjaxCvCommand extends AjaxCommand {
	/**
	 * Name of the design
	 */
	private String layout;
	
	/**
	 * Does the user want to save right away?
	 */
	private String isSave;
	
	/**
	 * 
	 */
	private String wikiText;


	/**
	 * @return the layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @return the isSave
	 */
	public String getIsSave() {
		return isSave;
	}

	/**
	 * @param isSave the isSave to set
	 */
	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	/**
	 * @return the wikiText
	 */
	public String getWikiText() {
		return wikiText;
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}

}
