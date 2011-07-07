package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.webapp.command.admin.AdminSyncCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

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
	public View workOn(AdminSyncCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* 
		 * Check user role
		 * If user is not logged in or not an admin: show error message 
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		/*
		 * FIXME: remove after integration
		 */
		SyncLogicInterface syncLogic = (SyncLogicInterface)logic;
		
		
		String action = command.getAction();
		if (present(action)) {
			return performAction(command);
		}
		
		/*
		 * get services and clients from db
		 */
		command.setAvlClients(syncLogic.getAvlSyncServices(false));
		command.setAvlServer(syncLogic.getAvlSyncServices(true));
		
		return Views.ADMIN_SYNC;
	}
	
	private View performAction (AdminSyncCommand command) {
		URI service = uriFromString(command.getService());
		
		/*
		 * TODO remove after integration
		 */
		SyncLogicInterface syncLogic = (SyncLogicInterface)logic;
		
		String action = command.getAction();
		if(!present(service)){
			//something wrong with uri
			return new ExtendedRedirectView("/admin/sync");
		}
		if(action.equals(CREATE_SERVICE)) {
			try {
				syncLogic.createSyncService(service, command.isServer());
			} catch (RuntimeException ex) {
				/*
				 * catch duplicates
				 */
				log.error(ex.getMessage(), ex);
			}
		} else if (action.equals(DELETE_SERVICE)) {
			syncLogic.deleteSyncService(service, command.isServer());
		} else {
			/*
			 * unknown action, do nothing
			 */
		}
		return new ExtendedRedirectView("/admin/sync");
	}
	
	private URI uriFromString(String uriString) {
		if(present(uriString) && uriString.length() > 0) {
			try {
				return new URI(uriString);
			} catch (URISyntaxException ex) {
				log.error("URI is malformed");
				ex.printStackTrace();
			}
		}
		log.error("URI is empty");
		return null;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the logic
	 */
	public LogicInterface getLogic() {
		return logic;
	}

}
