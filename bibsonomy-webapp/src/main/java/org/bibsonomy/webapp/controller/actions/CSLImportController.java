package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.User;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.CSLImportCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLImportController extends SettingsPageController {
	
	private static final String DELETE = "delete";

	private static final String CREATE = "create";

	private FileLogic fileLogic;
	
	@Override
	public View workOn(final SettingsViewCommand command) {
		final CSLImportCommand CSLImpCom = (CSLImportCommand) command;
		final RequestWrapperContext context = CSLImpCom.getContext();
		
		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		final User loginUser = context.getLoginUser();

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.SETTINGSPAGE;
		}
		
		return Views.ERROR;
	}
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		return new CSLImportCommand();
	}
	
	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
