package org.bibsonomy.search.index.generator.project;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;

import java.util.List;

/**
 * generation logic for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Project> {

	@Override
	public int getNumberOfEntities() {
		return 0;
	}

	@Override
	public List<Project> getEntites(int lastContenId, int limit) {
		return null;
	}
}
