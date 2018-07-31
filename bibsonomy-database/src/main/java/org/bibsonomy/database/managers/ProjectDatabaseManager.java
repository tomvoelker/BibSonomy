package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingObjectErrorMessage;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.ProjectParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.validation.ProjectValidator;
import org.bibsonomy.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * database manager for creating, updating and queriying {@link Project}s
 *
 * @author dzo
 */
public class ProjectDatabaseManager extends AbstractDatabaseManager {

	/** used to get a new project id */
	private GeneralDatabaseManager generalDatabaseManager;

	/** to notify others about project changes */
	private DatabasePluginRegistry plugins;

	/** used to validate a project */
	private ProjectValidator validator;

	/**
	 * creates a project with the provided information
	 * @param loginUser
	 * @param project
	 * @param session
	 * @return
	 */
	public JobResult createProject(final Project project, final User loginUser, final DBSession session) {
		session.beginTransaction();
		try {
			final String projectId = this.generateProjectId(project, session);
			project.setExternalId(projectId);

			final List<ErrorMessage> errorMessages = this.validator.validateProject(project);
			if (errorMessages.size() > 1) {
				return JobResult.buildFailure(errorMessages);
			}

			// get the id of the parent project
			final ProjectParam projectParam = this.buildParam(project, session);

			if (!present(projectParam)) {
				return JobResult.buildFailure(Collections.singletonList(new MissingObjectErrorMessage(project.getParentProject().getExternalId(), "project.parent")));
			}

			final int dbId = this.generalDatabaseManager.getNewId(ConstantID.PROJECT_ID, session).intValue();
			project.setId(dbId);
			projectParam.setProject(project);
			projectParam.setUpdatedAt(new Date());
			projectParam.setUpdatedBy(loginUser.getName());

			this.plugins.onProjectInsert(project, session);
			this.insert("insertProject", projectParam, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	private ProjectParam buildParam(final Project project, final DBSession session) {
		final ProjectParam projectParam = new ProjectParam();
		final Project parentProject = project.getParentProject();
		if (present(parentProject)) {
			final String parentExternalId = parentProject.getExternalId();
			final Project parentProjectDetails = this.getProjectDetails(parentExternalId, true, session);

			if (!present(parentProjectDetails)) {
				return null;
			}

			projectParam.setParentProjectId(Integer.valueOf(parentProjectDetails.getId()));
		}

		return projectParam;
	}

	private final String generateProjectId(final Project project, final DBSession session) {
		final String title = project.getTitle();

		final String normedString = StringUtils.normalizeString(title);
		int counter = 1;
		String projectId = normedString;

		do {
			final Project projectInDB = this.getProjectDetails(projectId, true, session);
			if (!present(projectInDB)) {
				return projectId;
			}

			if (counter > 1000) {
				throw new RuntimeException("Too many project name occurences");
			}

			projectId = normedString + "." + counter;
			counter++;
		} while (true);
	}

	/**
	 * updates the given project
	 *
	 * @param externalProjectId
	 * @param project
	 * @param loggedInUser
	 * @param session
	 * @return
	 */
	public JobResult updateProject(final String externalProjectId, final Project project, final User loggedInUser, final DBSession session) {
		try {
			session.beginTransaction();

			// to ensure that the project id does not change
			project.setExternalId(externalProjectId);
			final int newID = this.generalDatabaseManager.getNewId(ConstantID.PROJECT_ID, session).intValue();

			final Project projectInDb = this.getProjectDetails(externalProjectId, true, session);
			if (!present(projectInDb)) {
				return JobResult.buildFailure(Collections.singletonList(new MissingObjectErrorMessage(externalProjectId, "project")));
			}

			// call the validation
			final List<ErrorMessage> validationResults = this.validator.validateProject(project);
			if (present(validationResults)) {
				return JobResult.buildFailure(validationResults);
			}

			final ProjectParam projectParam = this.buildParam(project, session);

			if (!present(projectParam)) {
				return JobResult.buildFailure(Collections.singletonList(new MissingObjectErrorMessage(project.getParentProject().getExternalId(), "project.parent")));
			}

			projectParam.setProject(project);
			project.setId(newID);
			projectParam.setUpdatedAt(new Date());
			projectParam.setUpdatedBy(loggedInUser.getName());

			// inform others about the project update
			this.plugins.onProjectUpdate(projectInDb, project, loggedInUser, session);
			this.update("updateProject", projectParam, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	/**
	 * deletes a project from the database
	 *
	 * @param externalProjectId
	 * @param loggedinUser
	 * @param session
	 * @return
	 */
	public JobResult deleteProject(final String externalProjectId, final User loggedinUser, final DBSession session) {
		try {
			session.beginTransaction();
			final Project projectInDb = this.getProjectDetails(externalProjectId, true, session);
			if (!present(projectInDb)) {
				return JobResult.buildFailure(Collections.singletonList(new MissingObjectErrorMessage(externalProjectId, "project")));
			}

			// inform others
			this.plugins.onProjectDelete(projectInDb, loggedinUser, session);

			this.delete("deleteProject", projectInDb.getId(), session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	/**
	 * returns details about a project given by the external project id
	 *
	 * if fullDetails is <code>true</code> than all details are returned (e.g. the budget of the project)
	 * @param externalProjectId
	 * @param fullDetails
	 * @param session
	 * @return
	 */
	public Project getProjectDetails(final String externalProjectId, final boolean fullDetails, final DBSession session) {
		final String statement = fullDetails ? "getFullProjectDetails" : "getProjectDetails";
		final Project project = this.queryForObject(statement, externalProjectId, Project.class, session);

		if (present(project)) {
			// get the subprojects
			final List<Project> subProjects = this.getProjectsByParentId(project.getId(), session);
			project.setSubProjects(subProjects);
		}

		return project;
	}

	/**
	 * retrieves a list of projects by parent id
	 * @param projectId
	 * @param session
	 * @return
	 */
	public List<Project> getProjectsByParentId(final int projectId, final DBSession session) {
		return this.queryForList("getProjectsByParentId", projectId, Project.class, session);
	}

	/**
	 * @param generalDatabaseManager the generalDatabaseManager to set
	 */
	public void setGeneralDatabaseManager(final GeneralDatabaseManager generalDatabaseManager) {
		this.generalDatabaseManager = generalDatabaseManager;
	}

	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(final DatabasePluginRegistry plugins) {
		this.plugins = plugins;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(final ProjectValidator validator) {
		this.validator = validator;
	}
}
