package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;
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
public class GroupSettingsController implements MinimalisticController<SettingsViewCommand>, ErrorAware{

	private static final String TAB_URL = "/settings";
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	/**
	 * hold current errors
	 */
	private Errors errors;

	private RequestLogic requestLogic;
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	private LogicInterface logic;
	

	@Override
	public SettingsViewCommand instantiateCommand() {
		SettingsViewCommand command = new SettingsViewCommand();
		command.setTabURL(TAB_URL);
		return command;
	}

	@Override
	public View workOn(SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();

		//user specific logic
		if (!command.getContext().isUserLoggedIn()) 
		{
			return new ExtendedRedirectView("/login");
		}

		command.setPageTitle("settings");
		
		final User loginUser = command.getContext().getLoginUser();
		command.setUser(loginUser);
		
		//used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? 
				loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));
		
		
		//check whether the user is a group		
		if (UserUtils.userIsGroup(loginUser)) 
		{
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		} 
		//if he is not, he will be forwarded to the first settings tab
		else
		{
			command.setSelTab(command.MY_PROFILE_IDX);
			return Views.SETTINGSPAGE;
		}
			
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors())
		{
			log.debug("User is logged in, ckey is valid");
		}	
		
		//the group properties to update
		Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
		boolean sharedDocs = command.getSharedDocuments()==1;
		
		//the group to update
		Group groupToModify = logic.getGroupDetails(loginUser.getName());
		
		if(!ValidationUtils.present(groupToModify))
		{
			errors.reject("settings.group.error.groupDoesNotExist");
		}
		
		//update the bean
		groupToModify.setPrivlevel(priv);
		groupToModify.setSharedDocuments(sharedDocs);
		
		try {
			logic.updateGroup(groupToModify, GroupUpdateOperation.UPDATE_SETTINGS);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return Views.SETTINGSPAGE;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	

	public LogicInterface getLogic() {
		return this.logic;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

}
