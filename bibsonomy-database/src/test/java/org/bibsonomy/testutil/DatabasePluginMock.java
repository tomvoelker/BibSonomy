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
package org.bibsonomy.testutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.plugin.DatabasePlugin;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

/**
 * This is a test database plugin that'll check whether a method from the
 * {@link DatabasePlugin} interface was called. The state can be checked with
 * the <code>is*</code> methods.
 * 
 * @author Christian Schenk
 */
public class DatabasePluginMock extends AbstractDatabasePlugin {

	private boolean onBibTexInsert;
	private boolean onBibTexDelete;
	private boolean onBibTexUpdate;

	private boolean onBookmarkInsert;
	private boolean onBookmarkUpdate;
	
	private boolean onGoldStandardCreate;
	private boolean onGoldStandardUpdate;
	private boolean onGoldStandardDelete;

	private boolean onTagRelationDelete;

	/**
	 * The constructor initializes every boolean member variable with false.
	 */
	public DatabasePluginMock() {
		// make sure that all fields are initialized with "false"
		this.reset();
	}

	/**
	 * Makes sure that every field is initializes with false.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		for (final Field field : plugin.getClass().getDeclaredFields()) {
			assertFalse(field.getBoolean(plugin));
		}
	}
	
	/**
	 * tests reset method
	 */
	@Test
	public void testReset() {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		plugin.onBookmarkInsert(null, null);
		plugin.onPublicationUpdate(0, 1, null);
		
		assertTrue(plugin.isOnBookmarkInsert());
		assertTrue(plugin.isOnBibTexUpdate());
		
		plugin.reset();
		
		assertFalse(plugin.isOnBookmarkInsert());
		assertFalse(plugin.isOnBibTexUpdate());
	}

	@Override
	public void onPublicationInsert(final Post<? extends BibTex> post, final DBSession session) {
		this.onBibTexInsert = true;
	}

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		this.onBibTexDelete = true;
	}

	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		assertTrue(contentId != newContentId);
		this.onBibTexUpdate = true;
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		this.onBookmarkInsert = true;
	}

	@Override
	public void onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		assertTrue(contentId != newContentId);
		this.onBookmarkUpdate = true;
	}
	
	@Override
	public void onGoldStandardCreate(final String interhash, final DBSession session) {
		this.onGoldStandardCreate = true;
	}
	
	@Override
	public void onGoldStandardUpdate(final int newContentId, final int contentId, final String newInterhash, final String interhash, final DBSession session) {
			this.onGoldStandardUpdate = true;
	}
	
	@Override
	public void onGoldStandardDelete(final String interhash, final DBSession session) {
		this.onGoldStandardDelete = true;
	}

	@Override
	public void onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.onTagRelationDelete = true;
	}
	
	/**
	 * sets all boolean fields to false
	 */
	public void reset() {
		
		for (final Field field : this.getClass().getDeclaredFields()) {
			try {
				field.setBoolean(this, false);
			} catch (final IllegalArgumentException ex) {
				// ignore
			} catch (final IllegalAccessException ex) {
				// ignore
			}
		}
	}

	/**
	 * @return true if onBibTexInsert was called, otherwise false
	 */
	public boolean isOnBibTexInsert() {
		return this.onBibTexInsert;
	}

	/**
	 * @return true if onBibTexDelete was called, otherwise false
	 */
	public boolean isOnBibTexDelete() {
		return this.onBibTexDelete;
	}

	/**
	 * @return true if onBibTexUpdate was called, otherwise false
	 */
	public boolean isOnBibTexUpdate() {
		return this.onBibTexUpdate;
	}

	/**
	 * @return true if onBookmarkInsert was called, otherwise false
	 */
	public boolean isOnBookmarkInsert() {
		return this.onBookmarkInsert;
	}

	/**
	 * @return true if onBookmarkUpdate was called, otherwise false
	 */
	public boolean isOnBookmarkUpdate() {
		return this.onBookmarkUpdate;
	}

	/**
	 * @return true if onTagRelationDelete was called, otherwise false
	 */
	public boolean isOnTagRelationDelete() {
		return this.onTagRelationDelete;
	}
	
	/**
	 * @return the onGoldStandardPublicationCreate
	 */
	public boolean isOnGoldStandardCreate() {
		return this.onGoldStandardCreate;
	}

	/**
	 * @return the onGoldStandardPublicationUpdate
	 */
	public boolean isOnGoldStandardUpdate() {
		return this.onGoldStandardUpdate;
	}

	/**
	 * @return the onGoldStandardPublicationDelete
	 */
	public boolean isOnGoldStandardDelete() {
		return this.onGoldStandardDelete;
	}
}