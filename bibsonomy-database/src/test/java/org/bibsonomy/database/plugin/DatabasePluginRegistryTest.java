package org.bibsonomy.database.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class DatabasePluginRegistryTest extends AbstractDatabaseManagerTest {

	/**
	 * tests that the plugins are called
	 */
	@Test
	public void testThatPluginsAreCalled() {
		this.pluginRegistry.clearPlugins();
		this.pluginRegistry.add(this.pluginMock);
		
		assertFalse(this.pluginMock.isOnBibTexDelete());
		assertFalse(this.pluginMock.isOnBibTexInsert());
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		assertFalse(this.pluginMock.isOnBookmarkInsert());
		assertFalse(this.pluginMock.isOnBookmarkUpdate());
		assertFalse(this.pluginMock.isOnTagRelationDelete());
		
		this.pluginMock.reset();

		this.pluginRegistry.onBibTexDelete(1, null);
		this.pluginRegistry.onBibTexInsert(1, null);
		this.pluginRegistry.onBibTexUpdate(1, 2, null);
		this.pluginRegistry.onBookmarkInsert(1, null);
		this.pluginRegistry.onBookmarkUpdate(1, 2, null);
		this.pluginRegistry.onTagRelationDelete(null, null, null, null);

		assertTrue(this.pluginMock.isOnBibTexDelete());
		assertTrue(this.pluginMock.isOnBibTexInsert());
		assertTrue(this.pluginMock.isOnBibTexUpdate());
		assertTrue(this.pluginMock.isOnBookmarkInsert());
		assertTrue(this.pluginMock.isOnBookmarkUpdate());
		assertTrue(this.pluginMock.isOnTagRelationDelete());
	}

	/**
	 * tests that only one plugin instance per type is allowed
	 */
	@Test
	public void onlyOnePluginInstancePerTypeAllowed() {
		this.pluginRegistry.clearPlugins();
		this.pluginRegistry.add(this.pluginMock);

		try {
			this.pluginRegistry.add(this.pluginMock);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}