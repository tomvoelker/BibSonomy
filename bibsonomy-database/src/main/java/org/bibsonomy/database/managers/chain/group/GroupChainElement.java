package org.bibsonomy.database.managers.chain.group;

import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.List;

public abstract class GroupChainElement extends ChainElement<List<Group>, GroupQuery> {

    private final GroupDatabaseManager groupDB;

    public GroupChainElement(GroupDatabaseManager groupDatabaseManager) {
        this.groupDB = groupDatabaseManager;
    }
}
