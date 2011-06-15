package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.webapp.command.actions.SyncSettingsCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsController implements MinimalisticController<SyncSettingsCommand>, ErrorAware{
	
	private Errors errors;
	
	private LogicInterface logic;
	private SyncLogicInterface syncLogic;
	
	@Override
	public SyncSettingsCommand instantiateCommand() {
		return new SyncSettingsCommand();
	}

	@Override
	public View workOn(SyncSettingsCommand command) {
		
		if (!command.getContext().isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
		}
		
		if(command.getContext().getLoginUser().isSpammer()){
			//FIXME correct error code
			this.errors.reject("error.spammer");
		}
		
		URI service = command.getService();
		if(!present(service)) {
			String serviceName = command.getServiceName();
			if (present(serviceName)) {
				try {
					service = new URI(serviceName);
				} catch (URISyntaxException ex) {
					this.errors.reject("error.field.serviceName");
				}
			} else {
				this.errors.reject("error.field.valid.service");
			}
		}
		
		if(!present(command.getServerUserName())) {
			this.errors.reject("error.field.valid.serveruserName");
		}
		
		if(!present(command.getApiKey())) {
			this.errors.reject("error.field.valid.apiKey");
		}
		
		//TODO remove this check after integration
		if (!present(syncLogic)){
			this.errors.reject("error.general");
		}
		
		String action = command.getAction();
		Properties userCredentials = readUserCredentials(command);
		String userName = command.getContext().getLoginUser().getName();
		
		if("create".equals(action)) {
			syncLogic.createSyncServer(userName, service, userCredentials);
		} else if("delete".equals(action)) {
			syncLogic.deleteSyncServer(userName, service);
		} else if("update".equals(action)) {
			syncLogic.updateSyncServer(userName, service, userCredentials);
		} else {
			errors.reject("error.general");
		}
		
		if (errors.hasErrors()) {
			return Views.ERROR;
		}
		
		return new ExtendedRedirectView("/settings?selTab=4");
	}
	
	private Properties readUserCredentials(SyncSettingsCommand command) {
		Properties userCredentials = new Properties();
		userCredentials.put("userName", command.getServerUserName());
		userCredentials.put("apiKey", command.getApiKey());
		return userCredentials;
	}


	/**
	 * @param errors the error to set
	 */
	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @return the error
	 */
	@Override
	public Errors getErrors() {
		return errors;
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
	
	
}
