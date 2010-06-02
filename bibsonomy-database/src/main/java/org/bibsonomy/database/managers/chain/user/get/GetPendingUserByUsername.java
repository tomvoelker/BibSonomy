package org.bibsonomy.database.managers.chain.user.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Get group members
 * 
 * @author cbaier
 * @version $Id:
 */
public class GetPendingUserByUsername extends UserChainElement {

    @Override
    protected List<User> handle(final UserParam param, final DBSession session) {
        return this.userDB.getPendingUserByUsername(param.getRequestedGroupName(), param.getOffset(), param.getLimit(), session);
    }

    @Override
    protected boolean canHandle(final UserParam param) {
        log.debug(param.getRequestedGroupName());
        return (GroupingEntity.PENDING.equals(param.getGrouping()) && param.getRequestedGroupName() != null);
    }
}