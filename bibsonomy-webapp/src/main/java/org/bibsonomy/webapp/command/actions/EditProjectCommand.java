package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;

import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EditProjectCommand extends BaseCommand {

	private String projectIdToUpdate;

	private Project project;

	/**
	 * Project properties to edit
	 */
	private String title;
	private String subTitle;
	private URL homepage;
	private String description;
	private String type;
	private float budget;
	private Date startDate;
	private Date endDate;
	private Project parentProject;
	private List<Project> subProjects = new LinkedList<>();
	private List<CRISLink> crisLinks = new LinkedList<>();


	/**
	 * For some pages we need to store the referer to send the user back
	 * to that page.
	 */
	private String referer;

	/**
	 * @return the projectIdToUpdate
	 */
	public String getProjectIdToUpdate() {
		return projectIdToUpdate;
	}

	/**
	 * @param projectIdToUpdate the projectIdToUpdate to set
	 */
	public void setProjectIdToUpdate(String projectIdToUpdate) {
		this.projectIdToUpdate = projectIdToUpdate;
	}

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
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @return
	 */
	public URL getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage
	 */
	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public float getBudget() {
		return budget;
	}

	/**
	 * @param budget
	 */
	public void setBudget(String budget) {
		try {
			this.budget = Float.parseFloat(budget);
		} catch (NumberFormatException e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * @return
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return
	 */
	public Project getParentProject() {
		return parentProject;
	}

	/**
	 * @param parentProject
	 */
	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	/**
	 * @return
	 */
	public List<Project> getSubProjects() {
		return subProjects;
	}

	/**
	 * @param subProjects
	 */
	public void setSubProjects(List<Project> subProjects) {
		this.subProjects = subProjects;
	}

	/**
	 * @return
	 */
	public List<CRISLink> getCrisLinks() {
		return crisLinks;
	}

	/**
	 * @param crisLinks
	 */
	public void setCrisLinks(List<CRISLink> crisLinks) {
		this.crisLinks = crisLinks;
	}

	/**
	 * @return
	 */
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 *
	 * @return
	 */
	public String getUrl() {
		return "/editProject?projectIdToUpdate=" + this.projectIdToUpdate;
	}
}
