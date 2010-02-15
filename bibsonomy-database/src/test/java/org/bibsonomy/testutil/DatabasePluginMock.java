package org.bibsonomy.testutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.plugin.DatabasePlugin;
import org.bibsonomy.database.util.DBSession;
import org.junit.Test;

/**
 * This is a test database plugin that'll check whether a method from the
 * {@link DatabasePlugin} interface was called. The state can be checked with
 * the <code>is*</code> methods.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class DatabasePluginMock extends AbstractDatabasePlugin {

	private boolean onBibTexInsert;
	private boolean onBibTexDelete;
	private boolean onBibTexUpdate;

	private boolean onBookmarkInsert;
	private boolean onBookmarkUpdate;
	
	private boolean onGoldStandardPublicationCreate;
	private boolean onGoldStandardPublicationUpdate;
	private boolean onGoldStandardPublicationDelete;

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
		plugin.onBookmarkInsert(0, null);
		plugin.onBibTexUpdate(0, 1, null);
		
		assertTrue(plugin.isOnBookmarkInsert());
		assertTrue(plugin.isOnBibTexUpdate());
		
		plugin.reset();
		
		assertFalse(plugin.isOnBookmarkInsert());
		assertFalse(plugin.isOnBibTexUpdate());
	}

	@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		this.onBibTexInsert = true;
		return null;
	}

	@Override
	public Runnable onBibTexDelete(int contentId, DBSession session) {
		this.onBibTexDelete = true;
		return null;
	}

	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		assertTrue(contentId != newContentId);
		this.onBibTexUpdate = true;
		return null;
	}

	@Override
	public Runnable onBookmarkInsert(final int contentId, final DBSession session) {
		this.onBookmarkInsert = true;
		return null;
	}

	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		assertTrue(contentId != newContentId);
		this.onBookmarkUpdate = true;
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationCreate(String interhash, DBSession session) {
		this.onGoldStandardPublicationCreate = true;
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationUpdate(String newInterhash, String interhash, DBSession session) {
		this.onGoldStandardPublicationUpdate = true;
		return null;
	}
	
	@Override
	public Runnable onGoldStandardPublicationDelete(String interhash, DBSession session) {
		this.onGoldStandardPublicationDelete = true;
		return null;
	}

	@Override
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.onTagRelationDelete = true;
		return null;
	}
	
	/**
	 * sets all boolean fields to false
	 */
	public void reset() {
		try {
			for (final Field field : this.getClass().getDeclaredFields()) {
				field.setBoolean(this, false);
			}
		} catch (final Exception ignored) {
			// will not happen
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
	public boolean isOnGoldStandardPublicationCreate() {
		return this.onGoldStandardPublicationCreate;
	}

	/**
	 * @return the onGoldStandardPublicationUpdate
	 */
	public boolean isOnGoldStandardPublicationUpdate() {
		return this.onGoldStandardPublicationUpdate;
	}

	/**
	 * @return the onGoldStandardPublicationDelete
	 */
	public boolean isOnGoldStandardPublicationDelete() {
		return this.onGoldStandardPublicationDelete;
	}
}