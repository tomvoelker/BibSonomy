package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns a list of resources for a given group.
 * 
 * @author Miranda Grahl
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesForGroup<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(P param) {
		return (param.getGrouping() == GroupingEntity.GROUP &&
				param.canHandle() &&
				present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()) &&
				!present(param.getAuthor()) &&
				!present(param.getTitle()));
	}

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
		if (!present(group) || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
			log.debug("group '" + param.getRequestedGroupName() + "' not found or special group");
			return new ArrayList<Post<R>>();			
		}
		
		return this.getDatabaseManagerForType(param.getClass()).getPostsForGroup(group.getGroupId(), param.getGroups(), param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

}
