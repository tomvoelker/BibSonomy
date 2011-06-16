package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionPlugin extends AbstractDatabasePlugin {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onCommentUpdate(java.lang.String, org.bibsonomy.model.Comment, org.bibsonomy.model.Comment, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				final LoggingParam<String> param = new LoggingParam<String>();
				param.setNewId(discussionItem.getHash());
				param.setOldId(oldDiscussionItem.getHash());
				update("updateParentHash", param, session);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onBibTexDelete(int, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onPublicationDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				// TODO Delete discussion item if publication was deleted by the user
				// TODO: interhash
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onBookmarkDelete(int, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			
			@Override
			public void run() {
				// TODO Delete discussion item if bookmark was deleted by the user
			}
		};
	}		
}
