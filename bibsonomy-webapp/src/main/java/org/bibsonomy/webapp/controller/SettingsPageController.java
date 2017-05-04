/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
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
import org.bibsonomy.services.URLGenerator;
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
	protected CSLFilesManager cslFilesManager;
	protected URLGenerator urlGenerator;
	
	/**
	 * The List is used in a hack to protect certain oAuth Tokens from
	 * deletions. Particularly, the oAuth-Tokens in PUMA are created
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
		// TODO: unused ?
		if (UserUtils.userIsGroup(loginUser)) {
			command.setHasOwnGroup(true);
		}
		
		/*
		 * get friends for sidebar
		 */
		final String loggedInUserName = loginUser.getName();
		command.setUserFriends(this.logic.getUserRelationship(loggedInUserName, UserRelation.FRIEND_OF, NetworkRelationSystemTag.BibSonomyFriendSystemTag));
		command.setFriendsOfUser(this.logic.getUserRelationship(loggedInUserName, UserRelation.OF_FRIEND, NetworkRelationSystemTag.BibSonomyFriendSystemTag));

		/*
		 * show sync tab only for non-spammers
		 */
		final boolean loggedinUserIsSpammer = loginUser.isSpammer();
		command.showSyncTab(!loggedinUserIsSpammer);
		// show my profile tab if spammer tries to enter sync settings via selTab-ID
		final Integer selectedTab = command.getSelTab();
		if (loggedinUserIsSpammer && present(selectedTab) && selectedTab.intValue() == SettingsViewCommand.SYNC_IDX) {
			command.setSelTab(Integer.valueOf(SettingsViewCommand.MY_PROFILE_IDX));
		}
		
		/*
		 * Get pending requested groups
		 */
		command.setPendingRequestedgroups(this.logic.getGroups(true, loginUser.getName(), 0, Integer.MAX_VALUE));
		
		if (!present(selectedTab) || selectedTab.intValue() < SettingsViewCommand.MY_PROFILE_IDX || selectedTab.intValue() > SettingsViewCommand.CSL_IDX) {
			this.errors.reject("error.settings.tab");
		} else {
			this.checkInstalledJabrefLayout(command);
			this.checkInstalledCSLLayout(command);
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
	 * checks whether the user has already uploaded csl layout definitions
	 * 
	 * @param command
	 */
	private void checkInstalledCSLLayout(final SettingsViewCommand command) {
		final String loggedInUserName = command.getContext().getLoginUser().getName();
		
		//URGENT
		//FIX ME
		//XXX: don't do dis
		cslFilesManager.setLogic(logic);
		
		/*
		* load all uploaded csl files
		*/
		List<CSLStyle> styles = cslFilesManager.getUserLayouts(loggedInUserName);
		
		command.setCslFiles(styles);
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

	/**
	 * handles synchronization tab
	 * 
	 * @param command
	 */
	private void workOnSyncSettingsTab(final SettingsViewCommand command) {
		final List<SyncService> userServers = this.logic.getSyncServiceSettings(command.getUser().getName(), null, true);
		final List<SyncService> allServers = this.logic.getSyncServices(true, null);

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
		command.setAvailableSyncClients(this.logic.getSyncServices(false, null));
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
			final Group requestedGroup = this.logic.getGroupDetails(requestedUser, false);
			/*
			 * check if the group is present. If it should be a user. If its no
			 * user the we will catch the exception and return an error message
			 * to the user s
			 */
			if (present(requestedGroup)) {
				command.setIsGroup(true);
				this.handleCV(command, null, requestedGroup);
			} else {
				command.setUser(loginUser);
				this.handleCV(command, loginUser, null);
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
	 * Handles the cv page request
	 * @param command
	 * @param reqGroup
	 */
	private void handleCV(final SettingsViewCommand command, final User requestedUser, final Group requestedGroup) {
		final String wikiUserName;
		if (present(requestedGroup)) {
			wikiUserName = requestedGroup.getName();
		} else {
			wikiUserName = requestedUser.getName();
		}
		
		// TODO: Implement date selection on the editing page
		final Wiki wiki = this.logic.getWiki(wikiUserName, null);
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
		this.wikiRenderer.setRequestedUser(requestedUser);
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
	 * @param invisibleOAuthConsumers
	 */
	public void setInvisibleOAuthConsumers(final List<String> invisibleOAuthConsumers) {
		this.invisibleOAuthConsumers = invisibleOAuthConsumers;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @param cslFilesManager the cslFilesManager to set
	 */
	public void setCslFilesManager(CSLFilesManager cslFilesManager) {
		this.cslFilesManager = cslFilesManager;
	}
}