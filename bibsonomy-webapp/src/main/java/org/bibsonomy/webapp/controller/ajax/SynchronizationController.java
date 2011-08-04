package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.webapp.util.sync.SyncUtils.getPlanSummary;

import java.net.URI;
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
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.sync.TwoStepSynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.sync.SyncUtils;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import com.ibm.icu.text.DateFormat;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationController extends AjaxController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware {
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
			 * delete synchronization data
			 */
			if (!present(command.getSyncDate())) {
				/*
				 * if no sync date given -> reset server(delete all syncData for all resourceTypes)
				 */
				client.resetServer(logic, serviceName);
				JSONObject second = new JSONObject();
				second.put(BibTex.class.getSimpleName(), messageSource.getMessage("synchronization.noresult", null, requestLogic.getLocale()));
				second.put(Bookmark.class.getSimpleName(), messageSource.getMessage("synchronization.noresult", null, requestLogic.getLocale()));
				json.put("syncData", second);
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
	private Map<Class<? extends Resource>, List<SynchronizationPost>> getSyncPlan(final URI serviceName) {
		 return SyncUtils.getSyncPlan(serviceName, requestLogic);
	}
	
	/**
	 * Stores the sync plan in the session.
	 * 
	 * @param serviceName
	 * @param syncPlan
	 */
	private void setSyncPlan(final URI serviceName, final Map<Class<? extends Resource>, List<SynchronizationPost>> syncPlan) {
		requestLogic.setSessionAttribute(SyncUtils.SESSION_KEY + serviceName, syncPlan);
	}
	
	private JSONObject serializeSyncData(final Map<Class<? extends Resource>, SynchronizationData> data) {
		final JSONObject json = new JSONObject();
		final Locale locale = requestLogic.getLocale();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

		for (final Entry<Class<? extends Resource>, SynchronizationData> entry : data.entrySet()) {
			StringBuilder dataString = new StringBuilder();
			SynchronizationData value = entry.getValue();
			dataString.append(df.format(value.getLastSyncDate()) + " ");
			dataString.append(messageSource.getMessage("synchronization.result", null, locale));
			dataString.append(" " + messageSource.getMessage("synchronization.result." + value.getStatus().toString().toLowerCase(), null, locale) + " <em>(" + value.getInfo() + ")</em>");
			json.put(entry.getKey().getSimpleName(), dataString.toString());
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
		
		Map<Class<? extends Resource>, Map<String, String>> planSummary = getPlanSummary(syncPlan, serverName.toString(), locale, messageSource, projectHome);
		
		for (final Entry<Class<? extends Resource>, Map<String, String>> planEntry : planSummary.entrySet()) {
			json.put(planEntry.getKey().getSimpleName(), planEntry.getValue());
		}
		return json;
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
