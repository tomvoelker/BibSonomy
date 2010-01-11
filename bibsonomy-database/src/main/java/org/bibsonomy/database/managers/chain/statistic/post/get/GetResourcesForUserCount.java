package org.bibsonomy.database.managers.chain.statistic.post.get;

import src.main.java.org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import src.main.java.org.bibsonomy.database.params.StatisticsParam;
import src.main.java.org.bibsonomy.database.util.DBSession;

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
			counts.add(this.db.getNumberOfResourcesForUser(BibTex.class, param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), session));
			
		} else if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
		
			counts.add(this.db.getNumberOfResourcesForUser(Bookmark.class, param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), session));
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
				nullOrEqual(param.getOrder(), Order.ADDED);
	}
}