package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.DBSession;

/**
 * This plugin takes care of additional features for BibTex entries.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtra extends AbstractDatabasePlugin {

	@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				// TODO implement...
			}
		};
	}

	@Override
	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				// TODO implement...
			}
		};
	}

	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				// TODO implement...
			}
		};
	}
}