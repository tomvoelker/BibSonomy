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
package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author mwa
 */
public class GetResourcesPopularDaysCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getPopularDays(Bookmark.class, param.getDays(), session));
		}
		
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getPopularDays(BibTex.class, param.getDays(), session));
		}
		
		return new Statistics(0);
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
