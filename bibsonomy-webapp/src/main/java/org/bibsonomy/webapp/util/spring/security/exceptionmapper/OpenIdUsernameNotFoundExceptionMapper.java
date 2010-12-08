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
				
				final List<OpenIDAttribute> attributes = openIdAuthentication.getAttributes();
				/*
				 * fill user
				 */
//				
//				user.setName(openIDUser.getName());
//				user.setEmail(openIDUser.getEmail());
//				user.setRealname(openIDUser.getRealname());
//				user.setGender(openIDUser.getGender());
//				user.setPlace(openIDUser.getPlace());
				user.setOpenID(openIdAuthentication.getIdentityUrl());
			}
		}
		
		return user;
	}
	
}
