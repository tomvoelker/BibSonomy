package org.bibsonomy.database.plugin;

import java.util.HashMap;
import java.util.Map;

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

	private final static DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	/** Holds all plugins */
	private final Map<String, DatabasePlugin> plugins;
	/** Runs the runnable returned by plugins */
	// private final ExecutorService executor;

	private DatabasePluginRegistry() {
		this.plugins = new HashMap<String, DatabasePlugin>();
		// this.executor = Executors.newCachedThreadPool();

		// XXX: shouldn't be wired statically...
		this.add(new Logging());
	}

	public static DatabasePluginRegistry getInstance() {
		return singleton;
	}

	/**
	 * Plugins can be added with this method.
	 */
	public void add(final DatabasePlugin plugin) {
		final String key = plugin.getClass().getName();
		if (this.plugins.containsKey(key)) throw new RuntimeException("Plugin already present " + key);
		this.plugins.put(key, plugin);
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

	public void onBibTexUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onBibTexUpdate(oldContentId, newContentId, session));
		}
	}

	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		for (final DatabasePlugin plugin : this.plugins.values()) {
			this.executeRunnable(plugin.onTagRelationDelete(upperTagName, lowerTagName, userName, session));
		}
	}
}