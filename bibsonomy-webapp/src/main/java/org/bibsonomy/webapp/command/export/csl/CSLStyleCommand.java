package org.bibsonomy.webapp.command.export.csl;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author jp
 */
public class CSLStyleCommand extends BaseCommand {
	private String responseString;
	private String style;
	private String locale;
	
	/**
	 * @return the style
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style.toLowerCase();
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @param responseString the responseString to set
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
}
