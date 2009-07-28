package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.DBSession;

/**
 * @author daill
 * @version $Id$
 */
public class Basket extends AbstractDatabasePlugin {
	
	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final BasketDatabaseManager basket = BasketDatabaseManager.getInstance();
				BasketParam param = new BasketParam();
				param.setContentId(contentId);
				param.setNewContentId(newContentId);
				basket.updateItem(param, session);
			}
		};
	}
	
	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session){
		return new Runnable() {
			public void run() {
				final BasketDatabaseManager basket = BasketDatabaseManager.getInstance();
				BasketParam param = new BasketParam();
				param.setContentId(contentId);
				param.setNewContentId(newContentId);
				basket.updateItem(param, session);
			}
		};
	}
}
