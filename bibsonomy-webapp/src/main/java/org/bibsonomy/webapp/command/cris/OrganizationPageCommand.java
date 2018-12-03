package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author dzo, pda
 */
public class OrganizationPageCommand extends BaseCommand {

	private String requestedOrganizationName;

	/**
	 * @return the requestedOrganizationName
	 */
	public String getRequestedOrganizationName() {
		return this.requestedOrganizationName;
	}

	/**
	 * @param requestedOrganizationName the requestedOrganizationName to set
	 */
	public void setRequestedOrganizationName(String requestedOrganizationName) {
		this.requestedOrganizationName = requestedOrganizationName;
	}
}
