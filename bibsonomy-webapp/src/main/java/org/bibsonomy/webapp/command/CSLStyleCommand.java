package org.bibsonomy.webapp.command;

/**
 * 
 * @author jp
 */
public class CSLStyleCommand extends ResourceViewCommand{
	private String responseString;
	private String style;
	
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
