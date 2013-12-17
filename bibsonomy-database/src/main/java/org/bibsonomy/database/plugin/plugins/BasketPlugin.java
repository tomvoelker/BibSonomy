package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;

/**
 * Updates/Deletes items from the basket.
 * 
 * XXX: we can't have a static/singleton {@link BasketDatabaseManager} instance,
 * because we have a circular dependency (the manager contains the plugins ...)
 * could be fixed by config registry with Spring beans
 * 
 * @author daill
  */
public class BasketPlugin extends AbstractDatabasePlugin {
	
	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		BasketDatabaseManager.getInstance().deleteItems(contentId, session);
	}
	
	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		BasketDatabaseManager.getInstance().deleteItems(contentId, session);
	}
	
	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		BasketDatabaseManager.getInstance().updateItems(newContentId, contentId, session);
	}
	
	@Override
	public void onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session){
		BasketDatabaseManager.getInstance().updateItems(newContentId, contentId, session);
	}
}
