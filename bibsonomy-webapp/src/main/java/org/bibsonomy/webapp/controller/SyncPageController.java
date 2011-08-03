package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationAction;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.sync.TwoStepSynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.controller.ajax.SynchronizationController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
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
	private SyncLogicInterface syncLogic;
	private TwoStepSynchronizationClient syncClient;

	private RequestLogic requestLogic;
	private MessageSource messageSource;
	private String projectHome;

	@Override
	public AjaxSynchronizationCommand instantiateCommand() {
		return new AjaxSynchronizationCommand();
	}

	@Override
	public View workOn(AjaxSynchronizationCommand command) {
		
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
			this.errors.reject("error.field.valid.ckey");
		}
		
		if (!command.getContext().getUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		
		if (!present(syncClient)) {
			errors.reject("error.synchronization.noclient");
			return Views.ERROR;
		}
		
		log.debug("try to get sync services for user");
		final List<SyncService> userServices = syncLogic.getSyncServer(command.getContext().getLoginUser().getName());
		
		log.debug("try to get synchronization data from remote service");
		for (final SyncService syncService : userServices) {
			final Map<String, SynchronizationData> syncData = new HashMap<String, SynchronizationData>();
			try {
				for (final Class<? extends Resource> resourceType : ResourceUtils.getResourceTypesByClass(syncService.getResourceType())) {
					SynchronizationData lastSyncData;
					boolean missedPlan = false;
					Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan;
					do {
						missedPlan = false;
						lastSyncData = syncClient.getLastSyncData(syncService, resourceType);
						
						if(lastSyncData.getStatus().equals(SynchronizationStatus.PLANNED)) {
							syncPlan = SynchronizationController.getSyncPlan(syncService.getService(), requestLogic);
							if(!present(syncPlan)) {
								missedPlan = true;
								syncClient.deleteSyncData(syncService, resourceType, lastSyncData.getLastSyncDate());
							} else {
								//TODO get plan from the session and show it
								syncService.setPlan(getPlan(syncPlan, syncService.getService().toString()));
							}
						}
					} while (missedPlan);
					syncData.put(resourceType.getSimpleName(), lastSyncData);
				}
			} catch (AccessDeniedException e) {
				log.debug("access denied to remote service " + syncService.getService().toString());
			}
			syncService.setLastSyncData(syncData);
		}
		
		command.setSyncServer(userServices);
		
		return Views.SYNC;
	}
	
	/**
	 * 
	 * @param syncPlan
	 * @param serverName
	 * @return
	 */
	private Map<Class<? extends Resource>, Map<String, String>> getPlan(Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan, String serverName) {
		Locale locale = requestLogic.getLocale();
		final Map<Class<? extends Resource>, Map<String, String>> result = new LinkedHashMap<Class<? extends Resource>, Map<String,String>>();
		for (final Entry<Class<? extends Resource>, List<SynchronizationPost>> entry : syncPlan.entrySet()) {
			int createClient = 0;
			int updateClient = 0;
			int deleteClient = 0;
			int createServer = 0;
			int updateServer = 0;
			int deleteServer = 0;
			int ok = 0;
			final Class<? extends Resource> resourceType = entry.getKey();
			for (final SynchronizationPost synchronizationPost : entry.getValue()) {
				final SynchronizationAction action = synchronizationPost.getAction();
				switch (action) {
				case CREATE_CLIENT:
					createClient++;
					break;
				case UPDATE_CLIENT:
					updateClient++;
					break;
				case DELETE_CLIENT:
					deleteClient++;
					break;
				case CREATE_SERVER:
					createClient++;
					break;
				case UPDATE_SERVER:
					updateClient++;
					break;
				case DELETE_SERVER:
					deleteClient++;
					break;
				case OK:
					ok++;
					break;
				default:
					break;
				}
			}
			final Map<String, String> messages = new LinkedHashMap<String, String>();
			messages.put("CLIENT", messageSource.getMessage("synchronization.syncPlan.message", new Object[]{projectHome, createClient, updateClient, deleteClient}, locale));
			messages.put("SERVER", messageSource.getMessage("synchronization.syncPlan.message", new Object[]{serverName, createServer, updateServer, deleteServer}, locale));
			messages.put("OTHER", messageSource.getMessage("synchronization.syncPlan.message.other", new Object[]{ok}, locale));
			result.put(resourceType, messages);
		}
		return result;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		//FIXME remove after integration
		if (logic instanceof SyncLogicInterface) {
			syncLogic = (SyncLogicInterface) logic;
		}
	}

	/**
	 * @param syncClient the syncClient to set
	 */
	public void setSyncClient(TwoStepSynchronizationClient syncClient) {
		this.syncClient = syncClient;
	}
	
	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
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
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}


}
