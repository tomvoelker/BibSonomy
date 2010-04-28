package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SearchPageController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author ema
 * @version $Id$
 */
public class GroupSettingsController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(SearchPageController.class);
	
	private static final String TAB_URL = "/settings";

	/**
	 * hold current errors
	 */
	private Errors errors;
	private RequestLogic requestLogic;
	private LogicInterface logic;
	

	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setTabURL(TAB_URL);
		return command;
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		if (!context.isUserLoggedIn()) {
			return new ExtendedRedirectView("/login");
		}

		command.setPageTitle("settings"); // TODO: i18n
		
		final User loginUser = context.getLoginUser();
		command.setUser(loginUser);
		
		//used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? 
				loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));
		
		
		// check whether the user is a group		
		if (UserUtils.userIsGroup(loginUser))  {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		} else {
			// if he is not, he will be forwarded to the first settings tab
			command.showGroupTab(false);
			command.setSelTab(SettingsViewCommand.MY_PROFILE_IDX);
			errors.reject("settings.group.error.groupDoesNotExist");
			return Views.SETTINGSPAGE;
		}
			
		/*
		 * check the ckey
		 */
		if (context.isValidCkey()) {
			log.debug("User is logged in, ckey is valid");
		} else {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		//the group properties to update
		final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
		final boolean sharedDocs = command.getSharedDocuments() == 1;
		
		//the group to update
		final Group groupToModify = logic.getGroupDetails(loginUser.getName());
		
		if (!present(groupToModify)) {
			command.showGroupTab(false);
			command.setSelTab(SettingsViewCommand.MY_PROFILE_IDX);
			errors.reject("settings.group.error.groupDoesNotExist");
			return Views.SETTINGSPAGE;
		}
		
		//update the bean
		groupToModify.setPrivlevel(priv);
		groupToModify.setSharedDocuments(sharedDocs);
		
		try {
			logic.updateGroup(groupToModify, GroupUpdateOperation.UPDATE_SETTINGS);
		} catch (final Exception ex) {
			// TODO: what exceptions can be thrown?!
		}
		
		return Views.SETTINGSPAGE;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

}
