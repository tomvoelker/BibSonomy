package org.bibsonomy.database.managers.chain.statistic.post;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.statistic.post.get.DefaultCatchAllCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesByTagNamesAndUserCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesByTagNamesCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesDuplicateCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForGroupCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashAndUserCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForUserCount;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesPopularDaysCount;
import org.bibsonomy.database.params.StatisticsParam;

/**
 * Chain of Responsibility for counts regarding posts
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class PostStatisticChain implements FirstChainElement<Integer, StatisticsParam> {

	private final GetResourcesForGroupCount getResourcesForGroupCount;
	private final GetResourcesForUserCount getResourcesForUserCount;
	private final GetResourcesByTagNamesAndUserCount getResourcesByTagNamesAndUserCount;
	private final GetResourcesByTagNamesCount getResourcesByTagNamesCount;
	private final GetResourcesDuplicateCount getResourcesDuplicateCount;
	//private final GetResourcesForUserAndGroupCount getResourcesForUserAndGroupCount;
	//private final GetResourcesForUserAndGroupByTagCount getResourcesForUserAndGroupByTagCount;
	private final GetResourcesPopularDaysCount getResourcesPopularDays;
	private final GetResourcesForHashCount getResourcesForHashCount;
	private final GetResourcesForHashAndUserCount getResourcesForHashAndUserCount;
	private final DefaultCatchAllCount defaultCatchAllCount;
	
	/**
	 * Default Constructor
	 */
	public PostStatisticChain() {
		getResourcesForGroupCount 	= new GetResourcesForGroupCount();
		getResourcesForUserCount	= new GetResourcesForUserCount();
		getResourcesByTagNamesAndUserCount = new GetResourcesByTagNamesAndUserCount();
		getResourcesByTagNamesCount = new GetResourcesByTagNamesCount();
		getResourcesDuplicateCount 	= new GetResourcesDuplicateCount();
		//getResourcesForUserAndGroupCount = new GetResourcesForUserAndGroupCount();
		//getResourcesForUserAndGroupByTagCount = new GetResourcesForUserAndGroupByTagCount();
		getResourcesPopularDays = new GetResourcesPopularDaysCount();
		getResourcesForHashCount = new GetResourcesForHashCount();
		getResourcesForHashAndUserCount = new GetResourcesForHashAndUserCount();
		defaultCatchAllCount = new DefaultCatchAllCount();
		
		getResourcesForGroupCount.setNext(getResourcesForUserCount);
		getResourcesForUserCount.setNext(getResourcesByTagNamesAndUserCount);
		getResourcesByTagNamesAndUserCount.setNext(getResourcesByTagNamesCount);
		getResourcesByTagNamesCount.setNext(getResourcesDuplicateCount);
		//getResourcesDuplicateCount.setNext(getResourcesForUserAndGroupCount);
		getResourcesDuplicateCount.setNext(getResourcesPopularDays);
		getResourcesPopularDays.setNext(getResourcesForHashCount);
		//getResourcesForUserAndGroupCount.setNext(getResourcesForUserAndGroupByTagCount);
		//getResourcesForUserAndGroupByTagCount.setNext(getResourcesPopularDays);
		getResourcesForHashCount.setNext(getResourcesForHashAndUserCount);
		getResourcesForHashAndUserCount.setNext(defaultCatchAllCount);
		
	}
	
	public ChainElement<Integer, StatisticsParam> getFirstElement() {
		return this.getResourcesForGroupCount;
	}
}