package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 * Returns all resources of your friends.
 * 
 * TODO extension with user restriction rearding returned bibtex and appropriate
 * naming of URL in REST interface (e.g. /user/friend).
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author nosebrain
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesByFriends<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(P param) {
		return (present(param.getUserName()) &&
				param.canHandle() &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				!present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		return this.getDatabaseManagerForType(param.getClass()).getPostsByUserFriends(param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

}
