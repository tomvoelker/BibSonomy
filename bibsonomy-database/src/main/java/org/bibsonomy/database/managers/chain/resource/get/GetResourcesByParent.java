package org.bibsonomy.database.managers.chain.resource.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author matthias gerecht
 */
public class GetResourcesByParent<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P>  {

    //@Override
	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		return this.databaseManager.getPostsWithHistory(param.getHash(),param.getRequestedUserName(), param.getFilter(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final P param) {
		return (param.getFilter() == FilterEntity.POSTS_HISTORY);
	}

}