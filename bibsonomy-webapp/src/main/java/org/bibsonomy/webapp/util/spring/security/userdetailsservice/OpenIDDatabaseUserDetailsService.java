package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author dzo
 * @version $Id$
 */
public class OpenIDDatabaseUserDetailsService extends DatabaseUserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		username = this.adminLogic.getOpenIDUser(username);
		final User user = this.getUserFromDatabase(username);
		
		if (Role.DELETED.equals(user.getRole())) {
			throw new UsernameNotFoundException("User was deleted");
		}
		
		return new UserAdapter(user);
	}
}
