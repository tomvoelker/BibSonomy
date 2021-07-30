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

	private OrganizationPageSubPage subPage = OrganizationPageSubPage.INFO;

	private String requestedOrganizationName;
	private Group group;

	private String sortPageOrder = "desc";
	private String sortPage = "date";

	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);
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

	/**
	 * @return the subPage
	 */
	public OrganizationPageSubPage getSubPage() {
		return subPage;
	}

	/**
	 * @param subPage the subPage to set
	 */
	public void setSubPage(OrganizationPageSubPage subPage) {
		this.subPage = subPage;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return the publications
	 */
	public ListCommand<Post<GoldStandardPublication>> getPublications() {
		return publications;
	}

	/**
	 * @return the sortPageOrder
	 */
	public String getSortPageOrder() {
		return sortPageOrder;
	}

	/**
	 * @param sortPageOrder the sortPageOrder to set
	 */
	public void setSortPageOrder(String sortPageOrder) {
		this.sortPageOrder = sortPageOrder;
	}

	/**
	 * @return the sortPage
	 */
	public String getSortPage() {
		return sortPage;
	}

	/**
	 * @param sortPage the sortPage to set
	 */
	public void setSortPage(String sortPage) {
		this.sortPage = sortPage;
	}

	/**
	 * @return the persons
	 */
	public ListCommand<Person> getPersons() {
		return persons;
	}

	/**
	 * @return the projects
	 */
	public ListCommand<Project> getProjects() {
		return projects;
	}
}
