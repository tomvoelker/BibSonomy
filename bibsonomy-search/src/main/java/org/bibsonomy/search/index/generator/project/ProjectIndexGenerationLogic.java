package org.bibsonomy.search.index.generator.project;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.List;

/**
 * generation logic for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Project> {

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getProjectsCount", Integer.class, session);
		}
	}

	@Override
	public List<Project> getEntites(int lastContenId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastContenId);
			param.setLimit(limit);

			return this.queryForList("getProjects", param, Project.class, session);
		}
	}
}
