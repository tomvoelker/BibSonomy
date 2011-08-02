package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationAction;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.sync.TwoStepSynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationController extends AjaxController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware {

	private static final String SESSION_KEY = "SYNC_PLAN_";
	private Errors errors;
	private TwoStepSynchronizationClient client;
	private MessageSource messageSource;
	private String projectHome;
	
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


		/*
		 * check server URI from service name
		 */
		final URI serviceName = command.getServiceName();
		if (!present(serviceName)) {
			errors.rejectValue("serviceName", "error.field.required");
		}

		if (errors.hasErrors()) {
			return Views.AJAX_ERRORS;
		}

		/*
		 * 
		 */
		final JSONObject json = new JSONObject();
		switch (requestLogic.getHttpMethod()) {
		case GET:
			/*
			 * get new sync plan
			 */
			final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan;
			try {
				syncPlan = client.getSyncPlan(logic, serviceName);
			} catch (final SynchronizationRunningException e) {
				errors.reject("error.synchronization.running");
				return Views.AJAX_ERRORS;
			}
			/*
			 * store it in session for later use
			 */
			this.setSyncPlan(serviceName, syncPlan);
			/*
			 * serialize it to show user
			 */
			json.put("syncPlan", serializeSyncPlan(syncPlan, serviceName));
			break;
		case POST:
			/*
			 * get sync plan from session
			 */
			final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan2 = this.getSyncPlan(serviceName);
			if (!present(syncPlan2)) {
				errors.reject("error.synchronization.no_sync_plan_found");
				return Views.AJAX_ERRORS;
			}
			/*
			 * run sync plan
			 */
			final Map<Class<? extends Resource>, SynchronizationData> syncResult;
			try {
				syncResult = client.synchronize(logic, serviceName, syncPlan2);
			} catch (final SynchronizationRunningException e) {
				errors.reject("error.synchronization.running");
				return Views.AJAX_ERRORS;
			}
			/*
			 * remove sync plan from session
			 * FIXME: do this before synchronize()?
			 */
			this.setSyncPlan(serviceName, null);
			/*
			 * serialize result
			 */
			json.put("syncData", serializeSyncData(syncResult));
			break;
		case DELETE:
			/*
			 * delete last synchronization data
			 */
			// FIXME: does a delete make sense? 
			// Not so simple to get parameters
//			for (final Class<? extends Resource> resourceType : ResourceUtils.getResourceTypesByClass(syncService.getResourceType())) {
//				client.deleteSyncData(serviceName, resourceType, syncDate);
//			}
			if (present(command.getResetSyncService())) {
				SyncService service = getSyncServer(command.getSyncServer());
				client.deleteSyncData(service, BibTex.class, null);
				client.deleteSyncData(service, Bookmark.class, null);
			}
			break;
		default:
			/*
			 * FIXME: what to do here?
			 */
			break;
		}
		
		command.setResponseString(json.toString());

		return Views.AJAX_JSON;
	}
	
	/**
	 * Retrieves the sync plan from the session. If no plan is found, <code>null</code> is returned.
	 * @param serviceName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<Class<? extends Resource>, List<SynchronizationPost>> getSyncPlan(final URI serviceName) {
		final Object sessionAttribute = requestLogic.getSessionAttribute(SESSION_KEY + serviceName);
		if (!present(sessionAttribute) || !(sessionAttribute instanceof Map<?,?>)) {
			return null;
		}
		return (Map<Class<? extends Resource>, List<SynchronizationPost>>) sessionAttribute;
	}
	
	/**
	 * Stores the sync plan in the session.
	 * 
	 * @param serviceName
	 * @param syncPlan
	 */
	private void setSyncPlan(final URI serviceName, final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan) {
		requestLogic.setSessionAttribute(SESSION_KEY + serviceName, syncPlan);
	}
	

	private JSONObject serializeSyncData(final Map<Class<? extends Resource>, SynchronizationData> data) {
		final JSONObject json = new JSONObject();

		for (final Entry<Class<? extends Resource>, SynchronizationData> entry : data.entrySet()) {
			final SynchronizationData value = entry.getValue();
			final HashMap<String, Object> values = new HashMap<String, Object>();
			values.put("date", value.getLastSyncDate().getTime());
			values.put("status", value.getStatus());
			values.put("info", value.getInfo());
			json.put(entry.getKey().getSimpleName(), values);
		}
		return json;
	}

	/**
	 * Currently, the method counts all operations and then creates human-readable
	 * messages that describe what will happen.
	 * 
	 * @param syncPlan
	 * @return
	 */
	private JSONObject serializeSyncPlan(final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan, final URI serverName) {
		final JSONObject json = new JSONObject();
		final Locale locale = requestLogic.getLocale();
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
			json.put(resourceType.getSimpleName(), messages);
		}
		return json;
	}
	
	/**
	 * Finds the sync service in the list whose update/create form was send. 
	 * 
	 * @param syncServices
	 * @return
	 */
	private SyncService getSyncServer(final List<SyncService> syncServices) {
		for (final SyncService syncService : syncServices) {
			if (present(syncService.getService())) return syncService;
		}
		return null;
	}

	
	@Override
	public AjaxSynchronizationCommand instantiateCommand() {
		return new AjaxSynchronizationCommand();
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
	 * @param client the client to set
	 */
	public void setClient(final TwoStepSynchronizationClient client) {
		this.client = client;
	}

	/** 
	 * The message source is necessary to render a human-readable synchronization plan. 
	 * @param messageSource
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * To render a human-readable message for the sync plan
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
}
