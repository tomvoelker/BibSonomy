/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns a list of resources for a given group (which is only viewable for
 * groupmembers excluded public option regarding setting a post).
 * 
 * @author Miranda Grahl
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesViewable<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return (present(param.getUserName()) &&
				(param.getGrouping() == GroupingEntity.VIEWABLE) &&
				present(param.getRequestedGroupName()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getSortKey(), SortKey.NONE, SortKey.DATE) &&
				!present(param.getSearch())) && 
				!present(param.getAuthor()) &&
				!present(param.getTitle());
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		final int groupId = this.groupDb.getGroupIdByGroupNameAndUserName(param.getRequestedGroupName(), param.getUserName(), session).intValue();
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found");
			return new ArrayList<Post<R>>(0);
		}
		
		if (present(param.getTagIndex())) {
			return this.databaseManager.getPostsViewableByTag(param.getRequestedGroupName(), param.getUserName(), param.getTagIndex(), groupId, param.getFilters(), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
		}
		
		return this.databaseManager.getPostsViewable(param.getRequestedGroupName(), param.getUserName(), groupId, HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
	}

}
