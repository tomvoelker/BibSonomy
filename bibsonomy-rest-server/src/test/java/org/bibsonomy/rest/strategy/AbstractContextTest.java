package org.bibsonomy.rest.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.database.TestDBLogic;
import org.junit.Before;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractContextTest {

	protected LogicInterface db;
	protected InputStream is;

	/**
	 * If you override this method, remember to make a call to this method
	 * (super.setUp()) to make sure that this code is executed too.
	 */
	@Before
	public void setUp() {
		this.db = TestDBLogic.factory.getLogicAccess(this.getClass().getSimpleName(), "apiKey");
		this.is = new ByteArrayInputStream("".getBytes());
	}
}