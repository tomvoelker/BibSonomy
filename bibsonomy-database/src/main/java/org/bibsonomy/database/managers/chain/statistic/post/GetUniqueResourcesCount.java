package org.bibsonomy.database.managers.chain.statistic.post;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author dzo
 */
public class GetUniqueResourcesCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		final int contentType = param.getContentType();
		int count = 0;
		if (contentType == ConstantID.BIBTEX_CONTENT_TYPE.getId() || contentType == ConstantID.ALL_CONTENT_TYPE.getId()) {
			count += this.db.getNumberOfUniqueResources(BibTex.class, param.getStartDate(), param.getFilters(), session);
		}
		
		if (contentType == ConstantID.BOOKMARK_CONTENT_TYPE.getId() || contentType == ConstantID.ALL_CONTENT_TYPE.getId()) {
			count += this.db.getNumberOfUniqueResources(Bookmark.class, param.getStartDate(), param.getFilters(), session);
		}
		
		return new Statistics(count);
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.ALL.equals(param.getGrouping()) && !present(param.getRequestedUserName()) && ValidationUtils.safeContains(param.getFilters(), FilterEntity.UNIQUE);
	}
}