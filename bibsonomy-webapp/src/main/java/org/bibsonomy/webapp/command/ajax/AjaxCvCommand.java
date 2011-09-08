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
	 * renderOptions
	 */
	private String renderOptions;
	
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

	/**
	 * @return the renderOptions
	 */
	public String getRenderOptions() {
		return renderOptions;
	}

	/**
	 * @param renderOptions the renderOptions to set
	 */
	public void setRenderOptions(String renderOptions) {
		this.renderOptions = renderOptions;
	}

}
