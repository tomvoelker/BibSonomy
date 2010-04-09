package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.DBSession;

/**
 * Updates/Deletes items from the basket.
 * 
 * XXX: we can't have a static/singleton {@link BasketDatabaseManager} instance,
 * because we have a circular dependency (the manager contains the plugins ...)
 * 
 * @author daill
 * @version $Id$
 */
public class BasketPlugin extends AbstractDatabasePlugin {
	
	@Override
	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				BasketDatabaseManager.getInstance().deleteItems(contentId, session);
			}
		};
	}
	
	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				BasketDatabaseManager.getInstance().deleteItems(contentId, session);
			}
		};
	}
	
	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				BasketDatabaseManager.getInstance().updateItems(newContentId, contentId, session);
			}
		};
	}
	
	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session){
		return new Runnable() {
			public void run() {
				BasketDatabaseManager.getInstance().updateItems(newContentId, contentId, session);
			}
		};
	}
}
