package org.bibsonomy.database.systemstags.database;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class DatabaseSystemTag extends SystemTag {
	
	@Override
	public <T extends Resource> void performAfterUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {}

	@Override
	public <T extends Resource> void performBeforeUpdate(Post<T> oldPost, Post<T> newPost, final PostUpdateOperation operation, final DBSession session) {}

	@Override
	public <T extends Resource> void performAfterCreate(Post<T> post, DBSession session) {}
	
	@Override
	public <T extends Resource> void performBeforeCreate(Post<T> post, DBSession session) {}
}
