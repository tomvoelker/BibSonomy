package org.bibsonomy.webapp.controller.actions;

import java.net.URI;
import java.util.LinkedList;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationDirection;
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
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setSyncServer(new LinkedList<SyncService>());
		return command;
	}

	/**
	 * FIXME remove casts to {@link SyncLogicInterface} after integration of 
	 * {@link SyncLogicInterface} into {@link LogicInterface}
	 * 
	 * @see org.bibsonomy.webapp.controller.SettingsPageController#workOn(org.bibsonomy.webapp.command.SettingsViewCommand)
	 */
	@Override
	public View workOn(final SettingsViewCommand command) {
		
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

		if (errors.hasErrors()) {
			return super.workOn(command);
		}

		
		final String loginUserName = loginUser.getName();
		final SyncService syncService = command.getSyncServer().get(0);
		final URI service = syncService.getService();
		final SynchronizationDirection direction = syncService.getDirection();
		final Class<? extends Resource> resourceType = syncService.getResourceType();
		final HttpMethod httpMethod = this.requestLogic.getHttpMethod();
		
		switch (httpMethod) {
		case POST:
			((SyncLogicInterface) logic).createSyncServer(loginUserName, service, resourceType, syncService.getServerUser(), direction);
			break;
		case PUT:
			((SyncLogicInterface) logic).updateSyncServer(loginUserName, service, resourceType, syncService.getServerUser(), direction);
			break;
		case DELETE:
			((SyncLogicInterface) logic).deleteSyncServer(loginUserName, service);
			break;
		default:
			errors.reject("error.general");
			break;
		}

		return super.workOn(command);
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
