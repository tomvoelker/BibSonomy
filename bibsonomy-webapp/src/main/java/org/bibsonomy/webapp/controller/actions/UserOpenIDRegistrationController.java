/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
public class UserOpenIDRegistrationController extends AbstractUserIDRegistrationController<Void> {

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
	protected Authentication getAuthentication(User user, final Void additionalInformation) {
		return new OpenIDAuthenticationToken(new UserAdapter(user), new UserAdapter(user).getAuthorities(), user.getOpenID(), null);
	}

	@Override
	public Validator<UserIDRegistrationCommand> getValidator() {
		return new UserOpenIDRegistrationValidator();
	}
}