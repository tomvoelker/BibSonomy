package org.bibsonomy.search.index.update.project;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;
import java.util.List;

/**
 * TODO: merge with {@link org.bibsonomy.search.index.update.person.PersonIndexUpdateLogic}
 *
 * update logic for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<Project> {

	@Override
	public List<Project> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);

			return this.queryForList("getUpdatedAndNewProjects", param, Project.class, session);
		}
	}

	@Override
	public List<Project> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeletedProjects", lastLogDate, Project.class, session);
		}
	}
}
