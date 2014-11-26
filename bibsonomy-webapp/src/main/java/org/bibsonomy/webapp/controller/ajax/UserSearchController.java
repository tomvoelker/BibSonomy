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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.UserSearchCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * @author bsc
 */
public class UserSearchController extends AjaxController implements MinimalisticController<UserSearchCommand> {
	private static final Log log = LogFactory.getLog(UserSearchController.class);
	
	@Override
	public View workOn(UserSearchCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		
		/* 
		 * Check user role
		 * If user is not logged in or not an admin: show error message 
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(context.getLoginUser().getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		if (present(command.getSearch())) {
			int limit = command.getLimit();
			
			/*
			 * TODO: ugly workaround to deal with showSpammers set to false, which
			 * may result in a too small list of users after the filtering.
			 */
			if (!command.showSpammers()) {
				limit *= 3;
			}
			final List<User> users = logic.getUsers(null, GroupingEntity.USER, null, null, null, null, null, command.getSearch(), 0, limit);
			
			if (!command.showSpammers()) {
				// Remove all spammers
				for (final User user: users) {
					if (user.isSpammer()) {
						users.remove(user);
					}
				}
				// Part 2 of the ugly workaround
				while (users.size() > command.getLimit()) {
					users.remove(users.get(users.size()-1));
				}
			}
			
			command.setUsers(users);
		}
		return Views.JSON;
	}

	@Override
	public UserSearchCommand instantiateCommand() {
		return new UserSearchCommand();
	}

}
