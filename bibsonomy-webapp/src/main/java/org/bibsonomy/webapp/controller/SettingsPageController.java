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
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.beans.factory.annotation.Required;
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

	private CVWikiModel wikiRenderer;

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
		case 5: // cv tab
			workOnCVTab(command);
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



	private void workOnCVTab(final SettingsViewCommand command) {
		log.debug("cvPageController accessed.");

		try {
			final String requestedUser = command.getContext().getLoginUser().getName();
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser);
			/* Check if the group is present. If it should be a user. If its no
			   user the we will catch the exception and return an error message
			   to the user. */
			if (present(requestedGroup)) {
				handleGroupCV(this.logic.getGroupDetails(requestedUser), command);
			} else {
				handleUserCV(this.logic.getUserDetails(requestedUser), command);
			}
		} catch (RuntimeException e) {
			//If the name does not fit to anything a runtime exception is thrown while attempting to get the requestedUser
			throw new MalformedURLSchemeException("Something went wrong! You are most likely looking for a non existant user/group.");
		} catch (Exception e) {
			throw new MalformedURLSchemeException("Something went wrong while working on your request. Please try again.");
		}
	}


	 /** Handles the group cv page request
	 * 
	 * @param reqGroup
	 * @param command
	 * @return The group-cv-page view
	 */
	private void handleGroupCV(final Group requestedGroup, final SettingsViewCommand command) {
		final String groupName = requestedGroup.getName();
		command.setIsGroup(true);

		final List<User> groupUsers = this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, 1000);
		requestedGroup.setUsers(groupUsers);

		//this.setTags(command, Resource.class, GroupingEntity.GROUP, requestedGroup.getName(), null,  null, null, 1000, null);

		final Wiki wiki = this.logic.getWiki(groupName, null);
		final String wikiText;

		if (present(wiki)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}

		/*
		 * set the group to render
		 */
		this.wikiRenderer.setRequestedGroup(requestedGroup); //FIXME: not thread-safe!
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);

		//return Views.WIKICVPAGE;
	}

	/**
	 * Handles the user cv page request
	 * 
	 * @param reqUser
	 * @param command
	 * @return The user-cv-page view
	 */
	private void handleUserCV(final User requestedUser, final SettingsViewCommand command) {
		command.setUser(requestedUser);
		final String userName = requestedUser.getName();
		//this.setTags(command, Resource.class, GroupingEntity.USER, userName, null, new LinkedList<String>(), null, 1000, null);

		/*
		 * convert the wiki syntax
		 */
		final Wiki wiki = this.logic.getWiki(userName, null);
		final String wikiText;

		if (present(wiki) && (requestedUser.equals(command.getContext().getLoginUser())
				|| !requestedUser.isSpammer() && requestedUser.getToClassify() != null && requestedUser.getToClassify() != 1)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}

		/*
		 * set the user to render
		 */
		this.wikiRenderer.setRequestedUser(requestedUser); // FME: not thread-safe!
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);

		//return Views.WIKICVPAGE;

	}
	
	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	@Required
	public void setWikiRenderer(CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

}
