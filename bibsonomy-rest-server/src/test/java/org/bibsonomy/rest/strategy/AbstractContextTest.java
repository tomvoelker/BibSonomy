package org.bibsonomy.rest.strategy;

import junit.framework.TestCase;

import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractContextTest extends TestCase {

	protected LogicInterface db;

	/*
	 * If you override this method, remember to make a call to this method
	 * (super.setUp()) to make sure that this code is executed too.
	 */
	@Override
	protected void setUp() throws Exception {
		this.db = new TestDatabase();
	}
}