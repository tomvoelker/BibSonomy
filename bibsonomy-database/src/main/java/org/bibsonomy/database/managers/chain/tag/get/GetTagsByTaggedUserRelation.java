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
package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given tagged user relation of a given user.
 * 
 * @author fmi
 */
public class GetTagsByTaggedUserRelation extends TagChainElement {
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsByTaggedUserRelation(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.FRIEND &&
				present(param.getRequestedUserName()) && 
				// discriminate from the friendOf and ofFriend queries
				present(param.getRelationTags()) &&
				SystemTagsUtil.containsSystemTag(param.getRelationTags(), UserRelationSystemTag.NAME) &&
				!present(param.getRegex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getHash()));
	}
}