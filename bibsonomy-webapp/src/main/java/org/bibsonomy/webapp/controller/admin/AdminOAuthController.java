package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.opensocial.oauth.database.IOAuthLogic;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthAdminCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for managing OAuth consumer keys
 * 
 * @author Folke Mitzlaff
 **/
public class AdminOAuthController implements MinimalisticController<OAuthAdminCommand> {
	private static final Log log = LogFactory.getLog(AdminOAuthController.class);
	
	private IOAuthLogic oauthLogic;
	
	
	@Override
	public OAuthAdminCommand instantiateCommand() {
		final OAuthAdminCommand command = new OAuthAdminCommand();
		command.setConsumerInfo(new OAuthConsumerInfo());
		return command;
	}
	
	@Override
	public View workOn(OAuthAdminCommand command) {
		ensureAdminAcess(command);

		if (!present(command.getAdminAction())) {
			command.setAdminAction(OAuthAdminCommand.AdminAction.List.name());
		}
		
		switch (command.getAdminAction_()) {
		case Register:
			// TODO: validate entry
			this.oauthLogic.createConsumer(command.getConsumerInfo());
		case List:
			List<OAuthConsumerInfo> consumerInfo = this.oauthLogic.listConsumers();
			command.setConsumers(consumerInfo);
			break;
		default:
			log.error("Invalid action given for administrating OAuth.");
		}
		
		return Views.ADMIN_OAUTH;
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * ensure that the requesting user is logged in and an administrator
	 * @param command
	 */
	private void ensureAdminAcess(BaseCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
	}

	public void setOauthLogic(IOAuthLogic oauthLogic) {
		this.oauthLogic = oauthLogic;
	}

	public IOAuthLogic getOauthLogic() {
		return oauthLogic;
	}


}