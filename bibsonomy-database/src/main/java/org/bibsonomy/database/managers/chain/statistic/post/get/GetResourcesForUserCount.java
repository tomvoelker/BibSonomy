package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.util.ValidationUtils.presentValidGroupId;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.enums.Order;

/**
 * Gets count of resources of a special user
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesForUserCount extends StatisticChainElement {

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		List<Integer> counts = new ArrayList<Integer>();
		
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForUser(BibTex.class, param, session));
		} else if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForUser(Bookmark.class, param, session));
		}
		return counts;
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	(param.getGrouping() == GroupingEntity.USER) && 
				present(param.getRequestedUserName()) && 
				!presentValidGroupId(param.getGroupId()) && 
				!present(param.getTagIndex()) && 
				!present(param.getHash()) && 
				nullOrEqual(param.getFilter(), FilterEntity.PDF) &&
				nullOrEqual(param.getOrder(), Order.ADDED) && 
				!present(param.getSearch());
	}
}