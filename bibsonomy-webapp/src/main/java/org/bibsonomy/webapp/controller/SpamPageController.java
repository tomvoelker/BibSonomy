package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminStatisticsCommand;
import org.bibsonomy.webapp.command.admin.AdminViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for admin page
 * 
 * @author Stefan Stützer
 * @author Beate Krause
 * @version $Id$
 **/

public class SpamPageController implements MinimalisticController<AdminViewCommand> {

	private static final Log log = LogFactory.getLog(SpamPageController.class);

	private LogicInterface logic;
	
	private UserSettings userSettings;

	public View workOn(AdminViewCommand command) {
		log.debug(this.getClass().getSimpleName());

		final User loginUser = command.getContext().getLoginUser();
		if (loginUser.getRole().equals(Role.DEFAULT)) {
			/** TODO: redirect to login page as soon as it is available */
		}

		command.setPageTitle("admin");
		this.setUsers(command);
		
		/*
		 * only compute counts for specific tabs
		 */
		if (command.getSelTab() == 5 || command.getSelTab() == 8){
			this.setStatistics(command);
		}

		for (ClassifierSettings s : ClassifierSettings.values()) {
			command.setClassifierSetting(s, this.logic.getClassifierSettings(s));
		}

		/*
		 * handle specific user
		 */
		if (command.getAclUserInfo() != null) {
			if ("flag_spammer".equals(command.getAction())) {
				if (!logic.getUserDetails(command.getAclUserInfo()).getSpammer()){
					User user = new User(command.getAclUserInfo());
					user.setToClassify(0);
					user.setAlgorithm("admin");
					user.setSpammer(true);
					this.logic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
				}else{
					command.addInfo("The user was already flagged as a spammer.");
				}
			}

			command.setUser(logic.getUserDetails(command.getAclUserInfo()));
					}

		return Views.ADMIN_SPAM;

	}

	public AdminViewCommand instantiateCommand() {
		return new AdminViewCommand();
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setStatistics(AdminViewCommand cmd) {
		AdminStatisticsCommand command = cmd.getStatisticsCommand();

		for (int interval : cmd.getInterval()) {
			command.setNumAdminSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.SPAMMER, interval));
			command.setNumAdminNoSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.NO_SPAMMER, interval));
			command.setNumClassifierSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER, interval));
			command.setNumClassifierSpammerUnsure(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER_NOT_SURE, interval));
			command.setNumClassifierNoSpammerUnsure(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER_NOT_SURE, interval));
			command.setNumClassifierNoSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER, interval));
		}
	}

	public void setUsers(AdminViewCommand cmd) {
		Classifier classifier = null;
		SpamStatus status = null;

		if (cmd.getSelTab() == AdminViewCommand.CLASSIFIER_EVALUATE) {

			// TODO: Interval checken
			List<User> u = this.logic.getClassifierComparison(cmd.getInterval()[0]);
			cmd.setContent(u);
			return;
		}

		/* set content in dependence of the selected tab */
		switch (cmd.getSelTab()) {
		case AdminViewCommand.MOST_RECENT:
			classifier = Classifier.BOTH;
			break;
		case AdminViewCommand.ADMIN_SPAMMER_INDEX:
			classifier = Classifier.ADMIN;
			status = SpamStatus.SPAMMER;
			break;
		case AdminViewCommand.ADMIN_UNSURE_INDEX:
			classifier = Classifier.ADMIN;
			status = SpamStatus.UNKNOWN;
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
		cmd.setContent(this.logic.getClassifiedUsers(classifier, status, cmd.getLimit()));
	}
	
	public UserSettings getUserSettings() {
		return this.userSettings;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

}