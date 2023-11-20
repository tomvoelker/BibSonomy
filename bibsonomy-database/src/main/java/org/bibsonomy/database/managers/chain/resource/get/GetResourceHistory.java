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

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.QueryBasedResourceChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * get the history of the specified post
 *
 * @author dzo
 */
public class GetResourceHistory<R extends Resource> extends QueryBasedResourceChainElement<R> {

	@Override
	protected List<Post<R>> handle(final QueryAdapter<PostQuery<R>> param, final DBSession session) {
		final PostQuery<R> query = param.getQuery();

		final int limit = BasicQueryUtils.calcLimit(query);
		final int offset = BasicQueryUtils.calcOffset(query);

		return this.databaseManager.getPostsWithHistory(query.getHash(), query.getGroupingName(), limit, offset, session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<PostQuery<R>> param) {
		final PostQuery<R> query = param.getQuery();
		return ValidationUtils.safeContains(query.getFilters(), FilterEntity.HISTORY);
	}
}
