package org.bibsonomy.webapp.command.ajax;

/**
 * @author clemensbaier
 * @version $Id$
 */
public class GroupShareAjaxCommand extends AjaxCommand {

	private String requestedGroup;

	public String getRequestedGroup() {
		return requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}
}
