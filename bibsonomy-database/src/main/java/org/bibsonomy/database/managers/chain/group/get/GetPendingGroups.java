package org.bibsonomy.database.managers.chain.group.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.List;

/**
 * Handles retrieval of pending groups.
 *
 * This relies on {@link GroupDatabaseManager#getPendingGroups(String, int, int, DBSession)} and therefore handles two
 * cases:
 * 1) If no username is set in the query object, all pending groups will be retrieved
 * 2) If a username is set, only pending groups requestes by this user are retrieved
 *
 * @author ada
 */
public class GetPendingGroups extends GroupChainElement {

	public GetPendingGroups(GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(QueryAdapter<GroupQuery> param, DBSession session) {
		final GroupQuery query = param.getQuery();
		return this.groupDb.getPendingGroups(query.getUserName(), query.getStart(), query.getEnd(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<GroupQuery> param) {
		return param.getQuery().isPending();
	}
}
