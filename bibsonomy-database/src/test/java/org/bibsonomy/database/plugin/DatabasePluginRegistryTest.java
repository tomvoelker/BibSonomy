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
public class DatabasePluginRegistryTest extends AbstractDatabasePluginTest {

	@Test
	public void testThatPluginsAreCalled() {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		this.plugins.clearPlugins();
		this.plugins.add(plugin);

		assertFalse(plugin.isOnBibTexDelete());
		assertFalse(plugin.isOnBibTexInsert());
		assertFalse(plugin.isOnBibTexUpdate());
		assertFalse(plugin.isOnBookmarkInsert());
		assertFalse(plugin.isOnBookmarkUpdate());
		assertFalse(plugin.isOnTagRelationDelete());

		this.plugins.onBibTexDelete(1, null);
		this.plugins.onBibTexInsert(1, null);
		this.plugins.onBibTexUpdate(1, 2, null);
		this.plugins.onBookmarkInsert(1, null);
		this.plugins.onBookmarkUpdate(1, 2, null);
		this.plugins.onTagRelationDelete(null, null, null, null);

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
		this.plugins.clearPlugins();
		this.plugins.add(plugin);

		try {
			this.plugins.add(plugin);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}