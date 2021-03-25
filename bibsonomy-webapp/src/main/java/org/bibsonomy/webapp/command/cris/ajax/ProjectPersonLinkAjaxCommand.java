package org.bibsonomy.webapp.command.cris.ajax;

import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * command to link project with person
 * @author dzo
 */
public class ProjectPersonLinkAjaxCommand extends AjaxCommand<String> {
	private String projectId;
	private String personId;
	private ProjectPersonLinkType linkType;

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
	}

	/**
	 * @return the linkType
	 */
	public ProjectPersonLinkType getLinkType() {
		return linkType;
	}

	/**
	 * @param linkType the linkType to set
	 */
	public void setLinkType(ProjectPersonLinkType linkType) {
		this.linkType = linkType;
	}
}
