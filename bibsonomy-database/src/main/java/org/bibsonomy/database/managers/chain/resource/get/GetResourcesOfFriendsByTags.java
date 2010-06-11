package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of resources for a given friend of a user (this friend also
 * posted the resource to group friends (made resources viewable for friends))
 * restricted by a given tag.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesOfFriendsByTags<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(P param) {
		return (present(param.getUserName()) &&
				param.canHandle() &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				present(param.getRequestedUserName()) &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() == 0 &&
				param.getNumSimpleTags() > 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()) && 
				!present(param.getAuthor()) &&
				!present(param.getTitle()));
	}

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		/*
		 * if the requested user has the current user in his/her friend list, he may 
		 * see the posts
		 */
		if (this.generalDb.isFriendOf(param.getUserName(), param.getRequestedUserName(), session)) {
			return this.getDatabaseManagerForType(param.getClass()).getPostsByTagNamesForUser(param.getUserName(), param.getRequestedUserName(), param.getTagIndex(), GroupID.FRIENDS.getId(), param.getGroups(), param.getLimit(), param.getOffset(), param.getFilter(), param.getSystemTags().values(), session);
		}
		return new ArrayList<Post<R>>();
	}

}
