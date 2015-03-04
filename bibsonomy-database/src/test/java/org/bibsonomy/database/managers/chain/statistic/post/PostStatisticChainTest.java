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
package org.bibsonomy.database.managers.chain.statistic.post;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.managers.chain.statistic.post.get.GetResourcesForHashCount;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to the post statistic chain.
 * 
 * @author Miranda Grahl
 */
public class PostStatisticChainTest extends AbstractDatabaseManagerTest {
	protected static Chain<Statistics, StatisticsParam> postStatisticsChain;
	
	/**
	 * sets up the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpChain() {
		postStatisticsChain = (Chain<Statistics, StatisticsParam>) testDatabaseContext.getBean("postStatisticChain");
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
		this.statisticsParam.setHash("097248439469d8f5a1e7fad6b02cbfcd");
		this.statisticsParam.setGrouping(GroupingEntity.ALL);
		this.statisticsParam.setRequestedUserName(null);
		this.statisticsParam.setTagIndex(null);
		this.statisticsParam.setOrder(null);
		this.statisticsParam.setSearch(null);
		
		final Statistics stats = postStatisticsChain.perform(this.statisticsParam, this.dbSession);
		assertEquals(2, stats.getCount());
		assertEquals(GetResourcesForHashCount.class, postStatisticsChain.getChainElement(this.statisticsParam).getClass());
	}

}