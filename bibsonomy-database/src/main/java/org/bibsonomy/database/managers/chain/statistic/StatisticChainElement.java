package org.bibsonomy.database.managers.chain.statistic;

import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class StatisticChainElement extends ChainElement<Statistics, StatisticsParam> {

	protected final StatisticsDatabaseManager db;

	/**
	 * Constructs a chain element
	 */
	public StatisticChainElement() {
		this.db = StatisticsDatabaseManager.getInstance();
	}
}