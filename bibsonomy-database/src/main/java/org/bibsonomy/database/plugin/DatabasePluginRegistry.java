package org.bibsonomy.database.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.plugins.BasketPlugin;
import org.bibsonomy.database.plugin.plugins.BibTexExtraPlugin;
import org.bibsonomy.database.plugin.plugins.GoldStandardPublicationReferencePlugin;
import org.bibsonomy.database.plugin.plugins.Logging;
import org.bibsonomy.database.util.DBSession;

/**
 * All database plugins are registered here.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class DatabasePluginRegistry implements DatabasePlugin {
	
	private static final Set<DatabasePlugin> DEFAULT_PLUGINS;
	
	static {
		DEFAULT_PLUGINS = new HashSet<DatabasePlugin>();
		
		DEFAULT_PLUGINS.add(new Logging());
		DEFAULT_PLUGINS.add(new BibTexExtraPlugin());
		DEFAULT_PLUGINS.add(new BasketPlugin());
		DEFAULT_PLUGINS.add(new GoldStandardPublicationReferencePlugin());
	}
	
	/**
	 * @return the default plugins
	 */
	public static Set<DatabasePlugin> getDefaultPlugins() {
		return Collections.unmodifiableSet(DEFAULT_PLUGINS);
	}

	private static final DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	/** Holds all plugins */
	private final Map<String, DatabasePlugin> plugins;
	
	private DatabasePluginRegistry() {
		this.plugins = new HashMap<String, DatabasePlugin>();
		
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
		if (this.plugins.containsKey(key)) throw new RuntimeException("Plugin already present " + key);
		this.plugins.put(key, plugin);
	}

	/**
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
	
	@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexInsert(contentId, session));
		}
		
		return null;
	}

	@Override
	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexDelete(contentId, session));
		}
		
		return null;
	}

	@Override
	public Runnable onBibTexUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexUpdate(newContentId, oldContentId, session)); // new and old contentId are not swapped!
		}
		
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationCreate(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onGoldStandardPublicationCreate(interhash, session));
		}
		
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationDelete(final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onGoldStandardPublicationDelete(interhash, session));
		}
		
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationUpdate(final String newInterhash, final String interhash, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onGoldStandardPublicationUpdate(newInterhash, interhash, session));
		}
		
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationReferenceCreate(final String userName, final String interHashPublication, final String interHashReference) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onGoldStandardPublicationReferenceCreate(userName, interHashPublication, interHashReference));
		}
		
		return null;
	}

	@Override
	public Runnable onGoldStandardPublicationReferenceDelete(final String userName, final String interHashPublication, final String interHashReference, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onGoldStandardPublicationReferenceDelete(userName, interHashPublication, interHashReference, session));
		}
		
		return null;
	}

	@Override
	public Runnable onBookmarkInsert(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkInsert(contentId, session));
		}

		return null;
	}

	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkDelete(contentId, session));
		}

		return null;
	}

	@Override
	public Runnable onBookmarkUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBookmarkUpdate(newContentId, oldContentId, session));
		}
		
		return null;
	}

	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session));
		}

		return null;
	}
	
	@Override
	public Runnable onConceptDelete(final String conceptName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onConceptDelete(conceptName, userName, session));
		}

		return null;
	}

	@Override
	public Runnable onTagDelete(final int contentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onTagDelete(contentId, session));
		}

		return null;
	}

	@Override
	public Runnable onRemoveUserFromGroup(final String username, final int groupId, DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onRemoveUserFromGroup(username, groupId, session));
		}

		return null;
	}
	
	@Override
	public Runnable onUserDelete(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserDelete(userName, session));
		}

		return null;
	}

	@Override
	public Runnable onUserInsert(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserInsert(userName, session));
		}

		return null;
	}

	@Override
	public Runnable onUserUpdate(final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onUserUpdate(userName, session));
		}
		
		return null;
	}
	
	@Override
	public Runnable onDeleteFellowship(final UserParam param, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteFellowship(param, session));
		}
		
		return null;
	}
	
	@Override
	public Runnable onDeleteFriendship(final UserParam param,final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteFriendship(param, session));
		}
		
		return null;
	}
	
	@Override
	public Runnable onDeleteBasketItem(final BasketParam param, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onDeleteBasketItem(param, session));
		}

		return null;
	}
	
	@Override
	public Runnable onDeleteAllBasketItems(final String userName, final DBSession session){
		for (final DatabasePlugin plugin : this.plugins.values()){
			this.executeRunnable(plugin.onDeleteAllBasketItems(userName, session));
		}

		return null;
	}
}