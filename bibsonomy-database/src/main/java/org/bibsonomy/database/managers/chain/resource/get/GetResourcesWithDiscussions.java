package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns a list of resources for a given user.
 * 
 * @author Sven Stefani
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesWithDiscussions<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
	    
		return ( FilterEntity.POSTS_WITH_DISCUSSIONS.equals(param.getFilter())
			|| FilterEntity.POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER.equals(param.getFilter())
			
		);
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		/*
		 * TODO: The determination of the groupId is conducted several times
		 * It would be better to determine it once and then pass it here, 
		 * and to the statistics chain (Count and DiscussionStatistics)
		 */
		if (GroupingEntity.GROUP.equals(param.getGrouping())) {
			final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
			if (!present(group) || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
				log.debug("group '" + param.getRequestedGroupName() + "' not found or special group");
				return new ArrayList<Post<R>>();			
			}
			return this.databaseManager.getPostsWithDiscussionsForGroup(param.getUserName(), group.getGroupId(), param.getGroups(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
		}
		// handle all other grouping Entities (USER and ALL)
		return this.databaseManager.getPostsWithDiscussions(param.getUserName(), param.getRequestedUserName(), param.getGroups(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

}
