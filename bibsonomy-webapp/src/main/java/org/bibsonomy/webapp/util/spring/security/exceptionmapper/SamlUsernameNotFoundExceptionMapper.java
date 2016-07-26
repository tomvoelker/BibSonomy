/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import javax.servlet.http.HttpSession;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.SamlUsernameNotFoundException;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.UserAttributeMapping;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 * @author jensi
 */
public class SamlUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {
	
	/** creds for later authentication */
	public static final String ATTRIBUTE_SAML_CREDS = "SAML_CREDS";
	
	
	private UserAttributeMapping<SAMLCredential, ?> attributeExtractor;
	
	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && SamlUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}

	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof SamlUsernameNotFoundException) {
			final SAMLCredential ctx = ((SamlUsernameNotFoundException) e).getSamlCreds();
			this.attributeExtractor.populate(user, ctx);
		}
		user.setToClassify(0);
		return user;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.spring.security.exceptionmapper.UsernameNotFoundExceptionMapper#writeAdditionAttributes(javax.servlet.http.HttpSession)
	 */
	@Override
	public void writeAdditionAttributes(final HttpSession session, final UsernameNotFoundException e) {
		if (e instanceof SamlUsernameNotFoundException) {
			final SAMLCredential ctx = ((SamlUsernameNotFoundException) e).getSamlCreds();
			session.setAttribute(ATTRIBUTE_SAML_CREDS, ctx);
		}
	}

	/**
	 * @return the attributeExtractor
	 */
	public UserAttributeMapping<SAMLCredential,?> getAttributeExtractor() {
		return this.attributeExtractor;
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(UserAttributeMapping<SAMLCredential, ?> attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

}
