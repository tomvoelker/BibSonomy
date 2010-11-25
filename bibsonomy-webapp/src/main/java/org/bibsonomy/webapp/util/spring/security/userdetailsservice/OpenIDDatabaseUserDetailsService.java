package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author dzo
 * @version $Id$
 */
public class OpenIDDatabaseUserDetailsService extends DatabaseUserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(final String openID) throws UsernameNotFoundException, DataAccessException {
		final String username = this.adminLogic.getOpenIDUser(openID);
		
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
