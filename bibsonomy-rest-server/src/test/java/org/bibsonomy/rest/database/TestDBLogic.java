/*
 * Created on 29.06.2007
 */
package org.bibsonomy.rest.database;

import org.bibsonomy.database.DBLogic;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.LogicInterfaceFactory;
import org.junit.Ignore;

@Ignore
public class TestDBLogic extends DBLogic {
	
	public static final LogicInterfaceFactory factory = new LogicInterfaceFactory() {
		public LogicInterface getLogicAccess(String loginName, String apiKey) {
			return new TestDBLogic(loginName);
		}
	};

	public TestDBLogic(final String authUserName) {
		super(authUserName, new TestDatabase());
	}
}
