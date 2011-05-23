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
 * @version $Id$
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
