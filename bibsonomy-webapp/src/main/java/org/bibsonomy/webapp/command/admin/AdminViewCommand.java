/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.admin;

import java.util.LinkedList;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.TabsCommand;

/**
 * Command bean for admin page 
 * 
 * @author Stefan Stützer
 */
public class AdminViewCommand extends TabsCommand<User> {

	/*
	 * Titles of the tabs - order must match the following public 
	 * integer definitions!
	 */
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
	/*
	 * If you change order here, change it also in the above array!
	 */
	public final static int MOST_RECENT = 0;
	public final static int ADMIN_SPAMMER_INDEX = 1;
	public final static int ADMIN_UNSURE_INDEX = 2;
	public final static int ADMIN_NOSPAMMER_INDEX = 3;
	public final static int CLASSIFIER_SPAMMER_INDEX = 4;
	public final static int CLASSIFIER_SPAMMER_UNSURE_INDEX = 5;
	public final static int CLASSIFIER_NOSPAMMER_UNSURE_INDEX = 6;
	public final static int CLASSIFIER_NOSPAMMER_INDEX	= 7;
	public final static int CLASSIFIER_EVALUATE = 8;

	/** Command containing current admin settings */
	private AdminSettingsCommand settingsCommand = new AdminSettingsCommand();

	private AdminStatisticsCommand statisticsCommand = new AdminStatisticsCommand();

	/** the time interval (in hours) for retrieving spammers */
	//TODO: variable time intervals
	private Integer[] interval = new Integer[] {12, 24, 168};

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
		setSelTab(CLASSIFIER_SPAMMER_UNSURE_INDEX);

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

	public void setInterval(final Integer[] interval) {
		this.interval = interval;
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