package org.bibsonomy.database.systemstags.database;

import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author nosebrain
 * @version $Id$
 */
public abstract class DatabaseSystemTag extends SystemTag {
	
	@Override
	public <T extends Resource> void performAfter(Post<T> post, DBSession session) {
				
	}
	
	@Override
	public <T extends Resource> void performBefore(Post<T> post, DBSession session) {
				
	}
}
