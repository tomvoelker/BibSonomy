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

import lombok.Setter;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.security.access.AccessDeniedException;

/**
 * controller responsible to render the group settings page
 *
 * - /settings/group/GROUPNAME
 * 
 * @author niebler
 */
@Setter
public class GroupSettingsPageController implements MinimalisticController<GroupSettingsPageCommand> {
	protected LogicInterface logic;
	
	private CVWikiModel wikiRenderer;

	private boolean presetTagsEnabled;

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		return new GroupSettingsPageCommand();
	}

	@Override
	public View workOn(final GroupSettingsPageCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}
		
		final String requestedGroup = command.getRequestedGroup();
		if (!present(requestedGroup)) {
			throw new MalformedURLSchemeException("group settings without requested group");
		}
		
		final User loginUser = command.getContext().getLoginUser();
		command.setLoggedinUser(loginUser);
		final Group group = this.logic.getGroupDetails(requestedGroup, false);
		if (!present(group)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
		
		command.setGroup(group);
		
		// check if the logged in user is a member of this group (and no pending user)
		final GroupMembership groupMembership = GroupUtils.getGroupMembershipForUser(group, loginUser.getName(), false);
		if (!present(groupMembership)) {
			throw new AccessDeniedException("You are not allowed to view this page");
		}
		
		final GroupRole roleOfLoggedinUser = groupMembership.getGroupRole();
		command.setGroupMembership(groupMembership);
		
		// determine which tabs to show based on the role of the logged in user
		final boolean selectedByUser = present(command.getSelTab());
		switch (roleOfLoggedinUser) {
		case ADMINISTRATOR:
			final User groupUser = this.logic.getUserDetails(requestedGroup);
			command.setRealname(groupUser.getRealname());
			command.setHomepage(groupUser.getHomepage());
			if (present(group)) {
				command.setDescription(group.getDescription());
				command.setPrivlevel(group.getPrivlevel().getPrivlevel());
				command.setSharedDocuments(group.isSharedDocuments() ? 1 : 0);
				command.setAllowJoin(group.isAllowJoin());
				command.setDescription(group.getDescription());
			}
			command.setUser(groupUser);
			
			// initiate wiki
			this.initiateGroupCV(groupUser, group, command);
			
			command.addTab(GroupSettingsPageCommand.GROUP_SETTINGS_IDX, "navi.groupsettings");
			command.addTab(GroupSettingsPageCommand.MEMBER_LIST_IDX, "settings.group.memberList");
			if (presetTagsEnabled) {
				command.addTab(GroupSettingsPageCommand.TAG_LIST_IDX, "settings.group.presetTags");
			}
			command.addTab(GroupSettingsPageCommand.CV_IDX, "navi.cvedit");
			command.addTab(GroupSettingsPageCommand.DELETE_GROUP_IDX, "settings.group.disband");
			
			if (!selectedByUser) {
				command.setSelTab(GroupSettingsPageCommand.GROUP_SETTINGS_IDX);
			}
			break;
		case MODERATOR:
			//$FALL-THROUGH$ all users should see the member list
		default:
			command.addTab(GroupSettingsPageCommand.MEMBER_LIST_IDX, "settings.group.memberList");
			if (!selectedByUser) {
				command.setSelTab(GroupSettingsPageCommand.MEMBER_LIST_IDX);
			}
			break;
		}
		
		return Views.GROUPSETTINGSPAGE;
	}

	
	/**
	 * Initiates the group cv page
	 * 
	 * @param groupUser
	 * @param command
	 */
	private void initiateGroupCV(final User groupUser, final Group group, final GroupSettingsPageCommand command) {
		final String userName = groupUser.getName();

		final Wiki wiki = this.logic.getWiki(userName, null);
		final String wikiText;

		if (present(wiki)) {
			wikiText = wiki.getWikiText();
		} else {
			wikiText = "";
		}
		
		this.wikiRenderer.setRequestedGroup(group);
		command.setRenderedWikiText(this.wikiRenderer.render(wikiText));

		command.setWikiText(wikiText);
	}
	
}
