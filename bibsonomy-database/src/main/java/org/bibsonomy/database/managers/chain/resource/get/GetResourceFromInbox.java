package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns the posts in the users inbox
 * @param <R> 
 * @param <P>
 * 
 * @author sdo
 * @version $Id$
 * 
 */
public class GetResourceFromInbox<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {
	
	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		final PostDatabaseManager<R, P> db = this.getDatabaseManagerForType(param.getClass());
		
		if (present(param.getHash())) {
			/*
			 * If an intraHash is given, we retrieve only the posts with this hash from the users inbox 
			 */
			return db.getPostsFromInboxByHash(param.getUserName(), param.getHash(), session);
		}
	
		return db.getPostsFromInbox(param.getUserName(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final P param) {
		return param.getGrouping() == GroupingEntity.INBOX;
	}
}