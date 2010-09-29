package org.bibsonomy.database.managers.chain.statistic.post;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashCount;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to the post statistic chain.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class PostStatisticChainTest extends AbstractChainTest {
	protected static PostStatisticChain postStatisticsChain;
	
	/**
	 * sets up the chain
	 */
	@BeforeClass
	public static void setUpChain() {
		postStatisticsChain = new PostStatisticChain();
	}

	private StatisticsParam statisticsParam;
	
	/**
	 * creates a statistic param
	 */
	@Before
	public void createParam() {
		this.statisticsParam = ParamUtils.getDefaultStatisticsParam();
	}

	/**
	 * tests getBibtexByHash
	 */
	@Test
	public void getResourcesForHashCount() {
		this.statisticsParam.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		this.statisticsParam.setHash("d9eea4aa159d70ecfabafa0c91bbc9f0");
		this.statisticsParam.setGrouping(GroupingEntity.ALL);
		this.statisticsParam.setRequestedUserName(null);
		this.statisticsParam.setTagIndex(null);
		this.statisticsParam.setOrder(null);
		this.statisticsParam.setSearch(null);
		
		Integer count = postStatisticsChain.getFirstElement().perform(this.statisticsParam, this.dbSession, chainStatus);
		assertEquals(2, count);
		assertEquals(GetResourcesForHashCount.class, chainStatus.getChainElement().getClass());
	}

}