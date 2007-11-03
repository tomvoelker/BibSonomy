package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.DBLogicTest;
import org.bibsonomy.database.util.DBSession;


/**
 * Some kind of workaroud for the {@link DBLogicTest} to use package-protected methods
 * from multiple packages
 *  
 * @version $Id$
 * @author  Jens Illig
 * $Author$
 */
public class AbstractDBLogicBase extends AbstractDatabaseManagerTest {
	protected List<String> getUserNamesByGroupId(final GroupID groupId, final DBSession dbSession) {
		return userDb.getUserNamesByGroupId( groupId.getId(), dbSession);
	}
}
