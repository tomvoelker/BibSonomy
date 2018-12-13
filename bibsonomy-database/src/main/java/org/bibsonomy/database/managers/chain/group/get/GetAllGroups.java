package org.bibsonomy.database.managers.chain.group.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.List;

/**
 * Handles retrieval of all groups.
 * @author ada
 */
public class GetAllGroups extends GroupChainElement {

	public GetAllGroups(GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(QueryAdapter<GroupQuery> queryAdapter, DBSession session) {
		final GroupQuery param = queryAdapter.getQuery();
		return this.groupDb.getAllGroups(param.getStart(), param.getEnd(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<GroupQuery> param) {
		return true;
	}
}
