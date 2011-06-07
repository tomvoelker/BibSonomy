package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.util.IbatisSyncDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationClients;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationController extends AjaxController implements MinimalisticController<AjaxSynchronizationCommand>{
		
	@Override
	public AjaxSynchronizationCommand instantiateCommand() {
		AjaxSynchronizationCommand command =  new AjaxSynchronizationCommand();
		User user = requestLogic.getLoginUser();
		if(present(user)) {
			command.setUserName(user.getName());
		}
		return command;
	}

	@Override
	public View workOn(AjaxSynchronizationCommand command) {
		/*
		 * only admins can synchronize!
		 */
		User currentUser = requestLogic.getLoginUser();
		if (!present(currentUser) || !currentUser.getRole().equals(Role.ADMIN)) {
			return Views.ERROR;
		}
		
		switch(command.getSyncAction()){
			case 0: //show sync page 
				return handleSyncPage(command);
			case 1: //add new sync service
				return addNewService(command);
			case 2: //remove a sync service
				return deleteService(command);
			case 3: //update a sync service
				return updateService(command);
			case 4: //start synchronization
				return synchronize(command);
			default:
				return Views.ERROR;
		}
	}
		
	private View addNewService(AjaxSynchronizationCommand command) {
		//TODO check user data (valid name & api key )
		
		final JSONObject json = new JSONObject();
		
		
		Properties userCredentials = new Properties();
		String syncUserName = command.getSyncUserName();
		String apiKey = command.getApiKey();
		if(present(syncUserName) && present(apiKey)){
			userCredentials.put("userName", syncUserName);
			userCredentials.put("apiKey", apiKey);
		}
		
		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;
		
		//FIXME 
		URI uri;
		try {
			uri = new URI("www.puma.de");
		} catch (URISyntaxException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		syncLogic.createSyncServer(command.getUserName(), command.getService(), userCredentials);
		
		
		json.put("status", "OK");
		command.setResponseString(json.toString());
		return Views.AJAX_JSON;
	}
	
	private View deleteService (AjaxSynchronizationCommand command) {
		final JSONObject json = new JSONObject();
		
		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;
		syncLogic.deleteSyncServer(command.getUserName(), command.getService());
		
		//TODO what is to do with server sync_data entries?
		
		json.put("serviceId", command.getServiceId());
		command.setResponseString(json.toString());
		return Views.AJAX_JSON;
	}
	
	private View updateService(AjaxSynchronizationCommand command) {
		//TODO check user data (valid name & api key )
		
		Properties userCredentials = new Properties();
		String syncUserName = command.getSyncUserName();
		String apiKey = command.getApiKey();
		if(present(syncUserName) && present(apiKey)){
			userCredentials.put("userName", syncUserName);
			userCredentials.put("apiKey", apiKey);
		}
		
		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;
		
		syncLogic.updateSyncServer(command.getUserName(), command.getService(), userCredentials);
		
		final JSONObject json = new JSONObject();
		
		json.put("status", "OK");
		command.setResponseString(json.toString());
		return Views.AJAX_JSON;
	}
	
	private View handleSyncPage(AjaxSynchronizationCommand command) {
		List<SyncService> userServices;
		
		//TODO remove cast and use logic after adding from SyncLogicInterface to LogicInterface
		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;
		
		userServices = syncLogic.getSyncServicesForUser(command.getUserName());
		
		/*
		 * get server logic
		 */
		LogicInterface serverLogic;
		DBLogicApiInterfaceFactory factory = new DBLogicApiInterfaceFactory();
		
		for (SyncService service : userServices) {
			switch (SynchronizationClients.getByUri(service.getService())) {
			case BIBSONOMY:
				break;
			case PUMA:
				factory.setDbSessionFactory(new IbatisSyncDBSessionFactory());
				break;
			default:
				//TODO error handling
				break;
			}
			

			Properties user = service.getServerUser();
			String userName = user.getProperty("userName");
			serverLogic = factory.getLogicAccess(userName, user.getProperty("apiKey"));
			
			/*
			 * TODO remove cast
			 */
			SyncLogicInterface syncServerLogic = (SyncLogicInterface)serverLogic;
			for (int i = 1; i<=2; i++) {
				Class<? extends Resource> resourceType = null;
				if (i == 1) {
					resourceType = Bookmark.class;
				} else if(i == 2) {
					resourceType = BibTex.class;
				} else {
					//TODO error handling
				}
				SynchronizationData data = syncServerLogic.getLastSynchronizationDataForUserForContentType(userName, service.getService(), resourceType);
				if(present(data) && present(data.getLastSyncDate())) {
					service.getLastSyncDates().put(i, data.getLastSyncDate());
					service.getLastResults().put(i, data.getStatus());
				}	
			}
			
		}
		command.setSyncServices(userServices);
		return Views.SYNCPAGE;
	}
	
	private View synchronize(AjaxSynchronizationCommand command) {
		return Views.ERROR;
//		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;
//		
//		DBLogicApiInterfaceFactory factory = new DBLogicApiInterfaceFactory();
//		
//		switch (SynchronizationClients.getByUri(command.getService())) {
//			case BIBSONOMY:
//				factory.setDbSessionFactory(new IbatisDBSessionFactory());
//				break;
//			case PUMA:
//				factory.setDbSessionFactory(new IbatisSyncDBSessionFactory());
//				break;
//			default:
//				break;
//		}
//		
//		
//		List<SyncService> services = syncLogic.getSyncServicesForUser(command.getUserName());
//		
//		String serviceName = SynchronizationClients.getById(command.getServiceId()).toString();
//		SyncService reqService = null;
//		for (SyncService service : services) {
//			service.setServiceName(SynchronizationClients.getById(service.getServiceId()).toString());
//			if(service.getServiceName().equals(serviceName)) {
//				reqService = service;
//			}
//		}
//		Properties user = null;
//		if (reqService != null) {
//			user = reqService.getServerUser();
//		}
//		if(!present(user)) {
//			
//			return Views.AJAX_ERRORS;
//		}
//		@SuppressWarnings("null")
//		String serverUserName = user.getProperty("userName");
//		LogicInterface serverLogic = factory.getLogicAccess(serverUserName, user.getProperty("apiKey"));
//		
//		
//		SynchronizationClient syncClient = new SynchronizationClient();
//		User serverUser = new User();
//		serverUser.setName(command.getSyncUserName());
//		syncClient.synchronize(serverLogic.getAuthenticatedUser(), logic.getAuthenticatedUser(), serverLogic, logic, String.valueOf(command.getServiceId()), "0");
//		syncClient.synchronize(serverUser, logic.getAuthenticatedUser(), serverLogic, logic, Bookmark.class);
//		
//		
//		final JSONObject json = new JSONObject();
//			json.put("service", command.getServiceId());
//			//FIXME service id must be id of the client service on server!
//			addData(json, Bookmark.class, (SyncLogicInterface)serverLogic, serverUserName, command.getService());
//			addData(json, BibTex.class, (SyncLogicInterface)serverLogic, serverUserName, command.getService());
//
//		
//		command.setResponseString(json.toString());		
//		return Views.AJAX_JSON;
	}
	
	private void addData (JSONObject json, Class<? extends Resource> resourceType, SyncLogicInterface syncLogic, String userName, URI service) {
		SynchronizationData data = syncLogic.getLastSynchronizationDataForUserForContentType(userName, service, resourceType);
		if (present(data)) {
			HashMap<String, Object> values = new HashMap<String, Object>();
			values.put("date", data.getLastSyncDate().getTime());
			values.put("result", data.getStatus());
			values.put("contentType", resourceType.getClass());
			json.put(ConstantID.getContentTypeByClass(resourceType), values);
		}
	}
	
}
