/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author dzo
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
