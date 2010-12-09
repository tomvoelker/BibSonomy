package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * @author rja
 * @version $Id$
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
					} else if ("country".equals(name)) {
						user.setPlace(value);
					}
				}
			}
		}
		
		return user;
	}
	
}
