package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.database.params.CRISLinkParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

/**
 * handles updates regarding cris links
 *
 * @author dzo
 */
public class CRISLinkPlugin extends AbstractDatabasePlugin {

	@Override
	public void onPersonUpdate(Person oldPerson, Person newPerson, DBSession session) {
		this.updateCRISLinks(oldPerson, newPerson, session);
	}

	@Override
	public void onPersonDelete(Person person, User user, DBSession session) {
		this.deleteCRISLinks(person, session);
	}

	@Override
	public void onProjectUpdate(Project oldProject, Project newProject, User loggedinUser, DBSession session) {
		this.updateCRISLinks(oldProject, newProject, session);
	}

	@Override
	public void onProjectDelete(final Project project, final User loggedinUser, final DBSession session) {
		this.deleteCRISLinks(project, session);
	}

	private CRISLinkParam createCRISParam(Linkable oldLink) {
		final CRISLinkParam param = new CRISLinkParam();
		final CRISEntityType type = CRISEntityType.getCRISEntityType(oldLink.getClass());
		param.setSourceType(type);
		param.setTargetType(type);

		param.setSourceId(oldLink.getId());
		param.setTargetId(oldLink.getId());
		return param;
	}

	private void updateCRISLinks(final Linkable oldLink, final Linkable newLink, final DBSession session) {
		final CRISLinkParam param = createCRISParam(oldLink);

		param.setNewContentId(newLink.getId().intValue());

		// update link to project
		this.update("updateAllCRISLinkSources", param, session);
		this.update("updateAllCRISLinkTargets", param, session);
	}

	private void deleteCRISLinks(final Linkable link, final DBSession session) {
		final CRISLinkParam param = createCRISParam(link);

		this.insert("logCRISLinkDelete", param, session);
		this.delete("deleteAllCrisLinks", param, session);
	}
}
