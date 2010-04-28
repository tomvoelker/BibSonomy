package org.bibsonomy.webapp.command.ajax;


/**
 * @author Christian Kramer
 * @version $Id$
 */
public class ConceptAjaxCommand extends AjaxCommand {
	private String tag;

	/**
	 * @return String
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * @param tag
	 */
	public void setTag(final String tag) {
		this.tag = tag;
	}
}
