/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.webapp.util.sync.SyncUtils.getPlanSummary;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.synchronization.TwoStepSynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.sync.SyncUtils;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * @author wla
 */
public class SyncPageController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(SyncPageController.class);

	private Errors errors;
	private LogicInterface logic;
	private TwoStepSynchronizationClient syncClient;

	private RequestLogic requestLogic;
	private MessageSource messageSource;
	private URLGenerator urlGenerator;

	@Override
	public AjaxSynchronizationCommand instantiateCommand() {
		return new AjaxSynchronizationCommand();
	}

	@Override
	public View workOn(final AjaxSynchronizationCommand command) {
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
		
		if (!present(syncClient)) {
			errors.reject("error.synchronization.noclient");
			return Views.ERROR;
		}

		log.debug("try to get sync services for user");
		final String loggedinUserName = loginUser.getName();
		final List<SyncService> userServices = logic.getSyncServiceSettings(loggedinUserName, null, true);
		
		/*
		 * get all sync data from remote sync service
		 */
		log.debug("try to get synchronization data from remote service");
		for (final SyncService syncService : userServices) {
			final List<SynchronizationData> lastSyncData = new LinkedList<SynchronizationData>();
			try {
				for (final Class<? extends Resource> resourceType : ResourceUtils.getResourceTypesByClass(syncService.getResourceType())) {
					lastSyncData.add(getLastSyncData(syncService, resourceType));
				}
			} catch (final AccessDeniedException e) {
				log.debug("access denied to remote service " + syncService.getService().toString());
			} catch (final Exception e) {
				log.warn("error while getting last sync data", e);
				// TODO: add error message
			}
			syncService.setLastSyncData(lastSyncData);
		}
		command.setSyncServer(userServices);
		
		/*
		 * get all sync clients with the lastest sync data
		 */
		command.setSyncClients(this.logic.getSyncServiceSettings(loggedinUserName, null, false));

		return Views.SYNC;
	}

	/**
	 * Gets the last sync data from the database. If it's status is PLANNED but
	 * no sync plan can be found, the sync data is deleted and the next one is
	 * requested. This repeats until one without status "PLANNED" is found.
	 * 
	 * @param syncService
	 * @param resourceType
	 * @return
	 */
	private SynchronizationData getLastSyncData(final SyncService syncService, final Class<? extends Resource> resourceType) {
		SynchronizationData lastSyncData = null;
		while (present(lastSyncData = syncClient.getLastSyncData(syncService, resourceType))) {
			if (!SynchronizationStatus.PLANNED.equals(lastSyncData.getStatus())) break;
			/*
			 * last status is "PLANNED" -> try to get plan from session 
			 */
			final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan = SyncUtils.getSyncPlan(syncService.getService(), requestLogic);
			if (present(syncPlan)) {
				/*
				 * plan found in session -> get summary and return last sync data
				 */
				syncService.setPlan(getPlanSummary(syncPlan, syncService.getService().toString(), requestLogic.getLocale(), messageSource, urlGenerator.getProjectHome()));
				return lastSyncData;
			}
			/*
			 * not found - remove sync date and try again
			 */
			syncClient.deleteSyncData(syncService, resourceType, lastSyncData.getLastSyncDate());
		}
		return lastSyncData;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param syncClient the syncClient to set
	 */
	public void setSyncClient(final TwoStepSynchronizationClient syncClient) {
		this.syncClient = syncClient;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * 
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
