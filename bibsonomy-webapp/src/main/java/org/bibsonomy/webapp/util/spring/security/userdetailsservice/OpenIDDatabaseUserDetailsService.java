package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
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
			throw new UsernameNotFoundException(""); // TODO
		}
		
		final User user = this.getUserFromDatabase(username);
		
		if (Role.DELETED.equals(user.getRole())) {
			throw new DisabledException("User was deleted");
		}
		
		return new UserAdapter(user);
	}
}
