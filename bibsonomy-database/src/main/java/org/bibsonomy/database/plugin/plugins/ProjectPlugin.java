package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.LoggingParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;

/**
 * updates related to {@link Project}s
 * @author dzo
 */
public class ProjectPlugin extends AbstractDatabasePlugin {

	@Override
	public void onProjectUpdate(final Project oldProject, final Project newProject, final User loggedinUser, final DBSession session) {
		final LoggingParam param = new LoggingParam();
		param.setNewContentId(newProject.getId());
		param.setOldContentId(oldProject.getId());

		this.update("updateProjectParent", param, session);
	}
}
