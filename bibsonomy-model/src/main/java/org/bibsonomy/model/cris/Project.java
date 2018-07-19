package org.bibsonomy.model.cris;

import java.util.Date;
import java.util.List;

/**
 * model representation of a project
 *
 * @author dzo
 */
public class Project implements Linkable {

	/** the title of the project */
	private String title;

	/** the type of the type */
	private String type;

	/** the funding */
	private float budget;

	/** the start date */
	private Date startDate;

	/** the end date */
	private Date endDate;

	private List<Project> subProjects;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
