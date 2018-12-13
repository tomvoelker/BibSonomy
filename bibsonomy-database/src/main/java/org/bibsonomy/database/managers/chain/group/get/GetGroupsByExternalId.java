package org.bibsonomy.database.managers.chain.group.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.Arrays;
import java.util.List;

/**
 * Handles retrieval of groups associated with an external id.
 * @author ada
 */
public class GetGroupsByExternalId extends GroupChainElement {

	public GetGroupsByExternalId(GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(QueryAdapter<GroupQuery> param, DBSession session) {
		return Arrays.asList(this.groupDb.getGroupByExternalId(param.getQuery().getExternalId(), session));
	}

	@Override
	protected boolean canHandle(QueryAdapter<GroupQuery> param) {
		final GroupQuery query = param.getQuery();
		return present(query.getExternalId());
	}
}
