package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * @author dzo, pda
 */
public class OrganizationPageCommand extends BaseCommand {

	private String requestedOrganizationName;
	private Group group;
	private ListCommand<Post<? extends Resource>> bibtex;
	private String sortPageOrder = "desc";
	private String sortPage = "date";
	private ListCommand<Person> persons;
	private ListCommand<Project> projects;

	/**
	 * @return the requestedOrganizationName
	 */
	public String getRequestedOrganizationName() {
		return this.requestedOrganizationName;
	}

	/**
	 * @param requestedOrganizationName the requestedOrganizationName to set
	 */
	public void setRequestedOrganizationName(String requestedOrganizationName) {
		this.requestedOrganizationName = requestedOrganizationName;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public ListCommand<Post<? extends Resource>> getBibtex() {
		return bibtex;
	}

	public void setBibtex(ListCommand<Post<? extends Resource>> bibtex) {
		this.bibtex = bibtex;
	}

	public String getSortPageOrder() {
		return sortPageOrder;
	}

	public void setSortPageOrder(String sortPageOrder) {
		this.sortPageOrder = sortPageOrder;
	}

	public String getSortPage() {
		return sortPage;
	}

	public void setSortPage(String sortPage) {
		this.sortPage = sortPage;
	}

	public ListCommand<Person> getPersons() {
		return persons;
	}

	public void setPersons(ListCommand<Person> persons) {
		this.persons = persons;
	}

	public ListCommand<Project> getProjects() {
		return projects;
	}

	public void setProjects(ListCommand<Project> projects) {
		this.projects = projects;
	}
}
