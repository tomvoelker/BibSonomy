package org.bibsonomy.database.managers.chain.project;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

/**
 * chain element to get all projects by the internal id
 *
 * @author dzo
 */
public class GetProjectsByInternalId extends ProjectChainElement {

	@Override
	protected List<Project> handle(ProjectQuery param, DBSession session) {
		return this.projectDatabaseManager.getProjectsByInternalId(param.getInternalId(), session);
	}

	@Override
	protected boolean canHandle(ProjectQuery param) {
		return present(param.getInternalId());
	}
}
