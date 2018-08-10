package org.bibsonomy.model.logic;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * defines all interactions that are required for the
 * Current research information system
 *
 * @author dzo
 */
public interface CRISLogicInterface {

	/**
	 * retrieves a filterable list of projects.
	 * @param query
	 * @return
	 */
	public List<Project> getProjects(ProjectQuery query);

	/**
	 * Returns details to a project. A project is uniquely identified by the external project id.
	 * @param projectId
	 * @return
	 */
	public Project getProjectDetails(final String projectId);

	/**
	 * creates a new project
	 * @param project
	 * @return
	 */
	public JobResult createProject(final Project project);

	/**
	 * updates a project identified by it's external project id
	 * @param projectId
	 * @param project
	 * @return
	 */
	public JobResult updateProject(final String projectId, final Project project);

	/**
	 * deletes a project identified by it's external project id
	 * @param projectId
	 * @return
	 */
	public JobResult deleteProject(final String projectId);

	/**
	 * creates a link between cris entries
	 * @param link
	 * @return
	 */
	public JobResult createCRISLink(final CRISLink link);
}

