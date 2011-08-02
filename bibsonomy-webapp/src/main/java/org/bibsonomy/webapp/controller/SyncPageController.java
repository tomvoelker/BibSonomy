package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.sync.TwoStepSynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
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
					syncData.put(resourceType.getSimpleName(), syncClient.getLastSyncData(syncService, resourceType));
				}
			} catch (AccessDeniedException e) {
				log.debug("access denied to remote service " + syncService.getService().toString());
			}
			syncService.setLastSyncData(syncData);
		}
		
		command.setSyncServer(userServices);
		
		return Views.SYNC;
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


}
