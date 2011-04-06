package org.bibsonomy.rest.database;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.junit.Ignore;

/**
 * @author dzo
 * @version $Id$
 */
@Ignore
public class TestDBLogicInterfaceFactory implements LogicInterfaceFactory {
	@Override
	public LogicInterface getLogicAccess(final String loginName, final String apiKey) {
		return new TestDBLogic(loginName);
	}
}
