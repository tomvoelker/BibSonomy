package org.bibsonomy.database.managers.chain.resource.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.QueryBasedResourceChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * get the history of the specified post
 *
 * @author dzo
 */
public class GetResourceHistory<R extends Resource> extends QueryBasedResourceChainElement<R> {

	@Override
	protected List<Post<R>> handle(final QueryAdapter<PostQuery<R>> param, final DBSession session) {
		final PostQuery<R> query = param.getQuery();

		final int limit = BasicQueryUtils.calcLimit(query);
		final int offset = BasicQueryUtils.calcOffset(query);

		return this.databaseManager.getPostsWithHistory(query.getHash(), query.getGroupingName(), limit, offset, session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<PostQuery<R>> param) {
		final PostQuery<R> query = param.getQuery();
		return ValidationUtils.safeContains(query.getFilters(), FilterEntity.HISTORY);
	}
}
