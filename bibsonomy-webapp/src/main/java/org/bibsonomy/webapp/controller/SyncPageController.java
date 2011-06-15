package org.bibsonomy.webapp.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SyncPageController implements MinimalisticController<SyncPageCommand>, ErrorAware{
	
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
		// TODO error handling
		
		List<SyncService> userServices;
		
		userServices = syncLogic.getSyncServerForUser(command.getContext().getLoginUser().getName());
		
		for (SyncService syncService : userServices) {
			Map<Integer, SynchronizationData> syncData = syncClient.getLastSyncData(syncService, ConstantID.ALL_CONTENT_TYPE);
			for (int key : syncData.keySet()) {
				long longKey = key;
				syncService.getLastSyncData().put(longKey, syncData.get(key));
			}
			
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
