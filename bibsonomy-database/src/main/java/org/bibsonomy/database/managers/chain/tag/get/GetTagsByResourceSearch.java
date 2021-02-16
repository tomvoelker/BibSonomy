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

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.ChainUtils;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.services.query.PostSearchQuery;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 */
public class GetTagsByResourceSearch extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final List<String> tags = present(param.getTagIndex()) ? DatabaseUtils.extractTagNames(param) : null;
		/*
		 * Check System tags for negated and year tags
		 */
		final List<String> negatedTags = new LinkedList<>();
		if (present(param.getSystemTags())) {
			for (final SystemTag systemTag : param.getSystemTags()) {
				if (systemTag instanceof NotTagSystemTag) {
					negatedTags.add(((NotTagSystemTag) systemTag).getTagName());
				}
			}
		}

		// FIXME: year restrictions missing

		final PostSearchQuery<?> query = new PostSearchQuery<>();
		query.setNegatedTags(negatedTags);
		query.setAuthorSearchTerms(param.getAuthor());
		query.setTitleSearchTerms(param.getTitle());
		query.setSearch(param.getSearch());
		query.setGrouping(getGroupingEntity(param));
		query.setBibtexKey(param.getBibtexKey());
		query.setScope(param.getQueryScope());
		query.setTags(tags);

		BasicQueryUtils.setStartAndEnd(query, param.getLimit(), param.getOffset());

		return this.db.getTagsByResourceSearch(param.getResourceType(), param.getLoggedinUser(), query);
	}

	private static GroupingEntity getGroupingEntity(TagParam param) {
		if (present(param.getRequestedUserName())) {
			return GroupingEntity.USER;
		}

		if (present(param.getRequestedGroupName())) {
			return GroupingEntity.GROUP;
		}

		return GroupingEntity.ALL;
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (!present(param.getBibtexKey()) && 
				!present(param.getRegex()) && 
				!present(param.getHash()) && 
				!present(param.getTagRelationType()) && 
				(present(param.getSearch()) || present(param.getTitle()) || present(param.getAuthor()) || ChainUtils.useResourceSearch(param)));
	}
}