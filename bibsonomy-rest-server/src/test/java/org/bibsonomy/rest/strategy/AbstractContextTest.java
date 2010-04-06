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
	 * sets up the logic
	 */
	@Before
	public final void setUp() {
		this.db = TestDBLogic.factory.getLogicAccess(this.getClass().getSimpleName(), "apiKey");
		this.is = new ByteArrayInputStream("".getBytes());
	}
}