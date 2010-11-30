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

	/**
	 * hold current errors
	 */
	private Errors errors;
	private LogicInterface logic;

	@Override
	public SettingsViewCommand instantiateCommand() {
		return new SettingsViewCommand();
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
		
		// used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));
		
		// check whether the user is a group		
		if (UserUtils.userIsGroup(loginUser))  {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		} else {
			// if he is not, he will be forwarded to the first settings tab
			command.showGroupTab(false);
			command.setSelTab(SettingsViewCommand.MY_PROFILE_IDX);
			this.errors.reject("settings.group.error.groupDoesNotExist");
			return Views.SETTINGSPAGE;
		}
			
		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		log.debug("User is logged in, ckey is valid");
		// the group properties to update
		final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
		final boolean sharedDocs = command.getSharedDocuments() == 1;
		
		// the group to update
		final Group groupToUpdate = this.logic.getGroupDetails(loginUser.getName());
		
		if (!present(groupToUpdate)) {
			// TODO: are these statements unreachable? @see if (UserUtils.userIsGroup())
			command.showGroupTab(false);
			command.setSelTab(SettingsViewCommand.MY_PROFILE_IDX);
			this.errors.reject("settings.group.error.groupDoesNotExist");
			return Views.SETTINGSPAGE;
		}
		
		// update the bean
		groupToUpdate.setPrivlevel(priv);
		groupToUpdate.setSharedDocuments(sharedDocs);
		
		try {
			this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_SETTINGS);
		} catch (final Exception ex) {
			// TODO: what exceptions can be thrown?!
		}
		return Views.SETTINGSPAGE;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

}
