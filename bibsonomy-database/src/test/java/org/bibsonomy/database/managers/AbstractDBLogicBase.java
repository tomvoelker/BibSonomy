package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.DBLogicTest;
import org.bibsonomy.database.common.DBSession;
import org.junit.BeforeClass;

/**
 * Some kind of workaround for the {@link DBLogicTest} to use package-protected
 * methods from multiple packages.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class AbstractDBLogicBase extends AbstractDatabaseManagerTest {
	
	private static UserDatabaseManager userDb;
	
	/**
	 * sets up required managers
	 */
	@BeforeClass
	public static void setupManagers() {
		userDb = UserDatabaseManager.getInstance();
	}
	
	protected static List<String> getUserNamesByGroupId(final GroupID groupId, final DBSession dbSession) {
		return userDb.getUserNamesByGroupId(groupId.getId(), dbSession);
	}
}