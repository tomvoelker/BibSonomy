package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.DBLogicTest;
import org.bibsonomy.database.util.DBSession;

/**
 * Some kind of workaround for the {@link DBLogicTest} to use package-protected
 * methods from multiple packages.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class AbstractDBLogicBase extends AbstractDatabaseManagerTest {
	protected List<String> getUserNamesByGroupId(final GroupID groupId, final DBSession dbSession) {
		return this.userDb.getUserNamesByGroupId(groupId.getId(), dbSession);
	}
}