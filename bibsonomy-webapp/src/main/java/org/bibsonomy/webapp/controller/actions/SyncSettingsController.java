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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.sync.SyncUtils;
import org.bibsonomy.webapp.validation.SyncSettingsValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * @author wla, vhem
 */
public class SyncSettingsController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand> {
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setSyncServer(new LinkedList<SyncService>());
		return command;
	}

	/** 
	 * @see org.bibsonomy.webapp.controller.SettingsPageController#workOn(org.bibsonomy.webapp.command.SettingsViewCommand)
	 */
	@Override
	public View workOn(final SettingsViewCommand command) {
		
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * some security checks
		 */
		if (!context.isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		final User loginUser = context.getLoginUser();
		if (loginUser.isSpammer()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		if (!context.isValidCkey()) {
			/*
			 * If user is coming from one of the configured sync services, we
			 * should better show a hint that he should now acknowledge the sync
			 * setting. 
			 */
			final String referer = requestLogic.getReferer();
			final SyncService newSyncServer = command.getNewSyncServer();
			final URI service = newSyncServer.getService();
			if (present(referer) && present(newSyncServer) && present(service) && referer.startsWith(service.toString())) { 
				this.errors.rejectValue("newSyncServer.service", "synchronization.server.add.acknowledge", new Object[]{service}, "please acknowledge the new synchronization server");
			} else {
				this.errors.reject("error.field.valid.ckey");
			}
		}
		
		final String loginUserName = loginUser.getName();
		/*
		 * Get the service whose form has been sent (i.e., that 
		 * should be updated or deleted)
		 */
		final SyncService syncServer = getSyncServer(command.getSyncServer(), loginUserName);

		final HttpMethod httpMethod = this.requestLogic.getHttpMethod();
		
		if (errors.hasErrors()) {
			final View view = super.workOn(command);
			/*
			 * On update, we replace the sync service from the DB with the one
			 * the user wants to update.
			 */
			if (HttpMethod.PUT.equals(httpMethod)) {
				replaceSyncService(command.getSyncServer(), syncServer);
			} 
			return view; 
		}

		switch (httpMethod) {
		case POST:
			final SyncService newSyncServer = command.getNewSyncServer();
			this.logic.createSyncServer(loginUserName, newSyncServer);
			// forward user to sync-page to perform an initial sync in BOTH directions first 
			if (SyncUtils.checkInitialAutoSync(newSyncServer))
				return new ExtendedRedirectView("/sync");
			break;
		case PUT:
			this.logic.updateSyncServer(loginUserName, syncServer);
			// forward user to sync-page to perform an initial sync in BOTH directions first 
			if (SyncUtils.checkInitialAutoSync(syncServer))
				return new ExtendedRedirectView("/sync");
			break;
		case DELETE:
			this.logic.deleteSyncServer(loginUserName, syncServer.getService());
			break;
		default:
			errors.reject("error.general");
			break;
		}

		return new ExtendedRedirectView("/settings?selTab=" + SettingsViewCommand.SYNC_IDX);
	}
	
	/**
	 * Finds the sync service in the list whose update/create form was send. 
	 * 
	 * @param syncServices
	 * @return
	 */
	private SyncService getSyncServer(final List<SyncService> syncServices, final String userName) {
		for (final SyncService syncService : syncServices) {
			if (present(syncService.getService()))
			{
				// get initialAutoSync value for configured sync-server from database
				List<SyncService> serviceDetails = this.logic.getSyncServiceSettings(userName, syncService.getService(), true);
				if (present(serviceDetails)) syncService.setInitialAutoSync(serviceDetails.get(0).getInitialAutoSync());
				
				return syncService;
			}
		}
		return null;
	}
	
	/**
	 * Finds the given syncService in the list and replaces it.
	 *  
	 * @param syncServices
	 */
	private static void replaceSyncService(final List<SyncService> syncServices, final SyncService syncService) {
		for (int i = 0; i < syncServices.size(); i++) {
			final SyncService next = syncServices.get(i);
			if (next.getService().equals(syncService.getService())) {
				syncServices.set(i, syncService);
			}
		}
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return new SyncSettingsValidator();
	}

	@Override
	public boolean isValidationRequired(final SettingsViewCommand command) {
		return true;
	}
	
}
