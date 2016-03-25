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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class DatabasePluginRegistryTest extends AbstractDatabaseManagerTest {

	/**
	 * tests that the plugins are called
	 */
	@Test
	public void testThatPluginsAreCalled() {
		pluginRegistry.removeAllPlugins();
		pluginRegistry.addPlugin(this.pluginMock);
		
		assertFalse(this.pluginMock.isOnBibTexDelete());
		assertFalse(this.pluginMock.isOnBibTexInsert());
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		assertFalse(this.pluginMock.isOnBookmarkInsert());
		assertFalse(this.pluginMock.isOnBookmarkUpdate());
		assertFalse(this.pluginMock.isOnTagRelationDelete());
		
		this.pluginMock.reset();

		pluginRegistry.onPublicationDelete(1, null);
		pluginRegistry.onPublicationInsert(null, null);
		pluginRegistry.onPublicationUpdate(1, 2, null);
		pluginRegistry.onBookmarkInsert(null, null);
		pluginRegistry.onBookmarkUpdate(1, 2, null);
		pluginRegistry.onTagRelationDelete(null, null, null, null);

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
		pluginRegistry.removeAllPlugins();
		pluginRegistry.addPlugin(this.pluginMock);

		try {
			pluginRegistry.addPlugin(this.pluginMock);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}