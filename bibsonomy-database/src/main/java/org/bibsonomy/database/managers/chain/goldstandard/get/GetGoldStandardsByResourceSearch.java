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
package org.bibsonomy.database.managers.chain.goldstandard.get;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.goldstandard.GoldStandardChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 * @param <RR> 
 * @param <R> 
 * @param <P> 
 */
public class GetGoldStandardsByResourceSearch<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends ResourceParam<RR>> extends GoldStandardChainElement<RR, R, P> {

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		ResourceSearch<R> search = this.databaseManager.getSearch();
		if (search == null) {
			// FIXME: should probably not be null, but was null for goldstandardbookmarks (test via http://localhost:8080/api/posts?resourcetype=goldstandardbookmark&sortPage=TITLE&sortOrder=ASC ) which might be at the wrong chain element here? 
			throw new UnsupportedResourceTypeException();
		}
		RR resource  = param.getResource();
		String resourceType = resource.getClass().getSimpleName();
		return search.getPosts(resourceType,param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), null, param.getGroupNames(), param.getSearchType(),param.getSearch(), param.getTitle(), param.getAuthor(), null, null, null, null, null, param.getOrder(), param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final P param) {
		return true; // TODO: adapt
	}
}
