package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseUserDetailsService implements UserDetailsService {
	
	protected LogicInterface adminLogic;
	
	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
	
	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
		final User user = this.getUserFromDatabase(username);	
		return new UserAdapter(user);
	}

	protected User getUserFromDatabase(String username) throws UsernameNotFoundException {
		if (username == null) {
			throw new UsernameNotFoundException("username was null");
		}
		
		final User user = this.adminLogic.getUserDetails(username);
		
		if (!present(user.getName())) {
			throw new UsernameNotFoundException("user with name " + username + " not found");
		}
		return user;
	}
}
