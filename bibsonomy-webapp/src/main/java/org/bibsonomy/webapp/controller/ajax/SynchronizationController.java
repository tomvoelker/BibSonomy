package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.shiro.authz.UnauthorizedException;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
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
		/*
		 * only admins can synchronize!
		 */
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		if (!present(loginUser) || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new UnauthorizedException();
		}

		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		
		/*
		 * create server URI from service name
		 */
		final String serviceName = command.getServiceName();
		URI uri = null;
		if (present(serviceName)) {
			try {
				uri = new URI(serviceName);
			} catch (URISyntaxException ex) {
				// FIXME: add error
				throw new IllegalStateException();
			}
		} else {
			errors.rejectValue("serviceName", "error.field.required");
		}
		
		if (errors.hasErrors()) {
			return Views.AJAX_ERRORS;
		}
		
		final JSONObject json = new JSONObject();
		
		
		switch(command.getContentType()) { // FIXME: use strings "bookmark" and "publication", not numbers
		case 1:
			addData(json, Bookmark.class, client.synchronize(logic, uri, Bookmark.class));
			break;
		case 2:
			addData(json, BibTex.class, client.synchronize(logic, uri, BibTex.class));
			break;
		case 3:
			addData(json, Bookmark.class, client.synchronize(logic, uri, Bookmark.class));
			addData(json, BibTex.class, client.synchronize(logic, uri, BibTex.class));
			break;
		default:
//			throw new UnsupportedContentTypeException();
			break;
		}

		command.setResponseString(json.toString());
		return Views.AJAX_JSON;
	}
	
	private void addData (final JSONObject json, Class<? extends Resource> resourceType, final SynchronizationData data) {
		final HashMap<String, Object> values = new HashMap<String, Object>();
		if("running".equals(data.getStatus())) {
			//TODO i18N
			values.put("error", "old synchronization still running, please try later");
		} else {
			values.put("error", "no");
			values.put("date", data.getLastSyncDate().getTime());
			values.put("result", data.getStatus());
		}
		json.put(resourceType.getSimpleName(), values);
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
