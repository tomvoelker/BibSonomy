package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Return all posts to a given Hash, that are visible for the loginUser (includinge friend, group, and owner checking)
 * This could be merged with GetResourcesByHash in which only public posts are fetched (GroupId.public!)
 * However, this would change the behaviour of that chain-element for each controller that uses it. 
 * @author sdo
 * @version $Id$
 */
public class GetResourcesByHashVisibleForLoginUser<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return (present(param.getHash()) &&
				param.getGrouping() == GroupingEntity.USER &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		return this.getDatabaseManagerForType(param.getResourceClass()).getPostsByHash(param.getUserName(), param.getHash(), HashID.getSimHash(param.getSimHash()), GroupID.INVALID.getId(), param.getGroups(), param.getLimit(), param.getOffset(), session);
	}

}