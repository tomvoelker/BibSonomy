package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.sync.SynchronizationClient;
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

	private Errors errors;
	private SynchronizationClient client;

	@Override
	public AjaxSynchronizationCommand instantiateCommand() {
		AjaxSynchronizationCommand command =  new AjaxSynchronizationCommand();
		return command;
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


		/*
		 * create server URI from service name
		 */
		final URI serviceName = command.getServiceName();
		if (!present(serviceName)) {
			errors.rejectValue("serviceName", "error.field.required");
		}

		if (errors.hasErrors()) {
			return Views.AJAX_ERRORS;
		}

		final JSONObject json = getJson(client.synchronize(logic, serviceName));

		command.setResponseString(json.toString());

		return Views.AJAX_JSON;
	}

	private JSONObject getJson(final Map<Class<? extends Resource>, SynchronizationData> data) {
		final JSONObject json = new JSONObject();

		for (final Entry<Class<? extends Resource>, SynchronizationData> entry : data.entrySet()) {
			final SynchronizationData value = entry.getValue();
			final HashMap<String, Object> values = new HashMap<String, Object>();
			if (SynchronizationStatus.RUNNING.equals(value.getStatus())) {
				// TODO i18N
				values.put("error", "old synchronization still running, please try later");
			} else {
				values.put("error", "no");
				values.put("date", value.getLastSyncDate().getTime());
				values.put("status", value.getStatus());
				values.put("info", value.getInfo());
			}
			json.put(entry.getKey().getSimpleName(), values);
		}
		return json;
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
	public void setClient(SynchronizationClient client) {
		this.client = client;
	}

	/**
	 * @return the client
	 */
	public SynchronizationClient getClient() {
		return client;
	}

}
