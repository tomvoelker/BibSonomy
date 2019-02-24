package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * command for project reporting page
 * @author pda
 */
public class ProjectReportingCommand extends ReportingCommand {
	private final ListCommand<Project> projects = new ListCommand<>(this);
	private String type;
	private String sponsor;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getFilename() {
		return "projects";
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public ListCommand<Project> getProjects() {
		return projects;
	}
}
