/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.actions.SamlUserIDRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.bibsonomy.webapp.util.spring.security.exceptionmapper.SamlUsernameNotFoundExceptionMapper;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.bibsonomy.webapp.validation.UserSamlRegistrationValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;

/**
 * This controller handles the registration of users via SAML (Shibboleth)
 * 
 * @author jensi
 */
public class UserSamlRegistrationController extends AbstractUserIDRegistrationController<SAMLCredential> {
	
	private List<String> requiredFields;
	
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.AbstractUserIDRegistrationController#getAddtionsInfoSessionKey()
	 */
	@Override
	protected String getAddtionsInfoSessionKey() {
		return SamlUsernameNotFoundExceptionMapper.ATTRIBUTE_SAML_CREDS;
	}

	@Override
	protected Authentication getAuthentication(final User user, final SAMLCredential creds) {
		return new SamlCredAuthToken(creds);
	}
	
	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserSamlRegistrationValidator(new SamlAuthenticationTool(getRequestLogic(), Arrays.asList("step")), getRequestLogic(), this.requiredFields);
	}

	/**
	 * @param requiredFields the requiredFields to set
	 */
	public void setRequiredFields(List<String> requiredFields) {
		this.requiredFields = requiredFields;
	}

}