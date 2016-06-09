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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.ChainUtils;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 */
public class GetTagsByResourceSearch extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final Collection<String> tags = present(param.getTagIndex()) ? DatabaseUtils.extractTagNames(param) : null;
		/*
		 * Check System tags for negated and year tags
		 */
		final List<String> negatedTags = new LinkedList<String>();
		if (present(param.getSystemTags())) {
			for (final SystemTag systemTag : param.getSystemTags()) {
				if (systemTag instanceof NotTagSystemTag) {
					negatedTags.add(((NotTagSystemTag) systemTag).getTagName());
				}
			}
		}
		return this.db.getTagsByResourceSearch(param.getResourceType(), param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getSearchType(), param.getSearch(), param.getTitle(), param.getAuthor(), tags, param.getBibtexKey(), null, null, null, negatedTags, param.getLimit(), param.getOffset());
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