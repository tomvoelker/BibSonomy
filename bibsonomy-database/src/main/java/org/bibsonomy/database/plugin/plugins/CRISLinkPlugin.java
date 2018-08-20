package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;

/**
 * handles updates regarding cris links
 * @author dzo
 */
public class CRISLinkPlugin extends AbstractDatabasePlugin {

	@Override
	public void onPersonDelete(final Person person, final DBSession session) {
		// log and delete all cris links belonging to the deleted person

		throw new UnsupportedOperationException("please implement me");
	}

	@Override
	public void onPersonUpdate(String personId, DBSession session) {
		// update person link

		throw new UnsupportedOperationException("please implement me");
	}

	@Override
	public void onProjectDelete(Project project, User loggedinUser, DBSession session) {
		// log link and delete all cris links belonging to the project

	}

	@Override
	public void onProjectUpdate(Project oldProject, Project newProject, User loggedinUser, DBSession session) {
		// update link to project
	}
}
