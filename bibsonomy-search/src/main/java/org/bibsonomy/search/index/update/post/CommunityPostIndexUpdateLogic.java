/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.update.post;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.params.SearchParam;

/**
 * index update logic for normal posts for the community logic
 * @param <R>
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements IndexUpdateLogic<Post<R>> {

	/**
	 * default constructor
	 *
	 * @param resourceClass the resource class
	 */
	public CommunityPostIndexUpdateLogic(final Class<R> resourceClass) {
		super(resourceClass, true);
	}

	/**
	 * default constructor
	 * @param resourceClass
	 * @param useSuperiorResourceClass
	 */
	public CommunityPostIndexUpdateLogic(Class<R> resourceClass, boolean useSuperiorResourceClass) {
		super(resourceClass, useSuperiorResourceClass);
	}

	@Override
	public List<Post<R>> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastEntityId);
			param.setLastLogDate(lastLogDate);
			param.setLimit(size);
			param.setOffset(offset);
			return (List<Post<R>>) this.queryForList("getNew" + this.getResourceName() + "Posts", param, session);
		}
	}

	@Override
	public List<Post<R>> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return (List<Post<R>>) this.queryForList("getDeleted" + this.getResourceName() + "Posts", lastLogDate, session);
		}
	}
}
