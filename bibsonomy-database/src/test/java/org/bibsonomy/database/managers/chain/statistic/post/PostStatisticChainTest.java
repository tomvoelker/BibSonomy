package org.bibsonomy.database.managers.chain.statistic.post;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashCount;
import org.junit.Test;

/**
 * Tests related to the bookmark chain.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class PostStatisticChainTest extends AbstractChainTest {

	/**
	 * tests getBookmarkByConceptForUser
	 */
//	@Test
//	public void getBookmarkByConceptForUser() {
//		this.bookmarkParam.setGrouping(GroupingEntity.USER);
//		this.bookmarkParam.setHash(null);
//		this.bookmarkParam.setOrder(null);
//		this.bookmarkParam.setRequestedUserName("hotho");
//		this.bookmarkParam.setNumSimpleConcepts(3);
//		this.bookmarkParam.setNumSimpleTags(0);
//		this.bookmarkParam.setNumTransitiveConcepts(0);
//		this.bookmarkChain.getFirstElement().perform(this.bookmarkParam, this.dbSession, this.chainStatus);
//		assertEquals(GetBookmarksByConceptForUser.class, this.chainStatus.getChainElement().getClass());
//	}

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
		
		final List<Integer> counts = this.postStatisticsChain.getFirstElement().perform(this.statisticsParam, this.dbSession, this.chainStatus);
		assertEquals(2, counts.get(0));
		assertEquals(GetResourcesForHashCount.class, this.chainStatus.getChainElement().getClass());
	}

}