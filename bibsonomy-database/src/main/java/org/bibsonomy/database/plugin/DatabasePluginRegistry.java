/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.ClipboardParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
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
	@Deprecated
	public static DatabasePluginRegistry getInstance() {
		return singleton;
	}
	
	private List<DatabasePlugin> plugins;
	private List<DatabasePlugin> defaultPlugins;
	
	@Override
	public void onPublicationInsert(final Post<? extends BibTex> post, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPublicationInsert(post, session);
		}
	}

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPublicationDelete(contentId, session);
		}
	}

	@Override
	public void onPublicationUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPublicationUpdate(newContentId, oldContentId, session); // new and old contentId are not swapped!
		}
	}
	
	@Override
	public void onGoldStandardCreate(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onGoldStandardCreate(interhash, session);
		}
	}

	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onGoldStandardDelete(interhash, session);
		}
	}

	@Override
	public void onGoldStandardUpdate(final int oldContentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onGoldStandardUpdate(oldContentId, newContentId, newInterhash, interhash, session);
		}
	}
	
	@Override
	public void onGoldStandardPublicationReferenceCreate(final String userName, final String interHashPublication, final String interHashReference, final String interHashRelation) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onGoldStandardPublicationReferenceCreate(userName, interHashPublication, interHashReference, interHashRelation);
		}
	}

	@Override
	public void onGoldStandardRelationDelete(final String userName, final String interHashPublication, final String interHashReference, final GoldStandardRelation interHashRelation, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onGoldStandardRelationDelete(userName, interHashPublication, interHashReference, interHashRelation, session);
		}
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onBookmarkInsert(post, session);
		}
	}

	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onBookmarkDelete(contentId, session);
		}
	}

	@Override
	public void onBookmarkUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onBookmarkUpdate(newContentId, oldContentId, session);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onBookmarkMassUpdate(java.lang.String, int)
	 */
	@Override
	public void onBookmarkMassUpdate(String userName, int groupId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onBookmarkMassUpdate(userName, groupId, session);
		}
	}

	@Override
	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session);
		}
	}
	
	@Override
	public void onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onConceptDelete(conceptName, userName, session);
		}
	}

	@Override
	public void onTagDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onTagDelete(contentId, session);
		}
	}

	@Override
	public void onChangeUserMembershipInGroup(final String username, final int groupId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onChangeUserMembershipInGroup(username, groupId, session);
		}
	}
	
	@Override
	public void onUserDelete(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onUserDelete(userName, session);
		}
	}

	@Override
	public void onUserInsert(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onUserInsert(userName, session);
		}
	}

	@Override
	public void onUserUpdate(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onUserUpdate(userName, session);
		}
	}
	
	@Override
	public void onDeleteFellowship(final UserParam param, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onDeleteFellowship(param, session);
		}
	}
	
	@Override
	public void onDeleteFriendship(final UserParam param,final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onDeleteFriendship(param, session);
		}
	}
	
	@Override
	public void onDeleteClipboardItem(final ClipboardParam param, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onDeleteClipboardItem(param, session);
		}
	}
	
	@Override
	public void onDeleteAllClipboardItems(final String userName, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onDeleteAllClipboardItems(userName, session);
		}
	}

	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onDiscussionUpdate(interHash, discussionItem, oldDiscussionItem, session);
		}
	}
	
	@Override
	public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedDiscussionItem, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onDiscussionItemDelete(interHash, deletedDiscussionItem, session);
		}
	}

	@Override
	public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onDocumentDelete(deletedDocumentParam, session);
		}	
	}
	
	@Override
	public void onDocumentUpdate(DocumentParam updatedDocumentParam, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onDocumentUpdate(updatedDocumentParam, session);
		}
	}

	@Override
	public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onInboxMailDelete(deletedInboxMessageParam, session);
		}
	}

	
	/**
	 * @author MarcelM
	 */
	@Override
	public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onBibTexExtraDelete(deletedBibTexExtraParam, session);
		}
	}

	/**
	 * @param username
	 * @param groupId
	 * @param session
	 */
	@Override
	public void onPublicationMassUpdate(String username, int groupId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins){
			plugin.onPublicationMassUpdate(username, groupId, session);
		}
	}

	@Override
	public void onDiscussionMassUpdate(String username, int groupId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onDiscussionMassUpdate(username, groupId, session);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(org.bibsonomy.model.Person, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameDelete(PersonName personName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPersonNameDelete(personName, session);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdate(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdate(String personId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPersonUpdate(personId, session);
		}	
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonDelete(Person person, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPersonDelete(person, session);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPubPersonDelete(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPubPersonDelete(ResourcePersonRelation rel, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPubPersonDelete(rel, session);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onPersonUpdateByUserName(java.lang.String, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonUpdateByUserName(String userName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPersonUpdateByUserName(userName, session);
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.DatabasePlugin#onUpdatePersonName(java.lang.Integer, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPersonNameUpdate(Integer personChangeId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins) {
			plugin.onPersonNameUpdate(personChangeId, session);
		}
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