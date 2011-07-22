package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.actions.SyncSettingsCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
	
	private LogicInterface logic;
	private SyncLogicInterface syncLogic;
	
	@Override
	public SyncSettingsCommand instantiateCommand() {
		return new SyncSettingsCommand();
	}

	@Override
	public View workOn(SyncSettingsCommand command) {
		
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

		
		final String action = command.getAction();
		
		final String loginUserName = loginUser.getName();
		final SyncService syncService = command.getSyncService();
		final URI serviceUrl = syncService.getService();
		
		// FIXME: use _method param supported by Spring 
		if ("create".equals(action)) {
			syncLogic.createSyncServer(loginUserName, serviceUrl, syncService.getServerUser());
		} else if("delete".equals(action)) {
			syncLogic.deleteSyncServer(loginUserName, serviceUrl);
		} else if("update".equals(action)) {
			syncLogic.updateSyncServer(loginUserName, serviceUrl, syncService.getServerUser());
		} else {
			errors.reject("error.general");
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

	@Override
	public Validator<SyncSettingsCommand> getValidator() {
		return new SyncSettingsValidator();
	}

	@Override
	public boolean isValidationRequired(SyncSettingsCommand command) {
		return true;
	}
	
	
}
