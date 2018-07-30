package org.bibsonomy.database.managers;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.ProjectParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.validation.ProjectValidator;

import java.util.Date;
import java.util.List;

/**
 * database manager for creating, updating and queriying {@link Project}s
 *
 * @author dzo
 */
public class ProjectDatabaseManager extends AbstractDatabaseManager {

	private GeneralDatabaseManager generalDatabaseManager;

	private DatabasePluginRegistry plugins;

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

			final ProjectParam projectParam = new ProjectParam();

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

	private final String generateProjectId(final Project project, final DBSession session) {
		final String title = project.getTitle();


		return title.toLowerCase().replaceAll(" ", "");
	}

	public Project getProjectDetails(final String projectName, final boolean fullDetails, final DBSession session) {
		if (fullDetails) {
			return this.queryForObject("getFullProjectDetails", projectName, Project.class, session);
		}

		return this.queryForObject("getProjectDetails", Project.class, session);
	}

	/**
	 * @param generalDatabaseManager the generalDatabaseManager to set
	 */
	public void setGeneralDatabaseManager(GeneralDatabaseManager generalDatabaseManager) {
		this.generalDatabaseManager = generalDatabaseManager;
	}

	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(DatabasePluginRegistry plugins) {
		this.plugins = plugins;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(ProjectValidator validator) {
		this.validator = validator;
	}
}
