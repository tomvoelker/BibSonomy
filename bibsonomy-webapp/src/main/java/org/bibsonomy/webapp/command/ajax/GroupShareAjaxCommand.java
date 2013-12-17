package org.bibsonomy.webapp.command.ajax;

/**
 * @author clemensbaier
  */
public class GroupShareAjaxCommand extends AjaxCommand {

	private String requestedGroup;

	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}
}
