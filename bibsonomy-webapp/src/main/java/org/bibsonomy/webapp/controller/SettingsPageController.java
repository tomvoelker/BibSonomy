package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Steffen
 * @version $Id$
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware, RequestAware {
	private static final Log log = LogFactory.getLog(SettingsPageController.class);
	
	/**
	 * hold current errors
	 */
	protected Errors errors = null;

	protected LogicInterface logic;
	protected RequestLogic requestLogic;

	/**
	 * @param command
	 * @return the view
	 */
	@Override
	public View workOn(final SettingsViewCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}
		/*
		 * the user can only change his/her own settings, thus we take the 
		 * loginUser 
		 */
		final User loginUser = command.getContext().getLoginUser();
		command.setUser(loginUser);

		// used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));

		// check whether the user is a group		
		if (UserUtils.userIsGroup(loginUser)) {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		}
		
		/*
		 * get friends for sidebar
		 */
		final String loggedInUserName = command.getUser().getName();
		command.setUserFriends(logic.getUserRelationship(loggedInUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag));
		command.setFriendsOfUser(logic.getUserRelationship(loggedInUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag));

		// show sync tab only for non-spammers
		command.showSyncTab(!loginUser.isSpammer());

		switch (command.getSelTab()) {
		case 0:	// profile tab
			break;
		case 1:	// setting tab
			break;
		case 2:	// import tab
			checkInstalledJabrefLayout(command);
			break;
		case 3:	// group tab
			workOnGroupTab(command);
			break;
		case 4:	// sync tab
			workOnSyncSettingsTab(command);
			break;
		default:
			errors.reject("error.settings.tab");
			break;
		}

		return Views.SETTINGSPAGE;
	}


	/**
	 * checks whether the user has already uploaded jabref layout definitions
	 * 
	 * @param command
	 */
	private void checkInstalledJabrefLayout(final SettingsViewCommand command) {
		final String loggedInUserName = command.getContext().getLoginUser().getName();
		/* 
		 * set jabref layouts of the users
		 * TODO: better solution?
		 */
		for (final LayoutPart layoutpart : LayoutPart.values()) {
			final String fileHash = JabrefLayoutUtils.userLayoutHash(loggedInUserName, layoutpart);
			/*
			 * check whether the user has the jabref layout (begin, end or item)
			 */
			final Document document = this.logic.getDocument(loggedInUserName, fileHash);
			/*
			 * if a document was found
			 * set the corresponding hash and name of the file
			 */
			if (present(document)) {
				switch (layoutpart) {
				case BEGIN:
					command.setBeginHash(fileHash);
					command.setBeginName(document.getFileName());
					break;
				case END:
					command.setEndHash(fileHash);
					command.setEndName(document.getFileName());
					break;
				case ITEM:
					command.setItemHash(fileHash);
					command.setItemName(document.getFileName());
					break;
				default:
					log.warn("can't handle layoutpart " + layoutpart);
					break;
				}
			}
		}
	}

	private void workOnGroupTab(final SettingsViewCommand command) {
		final String groupName = command.getContext().getLoginUser().getName();
		// the group to update
		final Group group = logic.getGroupDetails(groupName);
		if (present(group)) {
			command.setGroup(group);
			/*
			 * get group users
			 */
			group.setUsers(this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, Integer.MAX_VALUE));
			/*
			 * FIXME: use the group in the command instead of 
			 * this hand-written conversion
			 */
			command.setPrivlevel(group.getPrivlevel().ordinal());
			
			/* 
			 * TODO: use share docs directly
			 */
			int sharedDocsAsInt =  0;
			if (group.isSharedDocuments()) {
				sharedDocsAsInt = 1;
			}
			command.setSharedDocuments(sharedDocsAsInt);
		}
	}

	/**
	 * handles synchronization tab
	 * @param command
	 */
	private void workOnSyncSettingsTab(final SettingsViewCommand command) {
		final List<SyncService> userServers = logic.getSyncService(command.getUser().getName(), null, true);
		final List<URI> allServers = logic.getSyncServices(true);

		/*
		 * Remove all servers the user already has configured.
		 */
		for (final SyncService service : userServers) {
			final URI serviceUri = service.getService();
			if (allServers.contains(serviceUri)) { // FIXME: not efficient
				allServers.remove(serviceUri);
			}
		}
		command.setAvailableSyncServers(allServers);
		command.setSyncServer(userServers);
		command.setAvailableSyncClients(logic.getSyncServices(false));
	}

	/**
	 * @return the current command
	 */
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		final User user = new User();
		command.setUser(user);
		user.setSettings(new UserSettings());
		return command;
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
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

}
