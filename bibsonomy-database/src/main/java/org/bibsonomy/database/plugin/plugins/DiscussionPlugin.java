package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
  */
public class DiscussionPlugin extends AbstractDatabasePlugin {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onCommentUpdate(java.lang.String, org.bibsonomy.model.Comment, org.bibsonomy.model.Comment, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<DiscussionItem>();
		param.setInterHash(interHash);
		param.setOldParentHash(oldDiscussionItem.getHash());
		param.setNewParentHash(discussionItem.getHash());
		
		this.update("updateParentHash", param, session);
	}
}
