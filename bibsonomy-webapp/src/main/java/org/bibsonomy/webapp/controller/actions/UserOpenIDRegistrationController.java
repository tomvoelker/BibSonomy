package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.validation.UserOpenIDRegistrationValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * This controller handles the registration of users via OpenID
 * (see http://openid.net/)
 * 
 * @author Stefan Stützer
 * @author rja
 */
public class UserOpenIDRegistrationController extends AbstractUserIDRegistrationController {

	@Override
	protected String getLoginNotice() {
		return "register.openid.step1";
	}
	
	@Override
	protected void setAuthentication(User registerUser, User user) {
		registerUser.setOpenID(user.getOpenID());
		/*
		 * We don't have a password for OpenID users, thus we set a random one
		 * for security reasons.
		 */
		registerUser.setPassword(UserUtils.generateRandomPassword());
	}

	@Override
	protected Authentication getAuthentication(User user) {
		return new OpenIDAuthenticationToken(new UserAdapter(user), new UserAdapter(user).getAuthorities(), user.getOpenID(), null);
	}

	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserOpenIDRegistrationValidator();
	}
}