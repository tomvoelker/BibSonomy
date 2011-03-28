package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.User;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.validation.UserLDAPRegistrationValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * This controller handles the registration of users via LDAP
 * 
 * @author Sven Stefani
 * @author rja
 * @version $Id$
 */
public class UserLDAPRegistrationController extends AbstractUserIDRegistrationController {
	
	/**
	 * Shall the LDAP ID be suggested as user name?
	 */
	private boolean ldapIdIsUsername = false;
	
	@Override
	protected String getLoginNotice() {
		return "register.ldap.step1";
	}
	
	@Override
	protected void setAuthentication(User registerUser, User user) {
		registerUser.setLdapId(user.getLdapId());
		/*
		 * For LDAP users we store their (hashed) LDAP password. Thus - if 
		 * "internal" authentication is enabled, they can login with their LDAP
		 * password using the internal authentication method.
		 */
		registerUser.setPassword(StringUtils.getMD5Hash(user.getPassword()));
	}

	@Override
	protected Authentication getAuthentication(User user) {
		return new UsernamePasswordAuthenticationToken(user.getLdapId(), user.getPassword());
	}

	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserLDAPRegistrationValidator();
	}
	
	@Override
	protected String generateUserName(User user) {
		if (ldapIdIsUsername) {
			return user.getLdapId();
		}
		return super.generateUserName(user);
	}

	/**
	 * @return <code>true</code>, if the LDAP ID shall be suggested as user name 
	 * during registration.
	 */
	public boolean isLdapIdIsUsername() {
		return this.ldapIdIsUsername;
	}

	/**
	 * If the LDAP ID shall be suggested as user name during registration, set 
	 * this to <code>true</code>. Otherwise, a user name is generated using the 
	 * real name. 
	 *  
	 * @param ldapIdIsUsername
	 */
	public void setLdapIdIsUsername(boolean ldapIdIsUsername) {
		this.ldapIdIsUsername = ldapIdIsUsername;
	}
}