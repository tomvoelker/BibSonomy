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
package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given group.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 */
public class GetTagsByGroup extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupName(param.getRequestedGroupName(), session);
		if (groupId == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Tag>(0);
		}
		param.setGroupId(groupId);
		return this.db.getTagsByGroup(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP &&
				present(param.getRequestedGroupName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getRegex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) 
				);
	}
}