/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers.chain.statistic.user;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author dzo
 */
public class GetGroupMembershipHistoryCount extends StatisticChainElement {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#canHandle(java.lang.Object)
	 */
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.GROUP.equals(param.getGrouping()) && ValidationUtils.safeContains(param.getFilters(), FilterEntity.HISTORY);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#handle(java.lang.Object, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(db.getNumberOfGroupMembersInHistory(session));
	}

}
