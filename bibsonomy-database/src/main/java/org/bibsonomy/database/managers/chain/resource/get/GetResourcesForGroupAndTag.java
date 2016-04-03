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
package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Returns a list of resources for a given group and common tags of a group.
 * 
 * @author Miranda Grahl
 * @param <R> 
 * @param <P> 
 */
public class GetResourcesForGroupAndTag<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return ((param.getGrouping() == GroupingEntity.GROUP) &&
				present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				present(param.getTagIndex()) &&
				(param.getNumSimpleConcepts() == 0) &&
				(param.getNumSimpleTags() > 0) &&
				(param.getNumTransitiveConcepts() == 0) &&
				!present(param.getHash()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()));
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
		if ((group == null) || (group.getGroupId() == GroupID.INVALID.getId()) || GroupID.isSpecialGroupId(group.getGroupId())) {
			log.debug("group '" + param.getRequestedGroupName() + "' not found or special group");
			return new ArrayList<Post<R>>();
		}
		return this.databaseManager.getPostsForGroupByTag(group.getGroupId(), param.getGroups(), param.getUserName(), param.getTagIndex(), param.getPostAccess(), param.getFilters(), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
	}

}
