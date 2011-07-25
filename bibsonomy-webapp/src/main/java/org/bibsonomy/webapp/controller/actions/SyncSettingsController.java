package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Properties;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.webapp.command.actions.SyncSettingsCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.SyncSettingsValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsController implements MinimalisticController<SyncSettingsCommand>, ErrorAware, ValidationAwareController<SyncSettingsCommand>{
	
	private Errors errors;
	private SyncLogicInterface syncLogic;
	private RequestLogic requestLogic; // to access HTTP method 
	
	@Override
	public SyncSettingsCommand instantiateCommand() {
		final SyncSettingsCommand syncSettingsCommand = new SyncSettingsCommand();
		final SyncService syncService = new SyncService();
		syncService.setServerUser(new Properties());
		syncSettingsCommand.setSyncService(syncService);
		return syncSettingsCommand;
	}

	@Override
	public View workOn(final SyncSettingsCommand command) {
		
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
		}

		// TODO remove this check after integration
		if (!present(syncLogic)){
			this.errors.reject("error.general");
		}

		if (errors.hasErrors()) {
			return Views.ERROR;
		}

		
		final String loginUserName = loginUser.getName();
		final SyncService syncService = command.getSyncService();
		final URI serviceUrl = syncService.getService();

		final HttpMethod httpMethod = requestLogic.getHttpMethod();
		
		
		switch (httpMethod) {
		case POST:
			syncLogic.createSyncServer(loginUserName, serviceUrl, syncService.getServerUser());
			break;
		case PUT:
			syncLogic.updateSyncServer(loginUserName, serviceUrl, syncService.getServerUser());
			break;
		case DELETE:
			syncLogic.deleteSyncServer(loginUserName, serviceUrl);
			break;
		default:
			errors.reject("error.general");
			break;
		}
		
		if (errors.hasErrors()) {
			return Views.ERROR;
		}
		
		return new ExtendedRedirectView("/settings?selTab=4");
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
		// FIXME remove after integration
		if (logic instanceof SyncLogicInterface) {
			syncLogic = (SyncLogicInterface) logic;
		}
		
	}


	@Override
	public Validator<SyncSettingsCommand> getValidator() {
		return new SyncSettingsValidator();
	}

	@Override
	public boolean isValidationRequired(SyncSettingsCommand command) {
		return true;
	}

	/**
	 * Sets the request logic which is used to identify the used HTTP method.
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	
}
