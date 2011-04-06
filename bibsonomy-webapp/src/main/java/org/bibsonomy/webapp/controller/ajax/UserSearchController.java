package org.bibsonomy.webapp.controller.ajax;

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
		final User loginUser = context.getLoginUser();
		final List<User> users;

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		if (command.getSearch() != null && !command.getSearch().isEmpty()) {
			users = logic.getUsers(null, GroupingEntity.USER, null, null, null, null, null, command.getSearch(), 0, command.getLimit());
			command.setSearchedUsers(users);
		}
		
		// TODO: Currently only json-format is supported
		return Views.getViewByFormat(command.getFormat());
	}

	@Override
	public UserSearchCommand instantiateCommand() {
		return new UserSearchCommand();
	}

}
