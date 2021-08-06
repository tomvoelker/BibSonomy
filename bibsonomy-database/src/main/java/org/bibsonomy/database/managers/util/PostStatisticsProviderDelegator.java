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
package org.bibsonomy.database.managers.util;

import java.util.Map;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.StatisticsProvider;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * a delegator for the posts
 *
 * @author dzo
 */
public class PostStatisticsProviderDelegator implements StatisticsProvider<PostQuery<? extends Resource>> {

	private final Map<Class<? extends Resource>, StatisticsProvider<?>> resourceDatabaseManagers;

	/**
	 * default constructor
	 */
	public PostStatisticsProviderDelegator(final Map<Class<? extends Resource>, StatisticsProvider<?>> resourceDatabaseManagers) {
		this.resourceDatabaseManagers = resourceDatabaseManagers;
	}

	@Override
	public Statistics getStatistics(final PostQuery<? extends Resource> query, final User loggedinUser, final DBSession session) {
		return getStatistics(query, loggedinUser, session, this.resourceDatabaseManagers);
	}

	private static <R extends Resource> Statistics getStatistics(final PostQuery<R> query, final User loggedinUser, final DBSession session, Map<Class<? extends Resource>, StatisticsProvider<?>> resourceDatabaseManagers) {
		final Class<R> resourceClass = query.getResourceClass();
		final StatisticsProvider<PostQuery<? extends Resource>> postQueryStatisticsProvider = (StatisticsProvider<PostQuery<? extends Resource>>) resourceDatabaseManagers.get(resourceClass);
		return postQueryStatisticsProvider.getStatistics(query, loggedinUser, session);
	}

}
