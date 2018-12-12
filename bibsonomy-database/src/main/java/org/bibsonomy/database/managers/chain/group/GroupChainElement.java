package org.bibsonomy.database.managers.chain.group;

import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.List;

/**
 * group chain element
 *
 * @author ada
 */
public abstract class GroupChainElement extends ChainElement<List<Group>, QueryAdapter<GroupQuery>> {

	private final GroupDatabaseManager groupDB;

	public GroupChainElement(GroupDatabaseManager groupDatabaseManager) {
		this.groupDB = groupDatabaseManager;
	}
}
