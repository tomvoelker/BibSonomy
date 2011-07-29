package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
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
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationController extends AjaxController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware {

	private static final String SESSION_KEY = SynchronizationController.class.getName() + "SYNC_PLAN";
	private Errors errors;
	private TwoStepSynchronizationClient client;

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
		final JSONObject json; 
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
			requestLogic.setSessionAttribute(SESSION_KEY, syncPlan);
			/*
			 * serialize it to show user
			 */
			json = serializeSyncPlan(syncPlan);
			break;
		case POST:
			/*
			 * get sync plan from session
			 */
			final Object sessionAttribute = requestLogic.getSessionAttribute(SESSION_KEY);
			if (!present(sessionAttribute) || !(sessionAttribute instanceof Map<?,?>)) {
				errors.reject("error.synchronization.no_sync_plan_found");
				return Views.AJAX_ERRORS;
			}
			final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan2 = (Map<Class<? extends Resource>, List<SynchronizationPost>>) sessionAttribute;
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
			requestLogic.setSessionAttribute(SESSION_KEY, null);
			/*
			 * serialize result
			 */
			json = serializeSyncData(syncResult);
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
			json = new JSONObject();
			break;
		default:
			/*
			 * FIXME: what to do here?
			 */
			json = new JSONObject();
			break;
		}
		
		command.setResponseString(json.toString());

		return Views.AJAX_JSON;
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

	private JSONObject serializeSyncPlan(final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan) {
		final JSONObject json = new JSONObject();
		for (final Entry<Class<? extends Resource>, List<SynchronizationPost>> entry : syncPlan.entrySet()) {
			final Map<SynchronizationAction, Integer> actions = getEmptyActions();
			final Class<? extends Resource> resourceType = entry.getKey();
			for (final SynchronizationPost synchronizationPost : entry.getValue()) {
				final SynchronizationAction action = synchronizationPost.getAction();
				actions.put(action, actions.get(action) + 1);
			}
			json.put(resourceType, actions);
		}
		return json;
	}
	
	private Map<SynchronizationAction, Integer> getEmptyActions() {
		final Map<SynchronizationAction, Integer> actions = new HashMap<SynchronizationAction, Integer>();
		for (final SynchronizationAction action : SynchronizationAction.values()) {
			actions.put(action, 0);
		}
		return actions;
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
}
