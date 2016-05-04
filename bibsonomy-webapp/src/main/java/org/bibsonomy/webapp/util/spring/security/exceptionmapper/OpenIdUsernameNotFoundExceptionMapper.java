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
package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * @author rja
 */
public class OpenIdUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {

	/**
	 * Checks if this mapper can handle the given exception.
	 * 
	 * @param e
	 * @return <code>true</code> if the given exception is a subclass of {@link OpenIdUsernameNotFoundException}.
	 */
	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && OpenIdUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}
	
	/**
	 * Maps the user data from the OpenID server to our user object.
	 * 
	 * @param e
	 * @return A user containing the information from the OpenID server.
	 */
	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof OpenIdUsernameNotFoundException) {
			final Authentication authentication = ((OpenIdUsernameNotFoundException) e).getAuthentication();
			
			if (authentication instanceof OpenIDAuthenticationToken) {
				final OpenIDAuthenticationToken openIdAuthentication = (OpenIDAuthenticationToken) authentication;

				user.setOpenID(openIdAuthentication.getIdentityUrl());

				
				final List<OpenIDAttribute> attributes = openIdAuthentication.getAttributes();
				/*
				 * fill user with additional attributes
				 * 
				 * FIXME: this works together with the attributes requested
				 * in bibsonomy2-servlet-security.xml and is fixed to one 
				 * specific scheme of attribute exchange. This means, it does 
				 * not work with all OpenID providers. E.g., for Google I got
				 * only the email address.
				 */
				for (final OpenIDAttribute openIDAttribute : attributes) {
					final String name = openIDAttribute.getName();
					// we pick the first value
					final String value = openIDAttribute.getValues().get(0);
					if ("email".equals(name)) {
						user.setEmail(value);
					} else if ("nickname".equals(name)) {
						user.setName(value);
					} else if ("fullname".equals(name)) {
						user.setRealname(value);
					} else if ("gender".equals(name)) {
						user.setGender(value);
					} else if ("language".equals(name)) {
						user.getSettings().setDefaultLanguage(value);
					} else if ("city".equals(name)) {
						user.setPlace(value);
					} else if ("web".equals(name)) {
						try {
							user.setHomepage(new URL(value));
						} catch (MalformedURLException ex) {
							// ignore
						}
					}
				}
			}
		}
		
		return user;
	}
	
}
