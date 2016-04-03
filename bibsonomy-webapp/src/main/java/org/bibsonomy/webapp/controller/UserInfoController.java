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
 package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.UserInfoCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the FOAF-rdf output:
 * 		- /foaf/user/USER
 * For more information please visit the <a href="http://www.foaf-project.org/">FOAF project page</a>
 * 
 * XXX: instead of returning hand-crafted RDF via JSPs, we should 
 * probably use an RDF writer from the Jena project.
 * 
 * @author dzo
 */
public class UserInfoController implements MinimalisticController<UserInfoCommand> {
	private LogicInterface logic;
	private String urlScheme;
	
	@Override
	public UserInfoCommand instantiateCommand() {
		return new UserInfoCommand();
	}

	@Override
	public View workOn(final UserInfoCommand command) {		
		if (!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		/*
		 * handle requests form iOS and Android apps
		 * they get a redirect to their custom url scheme
		 * the url contains the name of the logged-in user and his/her api key
		 */
		if ("devicesupport".equals(command.getFormat())) {
			if (!command.getContext().isValidCkey()) {
				return Views.DEVICE_AUTHORIZE;
			}
			if (!command.isShareInformation()) {
				return new ExtendedRedirectView("/");
			}
			
			final User loginUser = command.getContext().getLoginUser();
			String url = UrlUtils.setParam(this.urlScheme + "://settings", "username",  UrlUtils.safeURIEncode(loginUser.getName()));
			url = UrlUtils.setParam(url, "apiKey", loginUser.getApiKey());
			return new ExtendedRedirectView(url);
		}
		
		/*
		 * handle FOAF; currently the only other format
		 */
		final String requestedUser = command.getRequestedUser();
		
		if (!present(requestedUser)) {
			throw new MalformedURLSchemeException("error.foaf_output_without_username");
		}
		
		/*
		 * get informations from logic
		 */
		final User user = this.logic.getUserDetails(requestedUser);
		
		/*
		 * add friends
		 */
		try {
			user.addFriends(this.logic.getUserRelationship(requestedUser, UserRelation.OF_FRIEND, null));
		} catch (final AccessDeniedException ex) {
			// ignore it
		}
		
		/*
		 *  prepare mail address / encode it using sha-1
		 */
		final String mail = user.getEmail();
		if (present(mail)) {
			final String toEncode = "mailto:" + mail;
			user.setEmail(StringUtils.getSHA1Hash(toEncode));
		}		
		
		command.setUser(user);
		return Views.FOAF;
	}

	/**
	 * @param logic the adminLogic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param urlScheme the urlSchema to set
	 */
	public void setUrlScheme(String urlScheme) {
		this.urlScheme = urlScheme;
	}
}
