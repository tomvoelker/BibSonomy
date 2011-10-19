package org.bibsonomy.database.managers.chain.statistic.post;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.statistic.post.get.DefaultCatchAllCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesByTagNamesAndUserCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesByTagNamesCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesDuplicateCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForGroupCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForUserCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForUserInboxCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesPopularDaysCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesWithDiscussionsCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetUserDiscussionsStatistics;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Chain of Responsibility for counts regarding posts
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class PostStatisticChain implements FirstChainElement<Statistics, StatisticsParam> {

	private final GetUserDiscussionsStatistics getUserDiscussionsStatistics;
	private final GetResourcesForGroupCount getResourcesForGroupCount;
	private final GetResourcesForUserCount getResourcesForUserCount;
	private final GetResourcesWithDiscussionsCount getResourcesWithDiscussionsCount;
	private final GetResourcesByTagNamesAndUserCount getResourcesByTagNamesAndUserCount;
	private final GetResourcesByTagNamesCount getResourcesByTagNamesCount;
	private final GetResourcesDuplicateCount getResourcesDuplicateCount;
	private final GetResourcesPopularDaysCount getResourcesPopularDays;
	private final GetResourcesForHashCount getResourcesForHashCount;
	private final GetResourcesForUserInboxCount getResourcesForUserInboxCount;
	private final DefaultCatchAllCount defaultCatchAllCount;
	
	/**
	 * Default Constructor
	 */
	public PostStatisticChain() {
		getUserDiscussionsStatistics = new GetUserDiscussionsStatistics(); 
		getResourcesForGroupCount 	= new GetResourcesForGroupCount();
		getResourcesWithDiscussionsCount = new GetResourcesWithDiscussionsCount();
		getResourcesForUserCount	= new GetResourcesForUserCount();
		getResourcesByTagNamesAndUserCount = new GetResourcesByTagNamesAndUserCount();
		getResourcesByTagNamesCount = new GetResourcesByTagNamesCount();
		getResourcesDuplicateCount 	= new GetResourcesDuplicateCount();
		getResourcesPopularDays = new GetResourcesPopularDaysCount();
		getResourcesForHashCount = new GetResourcesForHashCount();
		getResourcesForUserInboxCount = new GetResourcesForUserInboxCount();
		defaultCatchAllCount = new DefaultCatchAllCount();
		
		getUserDiscussionsStatistics.setNext(getResourcesForGroupCount);
		getResourcesForGroupCount.setNext(getResourcesWithDiscussionsCount);
		getResourcesWithDiscussionsCount.setNext(getResourcesForUserCount);
		getResourcesForUserCount.setNext(getResourcesByTagNamesAndUserCount);
		getResourcesByTagNamesAndUserCount.setNext(getResourcesByTagNamesCount);
		getResourcesByTagNamesCount.setNext(getResourcesDuplicateCount);
		getResourcesDuplicateCount.setNext(getResourcesPopularDays);
		getResourcesPopularDays.setNext(getResourcesForHashCount);
		getResourcesForHashCount.setNext(getResourcesForUserInboxCount);
		getResourcesForUserInboxCount.setNext(defaultCatchAllCount);
	}

	@Override
	public ChainElement<Statistics, StatisticsParam> getFirstElement() {
	    return this.getUserDiscussionsStatistics;
	}
}