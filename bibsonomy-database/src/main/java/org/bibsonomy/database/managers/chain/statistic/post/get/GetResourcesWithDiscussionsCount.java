/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.ValidationUtils;

/**
 * Gets count of resources of a special user
 * 
 * @author Stefan Stützer
 */
public class GetResourcesWithDiscussionsCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		if (GroupingEntity.GROUP.equals(param.getGrouping())) {
			final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
			if (group == null || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
				log.debug("group " + param.getRequestedGroupName() + " not found or special group");
				return new Statistics(0);
			}
			if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
				return new Statistics(this.db.getNumberOfResourcesWithDiscussionsForGroup(BibTex.class, group.getGroupId(), param.getUserName(), param.getGroups(), session));
			}
			
			if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
				return new Statistics(this.db.getNumberOfResourcesWithDiscussionsForGroup(Bookmark.class, group.getGroupId(), param.getUserName(), param.getGroups(), session));
			}
		}

		// all other (USER and ALL)
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getNumberOfResourcesWithDiscussions(BibTex.class, param.getRequestedUserName(), param.getUserName(), param.getGroups(), session));
		}

		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getNumberOfResourcesWithDiscussions(Bookmark.class, param.getRequestedUserName(), param.getUserName(), param.getGroups(), session));
		}
		
		return new Statistics(0);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return ValidationUtils.safeContains(param.getFilters(), FilterEntity.POSTS_WITH_DISCUSSIONS);
	}
}