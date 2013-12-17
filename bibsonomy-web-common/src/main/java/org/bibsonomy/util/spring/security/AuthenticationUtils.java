/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util.spring.security;

import org.bibsonomy.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * class for accessing credentials as provided by spring security
 * 
 * @author fei, dzo
  */
public class AuthenticationUtils {

	/**
	 * Small helper method to easily retrieve the logged in user. If nobody is logged in, an anonymous dummy user is created.
	 * 
	 * How does this work? Using a static method to retrieve thread-specific
	 * information? Looks like some Java magic. :-O - yes it is. the context is
	 * saved in a ThreadLocal
	 * 
	 * @return the user (never null)
	 */
	public static User getUser() {
		User user = getUserOrNull();
		if (user != null) {
			return user;
		}
		return new User();
	}

	/**
	 * 	Small helper method to easily retrieve the logged in user. In contrast to {@link #getUser()}, this may return null.
	 * @return the user or null
	 */
	public static User getUserOrNull() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal != null && principal instanceof UserAdapter) {
				final UserAdapter adapter = (UserAdapter) principal;
				return adapter.getUser();
			}
		}
		return null;
	}

}
