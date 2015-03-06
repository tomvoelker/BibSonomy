package org.bibsonomy.database.managers.chain.statistic.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetUniqueResourcesCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		final List<String> usersToExclude = getUsersToExclude(param);
		final int contentType = param.getContentType();
		int count = 0;
		if (contentType == ConstantID.BIBTEX_CONTENT_TYPE.getId() || contentType == ConstantID.ALL_CONTENT_TYPE.getId()) {
			count += this.db.getNumberOfUniqueResources(BibTex.class, param.getStartDate(), usersToExclude, session);
		}
		
		if (contentType == ConstantID.BOOKMARK_CONTENT_TYPE.getId() || contentType == ConstantID.ALL_CONTENT_TYPE.getId()) {
			count += this.db.getNumberOfUniqueResources(Bookmark.class, param.getStartDate(), usersToExclude, session);
		}
		
		return new Statistics(count);
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		final Set<StatisticsConstraint> constraints = param.getConstraints();
		return GroupingEntity.ALL.equals(param.getGrouping()) && !present(param.getRequestedUserName()) && present(constraints) && constraints.contains(StatisticsConstraint.UNIQUE);
	}
}