/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import java.util.Arrays;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.actions.SamlUserIDRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeMapping;
import org.bibsonomy.webapp.validation.UserSamlRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via SAML (Shibboleth)
 * 
 * @author jensi
 */
public class UserSamlRegistrationController extends AbstractUserIDRegistrationController {
	
	private SamlUserAttributeMapping attributeExtractor;
	
	
	@Override
	protected String getLoginNotice() {
		return "register.saml.step1";
	}
	
	@Override
	protected UserIDRegistrationCommand instantiateCommandInternal() {
		return new SamlUserIDRegistrationCommand();
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
	protected void validate(Errors errors, UserIDRegistrationCommand command, User userToBeRegistered) {
		SamlRemoteUserId authRId = attributeExtractor.getRemoteUserId(VuFindUserInitController.getSamlCreds());
		org.bibsonomy.util.ValidationUtils.assertNotNull(authRId);
		for (RemoteUserId rId : userToBeRegistered.getRemoteUserIds()) {
			if (rId.equals(authRId) == false) {
				errors.rejectValue("samlId", "error.registration.samlId.missmatch");
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
		registerUser.setPassword(UserUtils.generateRandomPassword());
	}

	@Override
	protected Authentication getAuthentication(User user) {
		//TODO: put saml credentials in new authtokentype
		//then create authenticationprovider who does something like (username maybe taken from samlcreds):
		//	final UserDetails user = this.service.loadUserByUsername(username);
		//	authentication = new SessionAuthenticationToken(user, user.getAuthorities());
		
		return null;
	}
	
	@Override
	protected View logOn(User user) {
		final SAMLCredential creds = VuFindUserInitController.getSamlCreds();
		final Authentication auth = getAuthenticationManager().authenticate(new SamlCredAuthToken(creds));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return new ExtendedRedirectView("/register_saml_success");
	}
	
	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserSamlRegistrationValidator(new SamlAuthenticationTool(getRequestLogic(), Arrays.asList("step")), getRequestLogic());
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(SamlUserAttributeMapping attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

}