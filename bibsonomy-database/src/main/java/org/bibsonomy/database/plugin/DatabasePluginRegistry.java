package org.bibsonomy.database.plugin;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.plugins.Basket;
import org.bibsonomy.database.plugin.plugins.BibTexExtra;
import org.bibsonomy.database.plugin.plugins.Logging;
import org.bibsonomy.database.util.DBSession;

/**
 * All database plugins are registered here.
 * 
 * FIXME: should implement DatabasePlugin, i.e. have the same methods as
 * DatabasePlugin
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class DatabasePluginRegistry {

	private static final DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	/** Holds all plugins */
	private final Map<String, DatabasePlugin> plugins;
	/** Runs the runnable returned by plugins */
	// private final ExecutorService executor;

	private DatabasePluginRegistry() {
		this.plugins = new HashMap<String, DatabasePlugin>();
		// this.executor = Executors.newCachedThreadPool();

		// XXX: shouldn't be wired statically...
		this.add(new Logging());
		this.add(new BibTexExtra());
		this.add(new Basket());
	}

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
		if (this.plugins.containsKey(key)) throw new RuntimeException("Plugin already present " + key);
		this.plugins.put(key, plugin);
	}

	/*
	 * FIXME: will be removed with the introduction of a DI-framework
	 */
	public void clearPlugins() {
		this.plugins.clear();
	}

	private void executeRunnable(final Runnable runnable) {
		// If the runnable is null -> do nothing
		if (runnable == null) return;
		// this.executor.execute(runnable);
		runnable.run();
	}

	public void onBibTexInsert(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexInsert(contentId, session));
		}
	}

	public void onBibTexDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexDelete(contentId, session));
		}
	}

	public void onBibTexUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexUpdate(newContentId, oldContentId, session));
		}
	}

	public void onBookmarkInsert(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkInsert(contentId, session));
		}
	}

	public void onBookmarkDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkDelete(contentId, session));
		}
	}

	public void onBookmarkUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkUpdate(newContentId, oldContentId, session));
		}
	}

	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session));
		}
	}
	
	public void onConceptDelete(String conceptName, String userName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onConceptDelete(conceptName, userName, session));
		}
	}

	public void onTagDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onTagDelete(contentId, session));
		}
	}

	public void onRemoveUserFromGroup(final String username, final int groupId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onRemoveUserFromGroup(username, groupId, session));
		}
	}
	
	public void onUserDelete(String userName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserDelete(userName, session));
		}
	}

	public void onUserInsert(String userName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserInsert(userName, session));
		}
	}

	public void onUserUpdate(String userName, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserUpdate(userName, session));
		}
	}
	
	/**
	 * @param param
	 * @param session
	 */
	public void onDeleteFellowship(UserParam param, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteFellowship(param, session));
		}
	}
	
	/**
	 * @param param
	 * @param session
	 */
	public void onDeleteFriendship(UserParam param, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteFriendship(param, session));
		}
	}
	
	/**
	 * @param param
	 * @param session
	 */
	public void onDeleteBasketItem(final BasketParam param, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteBasketItem(param, session));
		}
	}
	
	/**
	 * @param userName
	 * @param session
	 */
	public void onDeleteAllBasketItems(final String userName, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()){
			this.executeRunnable(plugin.onDeleteAllBasketItems(userName, session));
		}
	}
}