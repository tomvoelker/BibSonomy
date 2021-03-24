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
	public PostStatisticsProviderDelegator(Map<Class<? extends Resource>, StatisticsProvider<?>> resourceDatabaseManagers) {
		this.resourceDatabaseManagers = resourceDatabaseManagers;
	}

	@Override
	public Statistics getStatistics(PostQuery<? extends Resource> query, User loggedinUser, DBSession session) {
		return getStatistics(query, loggedinUser, session, this.resourceDatabaseManagers);
	}

	private static <R extends Resource> Statistics getStatistics(final PostQuery<R> query, final User loggedinUser, final DBSession session, Map<Class<? extends Resource>, StatisticsProvider<?>> resourceDatabaseManagers) {
		final Class<R> resourceClass = query.getResourceClass();
		final StatisticsProvider<PostQuery<? extends Resource>> postQueryStatisticsProvider = (StatisticsProvider<PostQuery<? extends Resource>>) resourceDatabaseManagers.get(resourceClass);
		return postQueryStatisticsProvider.getStatistics(query, loggedinUser, session);
	}


}
