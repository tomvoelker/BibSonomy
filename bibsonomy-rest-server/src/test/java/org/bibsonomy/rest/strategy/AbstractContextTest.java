package org.bibsonomy.rest.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.database.TestDBLogic;
import org.junit.Before;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractContextTest {

	protected LogicInterface db;
	protected Reader is;

	/**
	 * sets up the logic
	 * @throws UnsupportedEncodingException 
	 */
	@Before
	public final void setUp() throws UnsupportedEncodingException {
		this.db = TestDBLogic.factory.getLogicAccess(this.getClass().getSimpleName(), "apiKey");
		this.is = new InputStreamReader(new ByteArrayInputStream("".getBytes()), "UTF-8");
	}
}