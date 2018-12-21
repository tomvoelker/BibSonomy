package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * organization page command
 *
 * @author dzo
 * @author pda
 */
public class OrganizationPageCommand extends BaseCommand {

	private String requestedOrganizationName;
	private Group group;

	private String sortPageOrder = "desc";
	private String sortPage = "date";

	private final ListCommand<Post<GoldStandardPublication>> bibtex = new ListCommand<>(this);
	private final ListCommand<Person> persons = new ListCommand<>(this);
	private final ListCommand<Project> projects = new ListCommand<>(this);

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

	public ListCommand<Post<GoldStandardPublication>> getBibtex() {
		return bibtex;
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

	public ListCommand<Project> getProjects() {
		return projects;
	}

}
