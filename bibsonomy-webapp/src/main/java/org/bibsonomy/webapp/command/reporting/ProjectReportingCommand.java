package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.cris.Project;

import java.util.Collection;
import java.util.Date;

public class ProjectReportingCommand extends ReportingCommand {
	private String search, type;
	private Date startDate, endDate;
	private Collection<Project> projects;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Collection<Project> getProjects() {
		return projects;
	}

	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

	@Override
	public String getFilename() {
		return "projects";
	}
}
