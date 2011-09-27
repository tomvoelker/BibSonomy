package org.bibsonomy.database.managers.chain.resource.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns a list of resources for a given user.
 * 
 * @author Sven Stefani
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesWithDiscussions<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
	    
		return ( FilterEntity.POSTS_WITH_DISCUSSIONS.equals(param.getFilter())
			|| FilterEntity.POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER.equals(param.getFilter())
			
		);
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		return this.getDatabaseManagerForType(param.getResourceClass()).getPostsWithDiscussions(param.getUserName(), param.getRequestedUserName(), param.getGroups(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

}
