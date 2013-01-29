package org.bibsonomy.webapp.controller.actions;

import java.util.Random;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.webapp.command.actions.SamlUserIDRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserSamlRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.core.Authentication;

/**
 * This controller handles the registration of users via SAML (Shibboleth)
 * 
 * @author jensi
 * @version $Id$
 */
public class UserSamlRegistrationController extends AbstractUserIDRegistrationController {

	@Override
	protected String getLoginNotice() {
		return "register.saml.step1";
	}
	
	@Override
	public UserIDRegistrationCommand instantiateCommand() {
		final UserIDRegistrationCommand command = new SamlUserIDRegistrationCommand();
		command.setRegisterUser(new User());
		return command;
	}
	
	@Override
	protected void setFixedValuesFromUser(UserIDRegistrationCommand command, User user) {
		super.setFixedValuesFromUser(command, user);
		for (RemoteUserId remoteId : user.getRemoteUserIds()) {
			if (remoteId instanceof SamlRemoteUserId) {
				((SamlUserIDRegistrationCommand) command).setSamlId((SamlRemoteUserId) remoteId);
				break;
			}
		}
	}
	
	@Override
	protected void setAuthentication(User registerUser, User user) {
		for (RemoteUserId remoteId : user.getRemoteUserIds()) {
			registerUser.setRemoteUserId(remoteId);
		}
		/*
		 * Like OpenID users, we don't have a password for SAML users thus we set a random one
		 * for security (and database constraint) reasons.
		 */
		registerUser.setPassword(generateRandomPassword());
	}

	@Override
	protected Authentication getAuthentication(User user) {
		//TODO: put saml credentials in new authtokentype
		//then create authenticationprovider who does something like (username maybe taken from samlcreds):
		//	final UserDetails user = this.service.loadUserByUsername(username);
		//	authentication = new SessionAuthenticationToken(user, user.getAuthorities());
		
		return null;
		//return new OpenIDAuthenticationToken(new UserAdapter(user), new UserAdapter(user).getAuthorities(), user.getOpenID(), null);
	}
	
	@Override
	protected View logOn(User user) {
		// do nothing but redirect to a successView where ( -- hopefully -- ) the user can login directly via spring security saml filters.
		return new ExtendedRedirectView("/register_saml_success");
	}

	private String generateRandomPassword() {
		final byte[] bytes = new byte[16];
		new Random().nextBytes(bytes);
		final String randomPassword = HashUtils.getMD5Hash(bytes);
		return randomPassword;
	}

	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserSamlRegistrationValidator();
	}
}