package org.bibsonomy.database.managers.chain.statistic.tag;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.params.StatisticsParam;

/**
 * Chain of Responsibility for counts regarding tags
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class TagStatisticChain implements FirstChainElement<Integer, StatisticsParam> {

	public TagStatisticChain() {

	}
	
	public ChainElement<Integer, StatisticsParam> getFirstElement() {
		return null;
	}
}