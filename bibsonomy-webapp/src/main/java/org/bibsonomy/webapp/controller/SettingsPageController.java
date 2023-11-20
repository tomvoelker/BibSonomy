/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import static org.bibsonomy.webapp.controller.ExportPageController.convertCSLStylesToMap;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.standard.StandardLayout;
import org.bibsonomy.layout.standard.StandardLayouts;
import org.bibsonomy.model.*;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.opensocial.oauth.database.OAuthLogic;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthUserInfo;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.*;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Steffen
 */
@Setter
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware, RequestAware {
	private static final Log log = LogFactory.getLog(SettingsPageController.class);

	/** hold current errors */
	protected Errors errors = null;

	protected OAuthLogic oauthLogic;
	protected LogicInterface logic;
	protected RequestLogic requestLogic;
	protected CSLFilesManager cslFilesManager;
	protected URLGenerator urlGenerator;

	private LayoutRenderer<AbstractJabRefLayout> layoutRenderer;
	private StandardLayouts layouts;

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
		command.setChangeTo((loginUser.getSettings().isMaxCount() ? loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));

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
		final GroupQuery groupQuery = GroupQuery.builder().end(Integer.MAX_VALUE)
				.userName(loggedInUserName).pending(true).build();
		command.setPendingRequestedgroups(this.logic.getGroups(groupQuery));

		/*
		 * Get current Person
		 */
		final Person person = loginUser.getClaimedPerson();
		command.setPerson(person);
		command.showPersonTab(person);
		
		if (!present(selectedTab) || selectedTab.intValue() < SettingsViewCommand.MY_PROFILE_IDX || selectedTab.intValue() > SettingsViewCommand.PERSON_IDX) {
			this.errors.reject("error.settings.tab");
		} else {
			this.checkInstalledJabrefLayout(command);
			this.checkInstalledCSLLayout(command);
			this.workOnSyncSettingsTab(command);
			this.workOnCVTab(command);
			this.workOnOAuthTab(command);
		}

		/*
		 * Settings tab
		 */
		final RequestWrapperContext context = command.getContext();
		final Map<String, Layout> layoutMap = new TreeMap<>(this.layoutRenderer.getLayouts());
		final Map<String, StandardLayout> layouts = this.layouts.getLayoutMap();
		layoutMap.putAll(layouts);

		if (context.isUserLoggedIn()) {
			try {
				final Layout layout = this.layoutRenderer.getLayout(LayoutRenderer.CUSTOM_LAYOUT, context.getLoginUser().getName());
				layoutMap.put(layout.getDisplayName(), layout);
			} catch (final LayoutRenderingException | IOException e) {
				// ignore because reasons
			}

			// also load user custom layouts
			command.setCustomCslLayoutMap(convertCSLStylesToMap(this.cslFilesManager.loadUserLayouts(context.getLoginUser().getName())));
		}

		command.setCslLayoutMap(this.cslFilesManager.getCslFiles());
		command.setLayoutMap(layoutMap);
		
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
	 * and if so loads it into the command
	 * @param command
	 */
	private void checkInstalledCSLLayout(final SettingsViewCommand command) {
		final String loggedInUserName = command.getContext().getLoginUser().getName();

		/*
		 * load all csl files that can be used for the person page
		 */
		final List<CSLStyle> personPageCslFiles = this.cslFilesManager.getStandardCslStyles();
		command.setPersonPageCslFiles(personPageCslFiles);

		/*
		 * Load cls layouts of the user
		 */
		final List<CSLStyle> styles = this.cslFilesManager.loadUserLayouts(loggedInUserName);
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
			oauthUserInfos.removeIf(oAuthUserInfo -> this.invisibleOAuthConsumers.contains(oAuthUserInfo.getConsumerKey()));
		}
		/*
		 * calculate the expiration time and issue time
		 */
		for (final OAuthUserInfo userInfo : oauthUserInfos) {
			userInfo.calculateExpirationTime(); // TODO: can ibatis do that for us
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
	 * @param requestedUser
	 * @param requestedGroup
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

}