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
 * Returns a list of resources for a given hash.
 * 
 * @author Miranda Grahl
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesByHash<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return (present(param.getHash()) &&
				param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		if (present(param.getUserName())) {
			// user is logged in => all posts visible for the loginUser
			return this.getDatabaseManagerForType(param.getResourceClass()).getPostsByHash(param.getUserName(), param.getHash(), HashID.getSimHash(param.getSimHash()), GroupID.INVALID.getId(), param.getGroups(), param.getLimit(), param.getOffset(), session);
		} else {
			// no loginUser => only public posts (GroupID.INVALID.getId())
			return this.getDatabaseManagerForType(param.getResourceClass()).getPostsByHash(null, param.getHash(), HashID.getSimHash(param.getSimHash()), GroupID.PUBLIC.getId(), null, param.getLimit(), param.getOffset(), session);
		}
	}

}
