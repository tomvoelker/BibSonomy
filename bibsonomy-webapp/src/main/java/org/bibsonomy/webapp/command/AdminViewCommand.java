package org.bibsonomy.webapp.command;

import java.util.LinkedList;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.model.User;

/**
 * Command bean for admin page 
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminViewCommand extends TabsCommand<User> {

	private static final String[] tabTitles = {
		"navi.newregistrations",
		"navi.admin_spammer",
		"navi.admin_unsure",
		"navi.admin_nospammer",
		"navi.classifier_spammer",
		"navi.classifier_spammer_unsure",
		"navi.classifier_nospammer_unsure",
		"navi.classifier_nospammer",
		"navi.classifier_evaluate"
	};

	/** Command containing current admin settings */
	private AdminSettingsCommand settingsCommand = new AdminSettingsCommand();

	private AdminStatisticsCommand statisticsCommand = new AdminStatisticsCommand();

	/** the time interval (in hours) for retrieving spammers */
	//TODO: variable time intervals
	private final Integer[] interval = new Integer[] {12, 24, 168};

	/** number of entries shown on one page */
	private Integer limit = 100;

	/** information about a specific user */
	private String aclUserInfo; 

	/** specific action for admin page */
	private String action; 

	/** specific user to show */
	private User user;

	/** specific user information */
	private final LinkedList<String> infos;



	public AdminViewCommand() {	
		addTabs(tabTitles);

		// change default tab to classifier tab
		selTab = 6;

		// initialise info list
		infos = new LinkedList<String>();
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

	public Integer[] getInterval() {
		return this.interval;
	}

	public void setInterval(int index, Integer interval) {
		this.interval[index] = interval;
	}

	public String getAclUserInfo() {

		return this.aclUserInfo;
	}

	public void setAclUserInfo(String aclUserInfo) {
		this.aclUserInfo = aclUserInfo;
	}

	public Integer getLimit() {
		return this.limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public LinkedList<String> getInfos() {
		return this.infos;
	}

	public void addInfo(String info) {
		this.infos.add(info);
	}

}