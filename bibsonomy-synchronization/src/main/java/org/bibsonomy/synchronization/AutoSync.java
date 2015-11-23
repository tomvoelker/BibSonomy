/**
 * BibSonomy-Synchronization - Handles user synchronization between BibSonomy authorities
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.synchronization;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Calendar;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.util.MailUtils;

/**
 * @author wla
 */
public class AutoSync {
	private static final Log log = LogFactory.getLog(AutoSync.class);
	private static Calendar calc;
	private LogicInterface adminLogic;
	private TwoStepSynchronizationClient syncClient;
	private LogicInterfaceFactory userLogicFactory;
	private MailUtils mailUtils;
	
	
	/**
	 * Performs automatic synchronization for all user which have selected this
	 */
	public void performAutoSync() {
		log.info("start automatic synchronization");
	
		if (!present(this.syncClient)) {
			log.info("snyc client not available");
			return;
		}
		
		// get configured AutoSync-Servers
		final List<SyncService> syncServices = this.adminLogic.getAutoSyncServer();
		for (final SyncService syncService : syncServices) {			
			final User clientUser = this.adminLogic.getUserDetails(syncService.getUserName());

			// check if user has run a sync in both-directions before; skip service and send sync-notification mail on Sunday otherwise
			if (syncService.getInitialAutoSync())
			{
				calc = Calendar.getInstance(); 
				if (calc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					log.info("no initial sync in both-directions was done; send eMail to User");
					mailUtils.sendSyncErrorMail(clientUser.getName(), clientUser.getEmail(), syncService.getName(), 
							LocaleUtils.toLocale(clientUser.getSettings().getDefaultLanguage())); 
				}
				continue;
			}
			
			final String userNameToSync = clientUser.getName();
			
			log.info("Autosync for user:" + userNameToSync + " and service: " + syncService.getService().toString() + " api: " + syncService.getSecureAPI());
			try {
				final LogicInterface clientLogic = this.userLogicFactory.getLogicAccess(userNameToSync, clientUser.getApiKey());
				
				final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan = this.syncClient.getSyncPlan(clientLogic, syncService);
				if (!present(syncPlan)) {
					log.info("no sync plan received");
					continue;
				}
				
				log.info("sync plan created");
				/*
				 * run sync plan
				 */
				try {
					final Map<Class<? extends Resource>, SynchronizationData> syncData = this.syncClient.synchronize(clientLogic, syncService, syncPlan);
					for (final Entry<Class<? extends Resource>, SynchronizationData> data : syncData.entrySet()) {
						log.info(data.getValue().getInfo());
					}
				} catch (final SynchronizationRunningException e) {
					// FIXME handle this, i think it is nothing to do in this case
					log.debug(e.getMessage());
				}
			} catch (final BadRequestOrResponseException ex) {
				log.error("error while autosyncing " + userNameToSync, ex);
				continue;
			}
		}
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
	
	/**
	 * @param synclient the syncClient to set
	 */
	public void setSyncClient(final TwoStepSynchronizationClient synclient) {
		this.syncClient = synclient;
	}

	/**
	 * @param userLogicFactory the userLogicFactory to set
	 */
	public void setUserLogicFactory(final LogicInterfaceFactory userLogicFactory) {
		this.userLogicFactory = userLogicFactory;
	}

	/**
	 * @param mailUtils
	 */
	public void setMailUtils(final MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

}
