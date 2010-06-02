package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
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
public class GetResourcesByTagNamesCount extends StatisticChainElement {

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		List<Integer> counts = new ArrayList<Integer>();
		
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForTags(BibTex.class, param.getTagIndex(), GroupID.PUBLIC.getId(), session));
		} else if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForTags(Bookmark.class, param.getTagIndex(), GroupID.PUBLIC.getId(), session));
		}
		return counts;
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {						
		return 	(param.getGrouping() == GroupingEntity.ALL) &&
				present(param.getTagIndex()) && 
				(param.getNumSimpleConcepts() == 0) && 
				(param.getNumSimpleTags() > 0) && 
				(param.getNumTransitiveConcepts() == 0) && 
				!present(param.getHash()) && 
				nullOrEqual(param.getOrder(), Order.ADDED, Order.FOLKRANK) && 
				!present(param.getSearch());
	}
}