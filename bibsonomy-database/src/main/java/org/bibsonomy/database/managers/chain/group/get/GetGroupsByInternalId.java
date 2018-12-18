package org.bibsonomy.database.managers.chain.group.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles retrieval of groups associated with an external id.
 * @author ada
 */
public class GetGroupsByInternalId extends GroupChainElement {

	/**
	 * internal id
	 * @param groupDatabaseManager
	 */
	public GetGroupsByInternalId(GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(final QueryAdapter<GroupQuery> param, final DBSession session) {
		final Group groupByExternalId = this.groupDb.getGroupByInternalId(param.getQuery().getExternalId(), session);
		if (present(groupByExternalId)) {
			return Arrays.asList(groupByExternalId);
		}
		return Collections.emptyList();
	}

	@Override
	protected boolean canHandle(QueryAdapter<GroupQuery> param) {
		final GroupQuery query = param.getQuery();
		return present(query.getExternalId());
	}
}
