package org.bibsonomy.database.managers.chain.statistic.post.get;

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
 * @author mwa
 * @version $Id$
 */
public class GetResourcesPopularDaysCount extends StatisticChainElement {

	@Override
	protected Integer handle(StatisticsParam param, DBSession session) {
		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return this.db.getPopularDays(Bookmark.class, param.getDays(), session);
		}
		
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getPopularDays(BibTex.class, param.getDays(), session);
		}
		
		return Integer.valueOf(0);
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	param.getOrder() == Order.POPULAR &&
				param.getDays() >= 0 &&
				param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getHash()) &&
				!present(param.getSearch());
				
	}

}
