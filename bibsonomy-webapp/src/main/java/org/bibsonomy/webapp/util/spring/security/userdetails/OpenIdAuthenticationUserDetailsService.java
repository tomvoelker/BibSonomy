package org.bibsonomy.webapp.util.spring.security.userdetails;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.bibsonomy.webapp.util.spring.security.userdetailsservice.DatabaseUserDetailsService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * @author dzo
 */
public class OpenIdAuthenticationUserDetailsService extends DatabaseUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

	@Override
	public UserDetails loadUserDetails(final OpenIDAuthenticationToken token) throws UsernameNotFoundException {
		final String openID = token.getIdentityUrl();
		
		final String username = this.adminLogic.getOpenIDUser(openID == null ? null : openID.trim());
		
		if (username == null) {
			throw new OpenIdUsernameNotFoundException("OpenID not found in database"); 
		}
		
		final User user = this.getUserFromDatabase(username);
		final UserAdapter userAdapter = new UserAdapter(user);
		
		if (!userAdapter.isEnabled()) {
			throw new DisabledException("user was deleted");
		}
		
		return userAdapter;
	}

}
