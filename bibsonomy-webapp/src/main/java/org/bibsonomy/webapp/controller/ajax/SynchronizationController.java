package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.shiro.authz.UnauthorizedException;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.sync.SynchronizationClient;
import org.bibsonomy.webapp.command.ajax.AjaxSynchronizationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationController extends AjaxController implements MinimalisticController<AjaxSynchronizationCommand>, ErrorAware{
	
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
		User currentUser = requestLogic.getLoginUser();
		if (!present(currentUser) || !currentUser.getRole().equals(Role.ADMIN)) {
			throw new UnauthorizedException();
		}
		
		//FIXME ckey check?
		
		
		/*
		 * create server URI from service name
		 */
		String serviceName = command.getServiceName();
		URI uri = null;
		if(present(serviceName)) {
			try {
				uri = new URI(serviceName);
			} catch (URISyntaxException ex) {
				// TODO Auto-generated catch block
				throw new IllegalStateException();
			}
		} else {
			return Views.AJAX_ERRORS;
		}
		
		SyncLogicInterface syncLogic = (SyncLogicInterface) logic;

		SyncService server = syncLogic.getSyncServer(currentUser.getName(), uri);
		
		JSONObject json = new JSONObject();
		
		switch(command.getContentType()) {
		case 1:
			addData(json, Bookmark.class, client.synchronize(logic, Bookmark.class, currentUser, server));
			break;
		case 2:
			addData(json, BibTex.class, client.synchronize(logic, BibTex.class, currentUser, server));
			break;
		case 3:
			addData(json, Bookmark.class, client.synchronize(logic, Bookmark.class, currentUser, server));
			addData(json, BibTex.class, client.synchronize(logic, BibTex.class, currentUser, server));
			break;
		default:
//			throw new UnsupportedContentTypeException();
			break;
		}
		
		String tets = json.toString();
		command.setResponseString(json.toString());
		return Views.AJAX_JSON;

	}
	
	private void addData (JSONObject json, Class<? extends Resource> resourceType, SynchronizationData data) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("date", data.getLastSyncDate().getTime());
		values.put("result", data.getStatus());
		json.put(ConstantID.getContentTypeByClass(resourceType).getId(), values);
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
