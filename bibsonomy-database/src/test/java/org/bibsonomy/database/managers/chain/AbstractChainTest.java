package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.junit.Before;

/**
 * Abstract class to test the chains. It reuses the database connection setup/clean methods
 * of {@link AbstractDatabaseManagerTest}. 
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractChainTest extends AbstractDatabaseManagerTest {
	
	protected ChainStatus chainStatus;
	
	/**
	 * resets the chain status
	 */
	@Before
	public void resetChainStatus() {
		this.chainStatus = new ChainStatus();
	}
}