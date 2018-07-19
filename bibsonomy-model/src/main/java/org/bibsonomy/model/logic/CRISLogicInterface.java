package org.bibsonomy.model.logic;

import org.bibsonomy.model.cris.Project;

import java.util.List;

/**
 * defines all interactions that are required for the
 * Current research information system
 *
 * @author dzo
 */
public interface CRISLogicInterface {

	public List<Project> getProjects();

	public boolean createProject(final Project project);

	public boolean updateProject(final String projectId, final Project project);

	public boolean deleteProject(final String projectId);
}

