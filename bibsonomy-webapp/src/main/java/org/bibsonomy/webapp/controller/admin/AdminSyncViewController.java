package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.webapp.command.admin.AdminSyncCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author wla
 * @version $Id$
 */
public class AdminSyncViewController implements MinimalisticController<AdminSyncCommand> {

	private static final Log log = LogFactory.getLog(AdminSyncViewController.class);
	private final String CREATE_SERVICE = "createService";
	private final String DELETE_SERVICE = "deleteService";
	
	private LogicInterface logic;
	
	@Override
	public AdminSyncCommand instantiateCommand() {
		return new AdminSyncCommand();
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
		
		/*
		 * TODO: sync: remove after integration
		 */
		final SyncLogicInterface syncLogic = (SyncLogicInterface)logic;
		
		final String action = command.getAction();
		if (present(action)) {
			return performAction(command);
		}
		
		/*
		 * get services and clients from db
		 */
		command.setAvlClients(syncLogic.getSyncServices(false));
		command.setAvlServer(syncLogic.getSyncServices(true));
		
		return Views.ADMIN_SYNC;
	}
	
	private View performAction (final AdminSyncCommand command) {
		final URI service = command.getService();
		
		/*
		 * TODO: sync: remove after integration
		 */
		final SyncLogicInterface syncLogic = (SyncLogicInterface)logic;
		
		final String action = command.getAction();
		if (!present(service)) {
			// something wrong with uri
			return new ExtendedRedirectView("/admin/sync");
		}
		if (CREATE_SERVICE.equals(action)) {
			try {
				syncLogic.createSyncService(service, command.isServer());
			} catch (final RuntimeException ex) {
				/*
				 * catch duplicates
				 */
				log.error(ex.getMessage(), ex);
			}
		} else if (DELETE_SERVICE.equals(action)) {
			syncLogic.deleteSyncService(service, command.isServer());
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
