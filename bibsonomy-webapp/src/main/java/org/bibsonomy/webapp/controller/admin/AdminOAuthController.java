package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.opensocial.oauth.database.IOAuthLogic;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthAdminCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.opensocial.BibSonomyOAuthValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for managing OAuth consumer keys
 * 
 * @author Folke Mitzlaff
 * @version $Id$
 **/
public class AdminOAuthController implements ValidationAwareController<OAuthAdminCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(AdminOAuthController.class);
	
	/** database access to the OAuth consumer store */
	private IOAuthLogic oauthLogic;
	
	/**
	 * hold current errors
	 */
	private Errors errors = null;
	
	//------------------------------------------------------------------------
	// MinimalisticController interface
	//------------------------------------------------------------------------
	@Override
	public OAuthAdminCommand instantiateCommand() {
		final OAuthAdminCommand command = new OAuthAdminCommand();
		command.setConsumerInfo(new OAuthConsumerInfo());
		return command;
	}
	
	@Override
	public View workOn(final OAuthAdminCommand command) {
		ensureAdminAcess(command);

		// show errors if command validation failed
		if (errors.hasErrors()) {
			command.setAdminAction(OAuthAdminCommand.AdminAction.List.name());
		}
		
		if (!present(command.getAdminAction())) {
			command.setAdminAction(OAuthAdminCommand.AdminAction.List.name());
		}
		
		/*
		 * Register or remove consumers.
		 * */
		switch (command.getAdminAction_()) {
		case Register: {
			if (KeyType.RSA_PRIVATE.equals(command.getConsumerInfo().getConsumerKey())) {
				command.getConsumerInfo().setKeyName("RSA-SHA1.PublicKey");
			}
			this.oauthLogic.createConsumer(command.getConsumerInfo());
			break;
		}
		case Remove: {
			log.info("Deleting consumerInfo " + command.getConsumerInfo().getConsumerKey() + ".");
			this.oauthLogic.deleteConsumer(command.getConsumerInfo().getConsumerKey());
			break;
		}}
		
		/*
		 * List consumers.
		 * */
		List<OAuthConsumerInfo> consumerInfo = this.oauthLogic.listConsumers();
		command.setConsumers(consumerInfo);
		
		return Views.ADMIN_OAUTH;
	}

	//------------------------------------------------------------------------
	// ValidationAwareController interface
	//------------------------------------------------------------------------
	@Override
	public Validator<OAuthAdminCommand> getValidator() {
		return new BibSonomyOAuthValidator(this.oauthLogic);
	}

	@Override
	public boolean isValidationRequired(final OAuthAdminCommand command) {
		return true;
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * ensure that the requesting user is logged in and an administrator
	 * @param command
	 */
	private void ensureAdminAcess(final BaseCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}
	}

	/**
	 * @param oauthLogic the oauth logic to set
	 */
	public void setOauthLogic(final IOAuthLogic oauthLogic) {
		this.oauthLogic = oauthLogic;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}