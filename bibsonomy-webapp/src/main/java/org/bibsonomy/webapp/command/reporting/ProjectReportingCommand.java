package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.cris.Project;

import java.util.Collection;
import java.util.Date;

public class ProjectReportingCommand extends ReportingCommand {
	private String type;
	private Collection<Project> projects;

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
