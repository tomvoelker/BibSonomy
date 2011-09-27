package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of posts owned by  related users (restricted by the given tag). 
 * The system tag 'sys:network:bibsonomy-friends' corresponds to BibSonomy's 
 * friendship (=trust) relation.
 * 
 * @author fmi
 * @version $Id$
 * @param <R> the resource
 * @param <P> the param
 */
public class GetResourcesByTaggedUserRelation<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {
	
	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		return this.getDatabaseManagerForType(param.getResourceClass()).getPostsByTaggedUserRelation(param.getRequestedUserName(), param.getTagIndex(), param.getRelationTags(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final P param) {
		return (present(param.getUserName()) &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				// discriminate from the friendOf and ofFriend queries
				present(param.getRelationTags()) &&
				SystemTagsUtil.containsSystemTag(param.getRelationTags(), UserRelationSystemTag.NAME) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));		
	}
}