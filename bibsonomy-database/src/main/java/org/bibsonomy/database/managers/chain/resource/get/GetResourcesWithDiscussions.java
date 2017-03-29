/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;

/**
 * Returns a list of resources for a given user.
 * 
 * @author Sven Stefani
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesWithDiscussions<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return ValidationUtils.safeContains(param.getFilters(), FilterEntity.POSTS_WITH_DISCUSSIONS) || 
				ValidationUtils.safeContains(param.getFilters(), FilterEntity.POSTS_WITH_DISCUSSIONS_UNCLASSIFIED_USER);
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		/*
		 * TODO: The determination of the groupId is conducted several times
		 * It would be better to determine it once and then pass it here, 
		 * and to the statistics chain (Count and DiscussionStatistics)
		 */
		if (GroupingEntity.GROUP.equals(param.getGrouping())) {
			final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
			if (!present(group) || (group.getGroupId() == GroupID.INVALID.getId()) || GroupID.isSpecialGroupId(group.getGroupId())) {
				log.debug("group '" + param.getRequestedGroupName() + "' not found or special group");
				return new ArrayList<Post<R>>();
			}
			return this.databaseManager.getPostsWithDiscussionsForGroup(param.getUserName(), group.getGroupId(), param.getGroups(), param.getFilters(), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
		}
		// handle all other grouping Entities (USER and ALL)
		return this.databaseManager.getPostsWithDiscussions(param.getUserName(), param.getRequestedUserName(), param.getGroups(), param.getFilters(), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
	}

}
