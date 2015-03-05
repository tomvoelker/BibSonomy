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
package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.ChainUtils;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;
import org.bibsonomy.database.systemstags.search.YearSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author claus
 * @param <R>  the resource
 * @param <P>  the param
 */
public abstract class GetResourcesByResourceSearch<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		return ChainUtils.useResourceSearch(param);
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		// convert tag index to tag list
		final List<String> tagIndex = present(param.getTagIndex()) ? DatabaseUtils.extractTagNames(param) : null;

		/*
		 * extract first-, last- and year from the system tag if present
		 */
		String year = null;
		String firstYear = null;
		String lastYear = null;
		
		/*
		 * Check Systen tags for negated and year tags
		 */
		final List<String> negatedTags = new LinkedList<String>();

		for (final SystemTag systemTag : param.getSystemTags()) {
			if (systemTag instanceof YearSystemTag) {
				// this means, the last year system tag is taken
				final YearSystemTag yearTag = (YearSystemTag) systemTag;
				year = yearTag.getYear();
				firstYear = yearTag.getFirstYear();
				lastYear = yearTag.getLastYear();
			} else if (systemTag instanceof NotTagSystemTag) {
				negatedTags.add(((NotTagSystemTag) systemTag).getTagName());
			}
		}
		// query the resource searcher
		return this.databaseManager.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getRelationTags(), param.getGroupNames(),param.getSearchType(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, year, firstYear, lastYear, negatedTags, param.getOrder(), param.getLimit(), param.getOffset());
	}
}
