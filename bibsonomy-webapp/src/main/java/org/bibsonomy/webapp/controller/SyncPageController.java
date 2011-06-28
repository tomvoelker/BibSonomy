package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.sync.SynchronizationClient;
import org.bibsonomy.webapp.command.SyncPageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SyncPageController implements MinimalisticController<SyncPageCommand>, ErrorAware{
	
	private static final Log log = LogFactory.getLog(SyncPageController.class);
	
	private Errors errors;
	private LogicInterface logic;
	private SyncLogicInterface syncLogic;
	private SynchronizationClient syncClient;
	
	@Override
	public SyncPageCommand instantiateCommand() {
		return new SyncPageCommand();
	}

	@Override
	public View workOn(SyncPageCommand command) {
		
		if(!present(syncClient)) {
			errors.reject("error.synchronization.noclient");
			return Views.ERROR;
		}
		
		List<SyncService> userServices;
		if(!command.getContext().getUserLoggedIn()) {
			throw new AccessDeniedException("user isn't logged in");
		}
		
		log.debug("try to get sync services for user");
		userServices = syncLogic.getSyncServerForUser(command.getContext().getLoginUser().getName());
		
		log.debug("try to get synchronization data from remote service");
		for (SyncService syncService : userServices) {
			Map<String, SynchronizationData> syncData = syncClient.getLastSyncData(syncService, ConstantID.ALL_CONTENT_TYPE);
			syncService.setLastSyncData(syncData);
		}
		
		command.setSyncServices(userServices);
		
		return Views.SYNCPAGE;
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
		this.logic = logic;
		//FIXME remove after integration
		if(logic instanceof SyncLogicInterface) {
			syncLogic = (SyncLogicInterface) logic;
		}
	}

	/**
	 * @return the logic
	 */
	public LogicInterface getLogic() {
		return logic;
	}

	/**
	 * @param syncClient the syncClient to set
	 */
	public void setSyncClient(SynchronizationClient syncClient) {
		this.syncClient = syncClient;
	}

	/**
	 * @return the syncClient
	 */
	public SynchronizationClient getSyncClient() {
		return syncClient;
	}

}
