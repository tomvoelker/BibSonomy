package org.bibsonomy.database.plugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.InboxParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.plugins.BasketPlugin;
import org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin;
import org.bibsonomy.database.plugin.plugins.DiscussionPlugin;
import org.bibsonomy.database.plugin.plugins.GoldStandardPublicationReferencePlugin;
import org.bibsonomy.database.plugin.plugins.Logging;
import org.bibsonomy.database.plugin.plugins.MetaDataPlugin;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.GoldStandardRelation;

/**
 * All database plugins are registered here.
 * 
 * @author Christian Schenk
 */
public class DatabasePluginRegistry implements DatabasePlugin {
	
	private static final List<DatabasePlugin> DEFAULT_PLUGINS;
	
	static {
		// TODO: config via spring
		DEFAULT_PLUGINS = new LinkedList<DatabasePlugin>();
		
		// order matters!
		DEFAULT_PLUGINS.add(new Logging());
		DEFAULT_PLUGINS.add(new BibTexExtraPlugin());
		DEFAULT_PLUGINS.add(new BasketPlugin());
		DEFAULT_PLUGINS.add(new GoldStandardPublicationReferencePlugin());
		DEFAULT_PLUGINS.add(new DiscussionPlugin());
		DEFAULT_PLUGINS.add(new MetaDataPlugin());
	}
	
	/**
	 * @return the default plugins
	 */
	public static List<DatabasePlugin> getDefaultPlugins() {
		return Collections.unmodifiableList(DEFAULT_PLUGINS);
	}

	private static final DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	
	/** Holds all plugins; order matters! */
	private final LinkedHashMap<String, DatabasePlugin> plugins;
	
	private DatabasePluginRegistry() {
		this.plugins = new LinkedHashMap<String, DatabasePlugin>();
		
		for (final DatabasePlugin plugin : DatabasePluginRegistry.DEFAULT_PLUGINS) {
			this.add(plugin);
		}
	}

	/**
	 * @return {@link DatabasePluginRegistry} instance
	 */
	public static DatabasePluginRegistry getInstance() {
		return singleton;
	}

	/**
	 * Plugins can be added with this method.
	 * FIXME: will be removed with the introduction of a DI-framework
	 * @param plugin 
	 */
	public void add(final DatabasePlugin plugin) {
		final String key = plugin.getClass().getName();
		if (this.plugins.containsKey(key)) {
			throw new RuntimeException("Plugin already present " + key);
		}
		this.plugins.put(key, plugin);
	}

	/**
	 * FIXME: will be removed with the introduction of a DI-framework
	 */
	public void clearPlugins() {
		this.plugins.clear();
	}
	
	@Override
	public void onPublicationInsert(final Post<? extends Resource> post, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onPublicationInsert(post, session);
		}
	}

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onPublicationDelete(contentId, session);
		}
	}

	@Override
	public void onPublicationUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onPublicationUpdate(newContentId, oldContentId, session); // new and old contentId are not swapped!
		}
	}
	
	@Override
	public void onGoldStandardCreate(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onGoldStandardCreate(interhash, session);
		}		
	}

	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onGoldStandardDelete(interhash, session);
		}	
	}

	@Override
	public void onGoldStandardUpdate(final int oldContentId, final int newContentId, final String newInterhash, final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onGoldStandardUpdate(oldContentId, newContentId, newInterhash, interhash, session);
		}
	}
	
	@Override
	public void onGoldStandardPublicationReferenceCreate(final String userName, final String interHashPublication, final String interHashReference, final String interHashRelation) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onGoldStandardPublicationReferenceCreate(userName, interHashPublication, interHashReference, interHashRelation);
		}	
	}

	@Override
	public void onGoldStandardRelationDelete(final String userName, final String interHashPublication, final String interHashReference, final GoldStandardRelation interHashRelation, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onGoldStandardRelationDelete(userName, interHashPublication, interHashReference, interHashRelation, session);
		}
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onBookmarkInsert(post, session);
		}
	}

	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onBookmarkDelete(contentId, session);
		}
	}

	@Override
	public void onBookmarkUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onBookmarkUpdate(newContentId, oldContentId, session);
		}
	}

	@Override
	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session);
		}		
	}
	
	@Override
	public void onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onConceptDelete(conceptName, userName, session);
		}		
	}

	@Override
	public void onTagDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onTagDelete(contentId, session);
		}		
	}

	@Override
	public void onRemoveUserFromGroup(final String username, final int groupId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onRemoveUserFromGroup(username, groupId, session);
		}
	}
	
	@Override
	public void onUserDelete(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onUserDelete(userName, session);
		}
	}

	@Override
	public void onUserInsert(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onUserInsert(userName, session);
		}
	}

	@Override
	public void onUserUpdate(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onUserUpdate(userName, session);
		}
	}
	
	@Override
	public void onDeleteFellowship(final UserParam param, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onDeleteFellowship(param, session);
		}
	}
	
	@Override
	public void onDeleteFriendship(final UserParam param,final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onDeleteFriendship(param, session);
		}
	}
	
	@Override
	public void onDeleteBasketItem(final BasketParam param, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()) {
			plugin.onDeleteBasketItem(param, session);
		}
	}
	
	@Override
	public void onDeleteAllBasketItems(final String userName, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onDeleteAllBasketItems(userName, session);
		}
	}

	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onDiscussionUpdate(interHash, discussionItem, oldDiscussionItem, session);
		}
	}
	
	@Override
	public void onDiscussionItemDelete(final String interHash, final DiscussionItem deletedDiscussionItem, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onDiscussionItemDelete(interHash, deletedDiscussionItem, session);
		}
	}

	@Override
	public void onDocumentDelete(final DocumentParam deletedDocumentParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onDocumentDelete(deletedDocumentParam, session);
		}	
	}
	
	@Override
	public void onDocumentUpdate(DocumentParam updatedDocumentParam, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onDocumentUpdate(updatedDocumentParam, session);
		}
	}

	@Override
	public void onInboxMailDelete(final InboxParam deletedInboxMessageParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onInboxMailDelete(deletedInboxMessageParam, session);
		}
	}

	@Override
	/**
	 * @author MarcelM
	 */
	public void onBibTexExtraDelete(final BibTexExtraParam deletedBibTexExtraParam, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()){
			plugin.onBibTexExtraDelete(deletedBibTexExtraParam, session);
		}
	}
}