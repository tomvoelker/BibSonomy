/**
 * BibSonomy-Database - Database for BibSonomy.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.bibsonomy.common.information.JobInformation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.ClipboardParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * All database plugins are registered here.
 * 
 * @author Christian Schenk
 */
public class DatabasePluginRegistry implements DatabasePlugin {
	private static final DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	
	/**
	 * @return {@link DatabasePluginRegistry} instance
	 */
	@Deprecated // TODO: config via spring
	public static DatabasePluginRegistry getInstance() {
		return singleton;
	}
	
	private List<DatabasePlugin> plugins;
	private List<DatabasePlugin> defaultPlugins;

	private void callAllPlugins(Consumer<DatabasePlugin> consumer) {
		this.plugins.forEach(consumer);
	}
	
	@Override
	public List<JobInformation> onPublicationInsert(final Post<? extends BibTex> post, User loggedinUser, final DBSession session) {
		final List<JobInformation> allInfo = new LinkedList<>();

		for (final DatabasePlugin plugin : this.plugins) {
			allInfo.addAll(plugin.onPublicationInsert(post, loggedinUser, session));
		}
		return allInfo;
	}

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPublicationDelete(contentId, session));
	}

	@Override
	public void onPublicationUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPublicationUpdate(newContentId, oldContentId, session)); // new and old contentId are not swapped!
	}
	
	@Override
	public void onGoldStandardCreate(final String interhash, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onGoldStandardCreate(interhash, session));
	}

	@Override
	public void onGoldStandardDelete(final String interhash, User loggedinUser, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onGoldStandardDelete(interhash, loggedinUser, session));
	}

	@Override
	public void onGoldStandardUpdate(final int oldContentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onGoldStandardUpdate(oldContentId, newContentId, newInterhash, interhash, session));
	}
	
	@Override
	public void onGoldStandardPublicationReferenceCreate(final String userName, final String interHashPublication, final String interHashReference, final String interHashRelation) {
		this.callAllPlugins(plugin -> plugin.onGoldStandardPublicationReferenceCreate(userName, interHashPublication, interHashReference, interHashRelation));
	}

	@Override
	public void onGoldStandardRelationDelete(final String userName, final String interHashPublication, final String interHashReference, final GoldStandardRelation interHashRelation, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onGoldStandardRelationDelete(userName, interHashPublication, interHashReference, interHashRelation, session));
	}

	@Override
	public List<JobInformation> onBookmarkInsert(final Post<? extends Resource> post, User logginUser, final DBSession session) {
		final LinkedList<JobInformation> jobInformation = new LinkedList<>();
		for (final DatabasePlugin plugin : this.plugins) {
			jobInformation.addAll(plugin.onBookmarkInsert(post, logginUser, session));
		}

		return jobInformation;
	}

	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onBookmarkDelete(contentId, session));
	}

	@Override
	public void onBookmarkUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onBookmarkUpdate(newContentId, oldContentId, session));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onBookmarkMassUpdate(java.lang.String, int)
	 */
	@Override
	public void onBookmarkMassUpdate(String userName, int groupId, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onBookmarkMassUpdate(userName, groupId, session));
	}

	@Override
	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session));
	}
	
	@Override
	public void onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onConceptDelete(conceptName, userName, session));
	}

	@Override
	public void onTagDelete(final int contentId, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onTagDelete(contentId, session));
	}

	@Override
	public void onAddedGroupMembership(Group group, GroupMembership membership, User loggedinUser, DBSession session) {
		callAllPlugins(plugin -> plugin.onAddedGroupMembership(group, membership, loggedinUser, session));
	}

	@Override
	public void onRemovedGroupMembership(Group group, String username, User loggedinUser, DBSession session) {
		callAllPlugins(plugin -> plugin.onRemovedGroupMembership(group, username, loggedinUser, session));
	}

	@Override
	public void beforeRemoveGroup(Group group, User loggedInUser, DBSession session) {
		callAllPlugins(plugin -> plugin.beforeRemoveGroup(group, loggedInUser, session));
	}

	@Override
	public void beforeRemoveGroupMembership(Group group, String username, User loggedInUser, DBSession session) {
		callAllPlugins(plugin -> plugin.beforeRemoveGroupMembership(group, username, loggedInUser, session));
	}

	@Override
	public void onChangeUserMembershipInGroup(Group group, String userName, User loggedinUser, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onChangeUserMembershipInGroup(group, userName, loggedinUser, session));
	}
	
	@Override
	public void onUserDelete(final String userName, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onUserDelete(userName, session));
	}

	@Override
	public void onUserInsert(final String userName, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onUserInsert(userName, session));
	}

	@Override
	public void onUserUpdate(final String userName, User loggedinUser, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onUserUpdate(userName, loggedinUser, session));
	}
	
	@Override
	public void onDeleteFellowship(final UserParam param, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDeleteFellowship(param, session));
	}
	
	@Override
	public void onDeleteFriendship(final UserParam param,final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDeleteFriendship(param, session));
	}
	
	@Override
	public void onDeleteClipboardItem(final ClipboardParam param, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDeleteClipboardItem(param, session));
	}
	
	@Override
	public void onDeleteAllClipboardItems(final String userName, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDeleteAllClipboardItems(userName, session));
	}

	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDiscussionUpdate(interHash, discussionItem, oldDiscussionItem, session));
	}
	
	@Override
	public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedDiscussionItem, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDiscussionItemDelete(interHash, deletedDiscussionItem, session));
	}

	@Override
	public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDocumentDelete(deletedDocumentParam, session));
	}
	
	@Override
	public void onDocumentUpdate(DocumentParam updatedDocumentParam, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDocumentUpdate(updatedDocumentParam, session));
	}

	@Override
	public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onInboxMailDelete(deletedInboxMessageParam, session));
	}

	@Override
	public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onBibTexExtraDelete(deletedBibTexExtraParam, session));
	}

	@Override
	public void onPublicationMassUpdate(String username, int groupId, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPublicationMassUpdate(username, groupId, session));
	}

	@Override
	public void onDiscussionMassUpdate(String username, int groupId, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onDiscussionMassUpdate(username, groupId, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(org.bibsonomy.model.Person, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameDelete(PersonName personName, User loggedInUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonNameDelete(personName, loggedInUser, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdate(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdate(Person oldPerson, Person newPerson, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonUpdate(oldPerson, newPerson, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonDelete(Person person, User user, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonDelete(person, user, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPubPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPubPersonDelete(ResourcePersonRelation rel, User loginUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPubPersonDelete(rel, loginUser, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdateByUserName(java.lang.String, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdateByUserName(String userName, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonUpdateByUserName(userName, session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onUpdatePersonName(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameUpdate(PersonName oldPerson, User loggedinUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonNameUpdate(oldPerson, loggedinUser, session));
	}

	@Override
	public void onPersonResourceRelationUpdate(ResourcePersonRelation oldRelation, ResourcePersonRelation newRelation, User loggedinUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onPersonResourceRelationUpdate(oldRelation, newRelation, loggedinUser, session));
	}

	@Override
	public void onProjectInsert(final Project project, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onProjectInsert(project, session));
	}

	@Override
	public void onProjectUpdate(final Project oldProject, final Project newProject, User loggedinUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onProjectUpdate(oldProject, newProject, loggedinUser, session));
	}

	@Override
	public void onProjectDelete(final Project project, final User loggedinUser, final DBSession session) {
		this.callAllPlugins(plugin -> plugin.onProjectDelete(project, loggedinUser, session));
	}

	@Override
	public void onCRISLinkUpdate(CRISLink oldCRISLink, CRISLink link, User loginUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onCRISLinkUpdate(oldCRISLink, link, loginUser, session));
	}

	@Override
	public void onCRISLinkDelete(CRISLink crisLink, User loginUser, DBSession session) {
		this.callAllPlugins(plugin -> plugin.onCRISLinkDelete(crisLink, loginUser, session));
	}

	/**
	 * @param defaultPlugins the defaultPlugins to set
	 */
	public void setDefaultPlugins(List<DatabasePlugin> defaultPlugins) {
		this.defaultPlugins = defaultPlugins;
		this.reset();
	}

	/**
	 * remove all plugins from the registry
	 */
	public void removeAllPlugins() {
		this.plugins = new LinkedList<>();
	}

	/**
	 * @param plugin
	 */
	public void addPlugin(final DatabasePlugin plugin) {
		for (final DatabasePlugin databasePlugin : this.plugins) {
			if (databasePlugin.getClass().equals(plugin.getClass())) {
				throw new RuntimeException("plugin already registered");
			}
		}
		this.plugins.add(plugin);
	}

	/**
	 * 
	 */
	public void reset() {
		this.plugins = new LinkedList<>(this.defaultPlugins);
	}
}