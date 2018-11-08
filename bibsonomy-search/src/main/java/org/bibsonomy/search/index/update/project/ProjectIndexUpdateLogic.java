package org.bibsonomy.search.index.update.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.update.IndexUpdateLogic;

import java.util.Date;
import java.util.List;

/**
 * update logic for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexUpdateLogic implements IndexUpdateLogic<Project> {

	@Override
	public List<Project> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		return null;
	}

	@Override
	public List<Project> getDeletedEntities(Date lastLogDate) {
		return null;
	}
}
