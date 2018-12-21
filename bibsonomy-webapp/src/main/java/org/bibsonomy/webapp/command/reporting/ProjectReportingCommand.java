package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.ListCommand;

public class ProjectReportingCommand extends ReportingCommand {
	private final ListCommand<Project> projects = new ListCommand<>(this);
	private String type, sponsor;
	private Person person;
	private Group organization;

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

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Group getOrganization() {
		return organization;
	}

	public void setOrganization(Group organization) {
		this.organization = organization;
	}
}
