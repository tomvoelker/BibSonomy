package org.bibsonomy.database.managers.chain.user.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;

/**
 * Get group members
 * 
 * @author cbaier
 * @version $Id:
 */
public class GetPendingUserByActivationCode extends UserChainElement {

    @Override
    protected List<User> handle(final UserParam param, final DBSession session) {
        return this.userDB.getPendingUserByActivationCode(param.getRawSearch(), param.getOffset(), param.getLimit(), session);
    }

    @Override
    protected boolean canHandle(final UserParam param) {
        return (GroupingEntity.PENDING.equals(param.getGrouping()) && param.getRawSearch() != null);
    }
}