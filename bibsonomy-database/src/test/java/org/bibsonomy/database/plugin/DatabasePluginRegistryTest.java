package org.bibsonomy.database.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.testutil.DatabasePluginMock;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class DatabasePluginRegistryTest {

	private final DatabasePluginRegistry pluginRegistry = DatabasePluginRegistry.getInstance();

	@Test
	public void testThatPluginsAreCalled() {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		this.pluginRegistry.clearPlugins();
		this.pluginRegistry.add(plugin);

		assertFalse(plugin.isOnBibTexDelete());
		assertFalse(plugin.isOnBibTexInsert());
		assertFalse(plugin.isOnBibTexUpdate());
		assertFalse(plugin.isOnBookmarkInsert());
		assertFalse(plugin.isOnBookmarkUpdate());
		assertFalse(plugin.isOnTagRelationDelete());

		this.pluginRegistry.onBibTexDelete(1, null);
		this.pluginRegistry.onBibTexInsert(1, null);
		this.pluginRegistry.onBibTexUpdate(1, 2, null);
		this.pluginRegistry.onBookmarkInsert(1, null);
		this.pluginRegistry.onBookmarkUpdate(1, 2, null);
		this.pluginRegistry.onTagRelationDelete(null, null, null, null);

		assertTrue(plugin.isOnBibTexDelete());
		assertTrue(plugin.isOnBibTexInsert());
		assertTrue(plugin.isOnBibTexUpdate());
		assertTrue(plugin.isOnBookmarkInsert());
		assertTrue(plugin.isOnBookmarkUpdate());
		assertTrue(plugin.isOnTagRelationDelete());
	}

	@Test
	public void onlyOnePluginInstancePerTypeAllowed() {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		this.pluginRegistry.clearPlugins();
		this.pluginRegistry.add(plugin);

		try {
			this.pluginRegistry.add(plugin);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}