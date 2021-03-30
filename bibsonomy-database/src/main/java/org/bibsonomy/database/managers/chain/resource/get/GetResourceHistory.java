package org.bibsonomy.database.managers.chain.resource.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.QueryBasedResourceChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.util.ValidationUtils;

/**
 * get the history of the specified post
 *
 * @author dzo
 */
public class GetResourceHistory<R extends Resource> extends QueryBasedResourceChainElement<R> {

	@Override
	protected List<Post<R>> handle(QueryAdapter<PostQuery<R>> param, DBSession session) {
		final PostQuery<R> query = param.getQuery();
		return this.databaseManager.getPostsWithHistory(query.getHash(), query.getGroupingName(), query.getEnd(), query.getStart(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<PostQuery<R>> param) {
		final PostQuery<R> query = param.getQuery();
		return ValidationUtils.safeContains(query.getFilters(), FilterEntity.HISTORY);
	}
}
