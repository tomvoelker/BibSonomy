package org.bibsonomy.model.cris;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * model representation of a project
 *
 * @author dzo
 */
public class Project implements Linkable {

	/** the database id */
	private int id;

	/** the external id of the project */
	private String externalId;

	/** the internal id of the project */
	private String internalId;

	/** the title of the project */
	private String title;

	/** the subtitle of the project */
	private String subTitle;

	/** the description of the project */
	private String description;

	/** the type of the type */
	private String type;

	/** the funding */
	private float budget;

	/** the start date */
	private Date startDate;

	/** the end date */
	private Date endDate;

	private List<Project> subProjects = new LinkedList<>();

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the internalId
	 */
	public String getInternalId() {
		return internalId;
	}

	/**
	 * @param internalId the internalId to set
	 */
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the budget
	 */
	public float getBudget() {
		return budget;
	}

	/**
	 * @param budget the budget to set
	 */
	public void setBudget(float budget) {
		this.budget = budget;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the subProjects
	 */
	public List<Project> getSubProjects() {
		return subProjects;
	}

	/**
	 * @param subProjects the subProjects to set
	 */
	public void setSubProjects(List<Project> subProjects) {
		this.subProjects = subProjects;
	}
}
