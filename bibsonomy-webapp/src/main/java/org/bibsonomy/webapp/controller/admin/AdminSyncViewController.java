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
package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.admin.AdminSyncCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author wla
 */
public class AdminSyncViewController implements MinimalisticController<AdminSyncCommand> {

	private static final Log log = LogFactory.getLog(AdminSyncViewController.class);
	private final String CREATE_SERVICE = "createService";
	private final String DELETE_SERVICE = "deleteService";
	
	private LogicInterface logic;
	
	@Override
	public AdminSyncCommand instantiateCommand() {
		final AdminSyncCommand command = new AdminSyncCommand();
		command.setService(new SyncService());
		return command;
	}

	@Override
	public View workOn(final AdminSyncCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* 
		 * Check user role
		 * If user is not logged in or not an admin: show error message 
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}
		
		final String action = command.getAction();
		if (present(action)) {
			return performAction(command);
		}
		
		/*
		 * get services and clients from db
		 */
		command.setAvlClients(this.logic.getSyncServices(false));
		command.setAvlServer(this.logic.getSyncServices(true));
		
		return Views.ADMIN_SYNC;
	}
	
	private View performAction (final AdminSyncCommand command) {
		final SyncService service = command.getService();
		
		final String action = command.getAction();
		final URI serviceUri = service.getService();
		if (!present(serviceUri)) {
			// something wrong with uri
			return new ExtendedRedirectView("/admin/sync");
		}
		if (CREATE_SERVICE.equals(action)) {
			try {
				logic.createSyncService(service, command.isServer());
			} catch (final RuntimeException ex) {
				/*
				 * catch duplicates
				 */
				log.error(ex.getMessage(), ex);
			}
		} else if (DELETE_SERVICE.equals(action)) {
			logic.deleteSyncService(serviceUri, command.isServer());
		} else {
			/*
			 * unknown action, do nothing
			 */
		}
		return new ExtendedRedirectView("/admin/sync");
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
