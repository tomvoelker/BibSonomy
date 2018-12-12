package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;

/**
 * command to query projects
 *
 * @author dzo
 */
public class ProjectsPageCommand extends BaseCommand {

	private ListCommand<Project> projects = new ListCommand<>(this);

	private String search;

	private Prefix prefix;

	private ProjectStatus projectStatus = ProjectStatus.RUNNING;

	private ProjectOrder projectOrder = ProjectOrder.TITLE;

	private SortOrder sortOrder = SortOrder.ASC;

	/**
	 * @return the search
	 */
	public String getSearch() {
		return search;
	}

	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(Prefix prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the projectOrder
	 */
	public ProjectOrder getProjectOrder() {
		return projectOrder;
	}

	/**
	 * @param projectOrder the projectOrder to set
	 */
	public void setProjectOrder(ProjectOrder projectOrder) {
		this.projectOrder = projectOrder;
	}

	/**
	 * @return the projectStatus
	 */
	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus the projectStatus to set
	 */
	public void setProjectStatus(ProjectStatus projectStatus) {
		this.projectStatus = projectStatus;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * @return the projects
	 */
	public ListCommand<Project> getProjects() {
		return projects;
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(ListCommand<Project> projects) {
		this.projects = projects;
	}
}
