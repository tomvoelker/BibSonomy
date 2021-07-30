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


	@Override
	public String getFilename() {
		return "projects";
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
	 * @return the projects
	 */
	public ListCommand<Project> getProjects() {
		return projects;
	}
}
