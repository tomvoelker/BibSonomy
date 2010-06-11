package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

/**
 *  Returns a list of resources that are related to the given concept name.
 * 
 * @author dzo
 * @version $Id$
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesByConceptByTag<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(P param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				param.canHandle() &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() > 0 &&
				param.getNumSimpleTags() == 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()) && 
				!present(param.getTitle()) &&
				!present(param.getAuthor()));
	}

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		return this.getDatabaseManagerForType(param.getClass()).getPostsByConceptByTag(param.getTagIndex(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

}
