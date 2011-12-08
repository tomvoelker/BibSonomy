package org.bibsonomy.webapp.command.ajax;

/**
 * @author Bernd
 * @version $Id$
 */
public class AjaxCvCommand extends AjaxCommand {
	
	/**
	 * TODO: change type to UserLayout after it is merged with GroupLayout
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
	public void setLayout(final String layout) {
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
	public void setWikiText(final String wikiText) {
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
	public void setRenderOptions(final String renderOptions) {
		this.renderOptions = renderOptions;
	}

}
