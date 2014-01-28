package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.filter.SamlLoginFilter;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This controller is neccessary to process the success of a saml login
 * TODO: document why we are not using the {@link AuthenticationSuccessHandler} provided
 * by spring security @see {@link SamlLoginFilter}
 * 
 * @author nilsraabe
 */
public class UserLoginSamlController implements MinimalisticController<BaseCommand> {

	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

	@Override
	public View workOn(final BaseCommand command) {
		return new ExtendedRedirectView("/");
	}

}
