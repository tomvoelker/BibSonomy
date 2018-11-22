package org.bibsonomy.database.managers.chain.group.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.Arrays;
import java.util.List;

/**
 * Handles retrieval of groups associated with an external id.
 */
public class GetGroupsByExternalId extends GroupChainElement {

    public GetGroupsByExternalId(GroupDatabaseManager groupDatabaseManager) {
        super(groupDatabaseManager);
    }

    @Override
    protected List<Group> handle(GroupQuery param, DBSession session) {
        return Arrays.asList(this.groupDb.getGroupByExternalId(param.getExternalId(), session));
    }

    @Override
    protected boolean canHandle(GroupQuery param) {
        return null != param.getExternalId() && !param.getExternalId().isEmpty();
    }
}
