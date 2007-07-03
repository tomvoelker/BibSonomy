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
	private boolean onBibTexUpdate;
	
	private boolean onBookmarkInsert;
	private boolean onBookmarkUpdate;
	
	private boolean onTagRelationDelete;

	public DatabasePluginMock() {
		try {
			for (final Field field : this.getClass().getDeclaredFields()) {
				field.setBoolean(this, false);
			}
		} catch (final Exception ex) {
			// will not happen
		}
	}

	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException {
		final DatabasePluginMock plugin = new DatabasePluginMock();
		for (final Field field : plugin.getClass().getDeclaredFields()) {
			assertFalse(field.getBoolean(plugin));
		}
	}

	/*@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		this.onBibTexInsert = true;
		return null;
	}*/

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
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.onTagRelationDelete = true;
		return null;
	}

	public boolean isOnBibTexInsert() {
		return this.onBibTexInsert;
	}

	public boolean isOnBibTexUpdate() {
		return this.onBibTexUpdate;
	}

	public boolean isOnBookmarkInsert() {
		return this.onBookmarkInsert;
	}

	public boolean isOnBookmarkUpdate() {
		return this.onBookmarkUpdate;
	}

	
	public boolean isOnTagRelationDelete() {
		return this.onTagRelationDelete;
	}
}