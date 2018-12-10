/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	private Integer id;

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

	/** the type of the project */
	private String type;

	private String sponsor;

	/** the funding */
	private Float budget;

	/** the start date */
	private Date startDate;

	/** the end date */
	private Date endDate;

	/** the parent project */
	private Project parentProject;

	/** sub projects of the project */
	private List<Project> subProjects = new LinkedList<>();

	/** cris links that are connected to this project */
	private List<CRISLink> crisLinks = new LinkedList<>();

	/**
	 * @return the id
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
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
	 * @return the sponsor
	 */
	public String getSponsor() {
		return sponsor;
	}

	/**
	 * @param sponsor the sponsor to set
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	/**
	 * @return the budget
	 */
	public Float getBudget() {
		return budget;
	}

	/**
	 * @param budget the budget to set
	 */
	public void setBudget(Float budget) {
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
	 * @return the parentProject
	 */
	public Project getParentProject() {
		return parentProject;
	}

	/**
	 * @param parentProject the parentProject to set
	 */
	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
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

	/**
	 * @return the crisLinks
	 */
	public List<CRISLink> getCrisLinks() {
		return crisLinks;
	}

	/**
	 * @param crisLinks the crisLinks to set
	 */
	public void setCrisLinks(List<CRISLink> crisLinks) {
		this.crisLinks = crisLinks;
	}

	@Override
	public String getLinkableId() {
		return this.getExternalId();
	}
}
