package org.bibsonomy.database.managers.chain.statistic.user;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.params.StatisticsParam;

/**
 * Chain of Responsibility for counts regarding user
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class TagUserChain implements FirstChainElement<Integer, StatisticsParam> {
	
	public ChainElement<Integer, StatisticsParam> getFirstElement() {
		return null;
	} 
}