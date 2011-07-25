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
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.SyncSettingsValidator;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsController extends SettingsPageController implements MinimalisticController<SettingsViewCommand>, ValidationAwareController<SettingsViewCommand> {
	
	private SyncLogicInterface syncLogic;
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand syncSettingsCommand = new SettingsViewCommand();
		final SyncService syncService = new SyncService();
		syncService.setServerUser(new Properties());
		syncSettingsCommand.setSyncService(syncService);
		return syncSettingsCommand;
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		
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
			return super.workOn(command);
		}

		
		final String loginUserName = loginUser.getName();
		final SyncService syncService = command.getSyncService();
		final URI serviceUrl = syncService.getService();

		final HttpMethod httpMethod = this.requestLogic.getHttpMethod();
		
		
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

		return super.workOn(command);
	}


	/**
	 * FIXME remove method after integration of {@link SyncLogicInterface} into {@link LogicInterface}
	 * 
	 * @param logic the logic to set
	 */
	@Override
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
		if (logic instanceof SyncLogicInterface) {
			syncLogic = (SyncLogicInterface) logic;
		}
	}


	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return new SyncSettingsValidator();
	}

	@Override
	public boolean isValidationRequired(SettingsViewCommand command) {
		return true;
	}
	
}
