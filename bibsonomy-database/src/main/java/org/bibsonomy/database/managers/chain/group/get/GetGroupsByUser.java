package org.bibsonomy.database.managers.chain.group.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * Retrieves all groups a user is member of.
 *
 * @author kchoong
 */
public class GetGroupsByUser extends GroupChainElement {

    public GetGroupsByUser(final GroupDatabaseManager groupDatabaseManager) {
        super(groupDatabaseManager);
    }

    @Override
    protected List<Group> handle(final QueryAdapter<GroupQuery> param, DBSession session) {
        final GroupQuery query = param.getQuery();
        return this.groupDb.getGroupsForUser(query.getUserName(), session);
    }

    @Override
    protected boolean canHandle(final QueryAdapter<GroupQuery> param) {
        final GroupQuery query = param.getQuery();
        return present(query.getUserName()) && !query.isPending();
    }
}
