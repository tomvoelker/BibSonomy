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
 * @version $Id$
 */
public class SyncPageController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(SyncPageController.class);

	private Errors errors;
	private LogicInterface logic;
	private TwoStepSynchronizationClient syncClient;

	private RequestLogic requestLogic;
	private MessageSource messageSource;
	private String projectHome;

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
		
		// TODO: remove?
//		if (!context.isValidCkey()) {
//			this.errors.reject("error.field.valid.ckey");
//		}

		if (!present(syncClient)) {
			errors.reject("error.synchronization.noclient");
			return Views.ERROR;
		}

		log.debug("try to get sync services for user");
		final String loggedinUserName = loginUser.getName();
		final List<SyncService> userServices = logic.getSyncService(loggedinUserName, null, true);
		
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
		command.setSyncClients(this.logic.getSyncService(loggedinUserName, null, false));

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
				syncService.setPlan(getPlanSummary(syncPlan, syncService.getService().toString(), requestLogic.getLocale(), messageSource, projectHome));
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
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(final String projectHome) {
		this.projectHome = projectHome;
	}


}
