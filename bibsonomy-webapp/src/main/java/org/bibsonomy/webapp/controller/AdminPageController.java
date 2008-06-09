package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.AdminStatisticsCommand;
import org.bibsonomy.webapp.command.AdminViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for admin page
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminPageController implements MinimalisticController<AdminViewCommand> {

	private static final Logger log = Logger.getLogger(AdminPageController.class);
	
	private LogicInterface logic;
	
	private UserSettings userSettings;
	
	public AdminPageController() {
		System.err.println("instantiate");
		// TODO Auto-generated constructor stub
	}
	
	public View workOn(AdminViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		final User loginUser = command.getContext().getLoginUser();		
		if (loginUser.getRole().equals(Role.DEFAULT)) {
			/** TODO: redirect to login page as soon as it is available */
		}		
		
		command.setPageTitle("admin");
		this.setUsers(command);
		this.setStatistics(command);
		
		for (ClassifierSettings s: ClassifierSettings.values()) {
			command.setClassifierSetting(s, this.logic.getClassifierSettings(s));
		}
	
		return Views.ADMINPAGE;				
	}

	public AdminViewCommand instantiateCommand() {
		return new AdminViewCommand();
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}	
	
	public void setStatistics(AdminViewCommand cmd) {
		AdminStatisticsCommand command = cmd.getStatisticsCommand();
		
		command.setNumAdminSpammers(this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.SPAMMER, cmd.getInterval()));
		command.setNumAdminNoSpammer(this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.NO_SPAMMER, cmd.getInterval()));
		command.setNumClassifierSpammer(this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER, cmd.getInterval()));
		command.setNumClassifierSpammerUnsure(this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER_NOT_SURE, cmd.getInterval()));
		command.setNumClassifierNoSpammer(this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER, cmd.getInterval()));
		command.setNumClassifierNoSpammerUnsure(this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER_NOT_SURE, cmd.getInterval()));				
	}
	
	public void setUsers(AdminViewCommand cmd) {
		Classifier classifier = null;
		SpamStatus status 	  = null;
		
		if (cmd.getSelTab() == AdminViewCommand.CLASSIFIER_EVALUATE) {
			List<User> u = this.logic.getClassifierComparison(cmd.getInterval());
			cmd.setContent(u);
			System.out.println(u.get(0).getName());
			return;
		}		
			
		/* set content in dependence of the selected tab */
		switch(cmd.getSelTab()) {
		case AdminViewCommand.ADMIN_SPAMMER_INDEX:
			classifier = Classifier.ADMIN;
			status = SpamStatus.SPAMMER;
			break;
		case AdminViewCommand.ADMIN_NOSPAMMER_INDEX:
			classifier = Classifier.ADMIN;
			status = SpamStatus.NO_SPAMMER;
			break;
		case AdminViewCommand.CLASSIFIER_SPAMMER_INDEX:
			classifier = Classifier.CLASSIFIER;
			status = SpamStatus.SPAMMER;
			break;
		case AdminViewCommand.CLASSIFIER_SPAMMER_UNSURE_INDEX:
			classifier = Classifier.CLASSIFIER;
			status = SpamStatus.SPAMMER_NOT_SURE;
			break;
		case AdminViewCommand.CLASSIFIER_NOSPAMMER_INDEX:
			classifier = Classifier.CLASSIFIER;
			status = SpamStatus.NO_SPAMMER;
			break;
		case AdminViewCommand.CLASSIFIER_NOSPAMMER_UNSURE_INDEX:
			classifier = Classifier.CLASSIFIER;
			status = SpamStatus.NO_SPAMMER_NOT_SURE;
			break;
		}			
		cmd.setContent(this.logic.getClassifiedUsers(classifier, status, cmd.getInterval()));		
	}	
}