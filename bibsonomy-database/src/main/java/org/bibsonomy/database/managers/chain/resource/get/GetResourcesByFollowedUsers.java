package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns all resources of users you are following.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesByFollowedUsers<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(P param) {
		return (present(param.getUserName()) &&
				present(param.getGroups()) &&
				param.getGrouping() == GroupingEntity.FOLLOWER);
	}

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		return this.getDatabaseManagerForType(param.getClass()).getPostsByFollowedUsers(param.getUserName(), param.getGroups(), param.getLimit(), param.getOffset(), session);
	}

}
