package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.model.User;

/**
 * Command bean for admin page 
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminViewCommand extends TabsCommand<User> {
	
	/** Indexes of definded tabs */
	public final static int ADMIN_SPAMMER_INDEX = 1;
	public final static int ADMIN_NOSPAMMER_INDEX = 2;
	public final static int CLASSIFIER_SPAMMER_INDEX = 3;
	public final static int CLASSIFIER_SPAMMER_UNSURE_INDEX = 4;
	public final static int CLASSIFIER_NOSPAMMER_UNSURE_INDEX = 5;
	public final static int CLASSIFIER_NOSPAMMER_INDEX	= 6;
	public final static int CLASSIFIER_EVALUATE = 7;
	
	
	/** Command containing current admin settings */
	private AdminSettingsCommand settingsCommand = new AdminSettingsCommand();
	
	private AdminStatisticsCommand statisticsCommand = new AdminStatisticsCommand();	
	
	/** the time interval for retrieving spammers */
	private Integer interval = 300;
	
	public AdminViewCommand() {				
		addTab(ADMIN_SPAMMER_INDEX, "Admin: Spammer");
		addTab(ADMIN_NOSPAMMER_INDEX, "Admin: No Spammer");
		addTab(CLASSIFIER_SPAMMER_INDEX, "Classifier: Spammer");
		addTab(CLASSIFIER_SPAMMER_UNSURE_INDEX, "Classifier: Spammer (U)");
		addTab(CLASSIFIER_NOSPAMMER_UNSURE_INDEX, "Classifier: No Spammer (U)");
		addTab(CLASSIFIER_NOSPAMMER_INDEX, "Classifier: No Spammer");
		addTab(CLASSIFIER_EVALUATE, "Classifier Evaluation");			
	}		
		
	public AdminSettingsCommand getSettingsCommand() {
		return this.settingsCommand;
	}

	public void setSettingsCommand(AdminSettingsCommand settingsCommand) {
		this.settingsCommand = settingsCommand;
	}	

	public AdminStatisticsCommand getStatisticsCommand() {
		return this.statisticsCommand;
	}

	public void setStatisticsCommand(AdminStatisticsCommand statisticsCommand) {
		this.statisticsCommand = statisticsCommand;
	}

	public void setClassifierSetting(final ClassifierSettings setting, final String value) {
		settingsCommand.setAdminSetting(setting, value);
	}

	public Integer getInterval() {
		return this.interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}	
}