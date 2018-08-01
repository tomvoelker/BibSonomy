package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;

import java.util.Date;

/**
 * project param
 *
 * @author dzo
 */
public class ProjectParam extends GenericParam {

	private Project project;

	private String updatedBy;

	private Date updatedAt;

	private Integer parentProjectId;

	private ProjectStatus projectStatus;

	private ProjectOrder projectOrder;

	private SortOrder sortOrder;

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @return the parentProjectId
	 */
	public Integer getParentProjectId() {
		return parentProjectId;
	}

	/**
	 * @param parentProjectId the parentProjectId to set
	 */
	public void setParentProjectId(Integer parentProjectId) {
		this.parentProjectId = parentProjectId;
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
}
