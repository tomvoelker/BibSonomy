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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.opensocial.oauth.database.OAuthLogic;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;
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
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware, RequestAware {
	private static final Log log = LogFactory.getLog(SettingsPageController.class);

	/** hold current errors */
	protected Errors errors = null;

	protected OAuthLogic oauthLogic;
	protected LogicInterface logic;
	protected RequestLogic requestLogic;
	/**
	 * The List is used in a hack to protect certain oAuth Tokens from
	 * deletions. Particulary, the oAuth-Tokens in PUMA are created
	 * automatically to guarantee access from VuFind. The ConsumerKey of those
	 * properties that are protected are configured in the project.properties.
	 */
	protected List<String> invisibleOAuthConsumers;

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
		}
		
		/*
		 * get friends for sidebar
		 */
		final String loggedInUserName = command.getUser().getName();
		command.setUserFriends(this.logic.getUserRelationship(loggedInUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag));
		command.setFriendsOfUser(this.logic.getUserRelationship(loggedInUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag));

		// show sync tab only for non-spammers
		command.showSyncTab(!loginUser.isSpammer());

		if (command.getSelTab() < 0 || command.getSelTab() > 7) {
			this.errors.reject("error.settings.tab");
		} else {
			this.checkInstalledJabrefLayout(command);
			this.workOnGroupTab(command);
			this.workOnSyncSettingsTab(command);
			this.workOnCVTab(command);
			this.workOnOAuthTab(command);
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
		 * set jabref layouts of the users TODO: better solution?
		 */
		for (final LayoutPart layoutpart : LayoutPart.values()) {
			final String fileHash = JabrefLayoutUtils.userLayoutHash(loggedInUserName, layoutpart);
			/*
			 * check whether the user has the jabref layout (begin, end or item)
			 */
			final Document document = this.logic.getDocument(loggedInUserName, fileHash);
			/*
			 * if a document was found set the corresponding hash and name of
			 * the file
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

	/**
	 * function to get the OAuth User Information and store it in the
	 * SettingsViewCommand object
	 * 
	 * @param command
	 */
	private void workOnOAuthTab(final SettingsViewCommand command) {
		/*
		 * test if user pressed the delete button. Then delete the OAuth access
		 */
		// TODO: extract to separate Controller?
		if ("Delete".equals(command.getAction())) {
			final String accessTokenDelete = command.getAccessTokenDelete();
			if (present(this.invisibleOAuthConsumers) && present(accessTokenDelete)) {
				final List<OAuthUserInfo> oauthUserInfos = this.oauthLogic.getOAuthUserApplication(command.getContext().getLoginUser().getName());
				for (final OAuthUserInfo oAuthUserInfo : oauthUserInfos) {
					if (accessTokenDelete.equals(oAuthUserInfo.getAccessToken()) && this.invisibleOAuthConsumers.contains(oAuthUserInfo.getConsumerKey())) {
						throw new IllegalArgumentException("The access token " + accessTokenDelete + " can not be deleted.");
					}
				}
			}
			this.oauthLogic.removeSpecificAccessToken(command.getUser().getName(), accessTokenDelete);
		}
		/*
		 * get the valid OAuth applications of the user
		 */
		final List<OAuthUserInfo> oauthUserInfos = this.oauthLogic.getOAuthUserApplication(command.getContext().getLoginUser().getName());
		if (present(this.invisibleOAuthConsumers)) {
			for (final Iterator<OAuthUserInfo> iterator = oauthUserInfos.iterator(); iterator.hasNext();) {
				final OAuthUserInfo oAuthUserInfo = iterator.next();
				if (this.invisibleOAuthConsumers.contains(oAuthUserInfo.getConsumerKey())) {
					iterator.remove();
				}
			}
		}
		/*
		 * calculate the expiration time and issue time
		 */
		for (final OAuthUserInfo userInfo : oauthUserInfos) {
			userInfo.calculateExpirationTime(); // TODO: can ibatis do that for
												// us?
		}

		command.setOauthUserInfo(oauthUserInfos);
	}

	private void workOnGroupTab(final SettingsViewCommand command) {
		final String groupName = command.getContext().getLoginUser().getName();
		// the group to update
		final Group group = this.logic.getGroupDetails(groupName);
		if (present(group)) {
			command.setGroup(group);
			/*
			 * get group users
			 */
			group.setUsers(this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, Integer.MAX_VALUE));
			/*
			 * FIXME: use the group in the command instead of this hand-written
			 * conversion
			 */
			command.setPrivlevel(group.getPrivlevel().ordinal());

			/*
			 * TODO: use share docs directly
			 */
			int sharedDocsAsInt = 0;
			if (group.isSharedDocuments()) {
				sharedDocsAsInt = 1;
			}
			command.setSharedDocuments(sharedDocsAsInt);
		}
	}

	/**
	 * handles synchronization tab
	 * 
	 * @param command
	 */
	private void workOnSyncSettingsTab(final SettingsViewCommand command) {
		final List<SyncService> userServers = this.logic.getSyncService(command.getUser().getName(), null, true);
		final List<URI> allServers = this.logic.getSyncServices(true);

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
		command.setAvailableSyncClients(this.logic.getSyncServices(false));
	}

	/**
	 * @return the current command
	 */
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		final User user = new User();
		user.setSettings(new UserSettings());
		command.setUser(user);

		/*
		 * instantiate empty server user, this seems to be required since spring
		 * update
		 */
		final Properties serverUser = new Properties();
		serverUser.setProperty("userName", "");
		serverUser.setProperty("apiKey", "");
		final SyncService newSyncServer = new SyncService();
		newSyncServer.setServerUser(serverUser);
		command.setNewSyncServer(newSyncServer);

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
	 * @param logic
	 *            the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param requestLogic
	 *            the requestLogic to set
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	private void workOnCVTab(final SettingsViewCommand command) {
		log.debug("settings: cv tab accessed.");
		try {
			final User loginUser = command.getContext().getLoginUser();
			final String requestedUser = loginUser.getName();
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser);
			/*
			 * check if the group is present. If it should be a user. If its no
			 * user the we will catch the exception and return an error message
			 * to the user s
			 */
			if (present(requestedGroup)) {
				this.handleGroupCV(requestedGroup, command);
			} else {
				this.handleUserCV(loginUser, command);
			}
		} catch (final RuntimeException e) {
			// If the name does not fit to anything a runtime exception is
			// thrown while attempting to get the requestedUser
			throw new MalformedURLSchemeException("Something went wrong! You are most likely looking for a non existant user/group.");
		} catch (final Exception e) {
			throw new MalformedURLSchemeException("Something went wrong while working on your request. Please try again.");
		}
	}

	/**
	 * Handles the group cv page request
	 * 
	 * @param reqGroup
	 * @param command
	 */
	private void handleGroupCV(final Group requestedGroup, final SettingsViewCommand command) {
		final String groupName = requestedGroup.getName();
		command.setIsGroup(true);

		final List<User> groupUsers = this.logic.getUsers(null, GroupingEntity.GROUP, groupName, null, null, null, null, null, 0, 1000);
		requestedGroup.setUsers(groupUsers);

		// TODO: Implement date selection on the editing page
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
		this.wikiRenderer.setRequestedGroup(requestedGroup);
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);
	}

	/**
	 * Handles the user cv page request
	 * 
	 * @param reqUser
	 * @param command
	 */
	private void handleUserCV(final User requestedUser, final SettingsViewCommand command) {
		command.setUser(requestedUser);
		final String userName = requestedUser.getName();

		/*
		 * convert the wiki syntax
		 */
		// TODO: Implement date selection on the editing page
		final Wiki wiki = this.logic.getWiki(userName, null);
		final String wikiText;

		if (present(wiki)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}

		/*
		 * set the user to render
		 */
		this.wikiRenderer.setRequestedUser(requestedUser); // FME: not
															// thread-safe!
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));
		command.setWikiText(wikiText);
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	@Required
	public void setWikiRenderer(final CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

	/**
	 * @param oauthLogic
	 *            the oauthLogic to set
	 */
	public void setOauthLogic(final OAuthLogic oauthLogic) {
		this.oauthLogic = oauthLogic;
	}

	/**
	 * @return
	 */
	public List<String> getInvisibleOAuthConsumers() {
		return this.invisibleOAuthConsumers;
	}

	/**
	 * @param invisibleOAuthConsumers
	 */
	public void setInvisibleOAuthConsumers(final List<String> invisibleOAuthConsumers) {
		this.invisibleOAuthConsumers = invisibleOAuthConsumers;
	}

}
