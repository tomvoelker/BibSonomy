package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import beans.GroupSettingsBean;
import beans.SettingsBean;

/**
 * @author Steffen
 * @version $Id: SettingsPageController.java,v 1.2 2009-05-20 12:03:21
 *          voigtmannc Exp $
 */
public class SettingsPageController implements
		MinimalisticController<SettingsViewCommand>, ErrorAware {

	private static final Log log = LogFactory
			.getLog(SearchPageController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	private LogicInterface logic;

	/**
	 * @param command
	 * @return the view
	 */
	public View workOn(SettingsViewCommand command) {

		command.setPageTitle("settings");
		
		if(!command.getContext().isUserLoggedIn()) {
			return Views.LOGIN;
		}
		
		switch (command.getSelTab()) {
		case 0: {
			// called by the my profile tab
			workOnMyProfileTab(command);
			break;
		}
		case 1: {
			// called by the setting tab
			workOnSettingsTab(command);
			break;
		}
		case 2: {

			checkInstalledJabrefLayout(command);
			break;
		}
		default: {
			errors.reject("error.settings.tab");
			break;
		}
		}

		if (errors.hasErrors()) {

			if (errors.hasFieldErrors("error.general.login")) {
				return Views.SETTINGSPAGE;
			}

			return Views.ERROR;
		}

		return Views.SETTINGSPAGE;
	}

	private void workOnMyProfileTab(SettingsViewCommand command) {
//		<%-- ------------------------ change settings -------------------------- --%>
//		<jsp:useBean id="settingsBean" class="beans.SettingsBean" scope="request">
//		  <jsp:setProperty name="settingsBean" property="*"/>
//		  <jsp:setProperty name="settingsBean" property="name" value="${user.name}"/>
//		  <jsp:setProperty name="settingsBean" property="validCkey" value="${validckey}"/>
//		</jsp:useBean>
//		
//		<% settingsBean.queryDB(); %> <%-- write data to database (if neccessary) --%>	
		
		User loginUser = command.getContext().getLoginUser();
		command.setFriendsOfUser(logic.getFriendsOfUser(loginUser));
		command.setUserFriends(logic.getUserFriends(loginUser));	
		/*SettingsBean settingsBean = new SettingsBean();
		// TODO which properties are set via property="*"?
		settingsBean.setName(loginUser.getName());
		settingsBean.setValidCkey(command.getContext().isValidCkey());
		settingsBean.queryDB();*/
	}

	/**
	 * checks whether the user has already uploaded jabref layout definitions
	 * 
	 * @param command
	 */
	private void checkInstalledJabrefLayout(SettingsViewCommand command) {

		LayoutPart[] values = LayoutPart.values();

		for (LayoutPart layoutpart : values) {

			String fileHash = JabrefLayoutUtils.userLayoutHash(command
					.getContext().getLoginUser().getName(), layoutpart);

			Document document = this.logic.getDocument(command.getContext()
					.getLoginUser().getName(), fileHash);

			if (document != null) {
				if ("begin".equals(layoutpart.getName())) {
					command.setBeginHash(fileHash);
					command.setBeginName(document.getFileName());
				} else if ("end".equals(layoutpart.getName())) {
					command.setEndHash(fileHash);
					command.setEndName(document.getFileName());
				} else if ("item".equals(layoutpart.getName())) {
					command.setItemHash(fileHash);
					command.setItemName(document.getFileName());
				}
			}
		}
	}

	private void workOnSettingsTab(SettingsViewCommand command) {
		User loginUser = command.getContext().getLoginUser();
		command.setUser(loginUser);
		// FIXME check other tabs if they need those info(queries) too, then remove them from settings.jspx.
//		Group group = logic.getGroupDetails(loginUser.getName());
		//List<User> users = logic.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end);
		//logic.updateUser(user);
		
//		command.getUser().getSettings().setTagboxStyle(loginUser
//				.getSettings().getTagboxStyle());
//		command.getUser().getSettings().setTagboxSort(loginUser.getSettings()
//				.getTagboxSort());
//		command.getUser().getSettings().setTagboxTooltip(loginUser
//				.getSettings().getTagboxTooltip());
//		command.getUser().getSettings().setDefaultLanguage(loginUser
//				.getSettings().getDefaultLanguage());
//		command.getUser().getSettings().setTagboxMinfreq(loginUser
//				.getSettings().getTagboxMinfreq());
//		command.getUser().getSettings().setListItemcount(loginUser.getSettings()
//				.getListItemcount());
//		command.getUser().getSettings().setLogLevel(loginUser.getSettings().getLogLevel());
//		command.setConfirmDelete(loginUser.getSettings().getConfirmDelete());
//		command.setHasOwnGroup(group != null);
		// FIXME necessary?
		
//		if(command.getHasOwnGroup()) {
//			GroupSettingsBean b = new GroupSettingsBean();
//			b.setUsername(loginUser.getName());
//			b.queryDB();
//		}
	}

	/**
	 * @return the current command
	 */
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setGroup(GroupUtils.getPublicGroup().getName());
		return command;
	}

	@Override
	public Errors getErrors() {

		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {

		this.errors = errors;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
