package org.bibsonomy.database.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bibsonomy.database.plugin.plugins.ExampleWaitPlugin;
import org.bibsonomy.model.Bookmark;

/**
 * All database plugins are registered here.
 * 
 * FIXME: should implement DatabasePlugin
 * 
 * @author Christian Schenk
 */
public class DatabasePluginRegistry {

	/** Singleton */
	private final static DatabasePluginRegistry singleton = new DatabasePluginRegistry();
	/** Holds all plugins */
	private final List<DatabasePlugin> plugins;
	/** Runs the runnable returned by plugins */
	private final ExecutorService executor;

	private DatabasePluginRegistry() {
		this.plugins = new ArrayList<DatabasePlugin>();
		this.executor = Executors.newCachedThreadPool();
		// Add plugins here
		this.plugins.add(new ExampleWaitPlugin());
	}

	public static DatabasePluginRegistry getInstance() {
		return singleton;
	}

	private void executeRunnable(final Runnable runnable) {
		// If the runnable is null we do nothing
		if (runnable == null) return;
		this.executor.execute(runnable);
	}

	public void onBookmarkCreate(final Bookmark bookmark) {
		for (final DatabasePlugin plugin : this.plugins) {
			this.executeRunnable(plugin.onBookmarkCreate(bookmark));
		}
	}

	public void onBookmarkUpdate(final Bookmark bookmark, final Bookmark oldBookmark) {
		for (final DatabasePlugin plugin : this.plugins) {
			this.executeRunnable(plugin.onBookmarkUpdate(bookmark, oldBookmark));
		}
	}	
}