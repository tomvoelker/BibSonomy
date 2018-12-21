package org.bibsonomy.database.managers.chain.group.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.GroupOrder;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * chain element to retrieve groups by search
 * @author dzo
 */
public class GetGroupsBySearch extends GroupChainElement {

	/**
	 * default constructor
	 * @param groupDatabaseManager
	 */
	public GetGroupsBySearch(final GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(final QueryAdapter<GroupQuery> param, DBSession session) {
		return this.groupDb.getGroupsBySearch(param.getLoggedinUser(), param.getQuery());
	}

	@Override
	protected boolean canHandle(final QueryAdapter<GroupQuery> param) {
		final GroupQuery query = param.getQuery();
		return GroupOrder.GROUP_REALNAME.equals(query.getGroupOrder()) || present(query.getSearch()) || present(query.getPrefix());
	}
}