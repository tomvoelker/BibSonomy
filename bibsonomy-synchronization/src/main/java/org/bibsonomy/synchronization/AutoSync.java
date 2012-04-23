package org.bibsonomy.synchronization;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * @author wla
 * @version $Id$
 */
public class AutoSync {
	private static final Log log = LogFactory.getLog(AutoSync.class);

	private LogicInterface adminLogic;
	private TwoStepSynchronizationClient syncClient;
	private LogicInterfaceFactory userLogicFactory;
	
	
	/**
	 * Performs automatic synchronization for all user which have selected this
	 */
	public void performAutoSync() {
		log.info("start automatic synchronization");
		
		if(!present(syncClient)) {
			log.info("snyc client not available");
			return;
		}
		
		List<SyncService> syncServices = adminLogic.getSyncService(null, null, true);
		for (SyncService syncService : syncServices) {
			//skip, if autosync not selected or direction is both
			if(!syncService.isAutosync() || syncService.getDirection() == SynchronizationDirection.BOTH) {
				continue;
			}
			
			
			User clientUser = adminLogic.getUserDetails(syncService.getUserName());
			log.info("Autosync for user:" + clientUser.getName() + " and service: " + syncService.getService().toString() + " api: " + syncService.getSecureAPI());
			try {
				LogicInterface clientLogic = userLogicFactory.getLogicAccess(clientUser.getName(), clientUser.getApiKey());
				
				Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan = syncClient.getSyncPlan(clientLogic, syncService);
				if (!present(syncPlan)) {
					log.info("no sync plan received");
					continue;
				}
				
				log.info("sync plan created");
				/*
				 * run sync plan
				 */
				try {
					Map<Class<? extends Resource>, SynchronizationData> syncData = syncClient.synchronize(clientLogic, syncService, syncPlan);
					for (Entry<Class<? extends Resource>, SynchronizationData> data : syncData.entrySet()) {
						log.info(data.getValue().getInfo());
					}
				} catch (final SynchronizationRunningException e) {
					//FIXME handle this, i think it is nothing to do in this case
					log.debug(e.getMessage());
				}
			} catch (BadRequestOrResponseException ex) {
				log.error(ex.getMessage());
				continue;
			}
		}
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @return the syncClient
	 */
	public TwoStepSynchronizationClient getSyncClient() {
		return syncClient;
	}

	/**
	 * @param synclient the syncClient to set
	 */
	public void setSyncClient(TwoStepSynchronizationClient synclient) {
		this.syncClient = synclient;
	}

	/**
	 * @return the userLogicFactory
	 */
	public LogicInterfaceFactory getUserLogicFactory() {
		return this.userLogicFactory;
	}

	/**
	 * @param userLogicFactory the userLogicFactory to set
	 */
	public void setUserLogicFactory(LogicInterfaceFactory userLogicFactory) {
		this.userLogicFactory = userLogicFactory;
	}

}
