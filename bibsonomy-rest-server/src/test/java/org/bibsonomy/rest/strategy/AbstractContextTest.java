package org.bibsonomy.rest.strategy;

import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;
import org.junit.Before;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractContextTest {

	protected LogicInterface db;

	/*
	 * If you override this method, remember to make a call to this method
	 * (super.setUp()) to make sure that this code is executed too.
	 */
	@Before
	public void setUp() {
		this.db = new TestDatabase();
	}
}