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
	 * @return the design
	 */
	public String getDesign() {
		return layout;
	}

	/**
	 * @param layout the design to set
	 */
	public void setDesign(String layout) {
		this.layout = layout;
	}
}
