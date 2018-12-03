package org.bibsonomy.webapp.controller.cris;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.cris.OrganizationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

/**
 * controller that lists a organization with all the details
 *
 * request paths:
 *  - /organization/ORGANIZATIONNAME
 *
 * @author dzo, pda
 */
public class OrganizationPageController implements MinimalisticController<OrganizationPageCommand> {

	private LogicInterface logic;

	@Override
	public OrganizationPageCommand instantiateCommand() {
		return new OrganizationPageCommand();
	}

	@Override
	public View workOn(OrganizationPageCommand command) {

		// get persons for the organization

		// get publications for the organization

		return null;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
