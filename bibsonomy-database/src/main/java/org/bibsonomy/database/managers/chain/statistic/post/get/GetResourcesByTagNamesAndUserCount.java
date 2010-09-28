package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.enums.Order;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesByTagNamesAndUserCount extends StatisticChainElement {

	@Override
	protected Integer handle(StatisticsParam param, DBSession session) {
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getNumberOfResourcesForUserAndTags(BibTex.class, param.getTagIndex(), param.getRequestedUserName(), param.getUserName(), param.getGroups(), session);
		}
		
		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return this.db.getNumberOfResourcesForUserAndTags(Bookmark.class, param.getTagIndex(), param.getRequestedUserName(), param.getUserName(), param.getGroups(), session);
		}
		return Integer.valueOf(0);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	param.getGrouping() == GroupingEntity.USER && 
		   		present(param.getTagIndex()) && 
		   		present(param.getRequestedUserName()) &&
		   		!present(param.getHash()) && 
		   		param.getNumSimpleConcepts() == 0 && 
		   		param.getNumSimpleTags() > 0 && 
		   		param.getNumTransitiveConcepts() == 0 && 
		   		nullOrEqual(param.getOrder(), Order.ADDED) && 
		   		!present(param.getSearch());
	}
}