package org.bibsonomy.webapp.command;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLStyleCommand extends ResourceViewCommand{
	private String xml;
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
		this.style = style;
	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return this.xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}
	
}
