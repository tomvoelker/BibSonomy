package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;

import java.util.Set;

public class PublicationReportingCommand extends ReportingCommand {
	private final ListCommand<Post<BibTex>> publications = new ListCommand<>(this);
	private Order order;
	private String groupingName;
	private Set<Filter> filters;
	private Person person;
	private Group organization;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getGroupingName() {
		return groupingName;
	}

	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	public Set<Filter> getFilters() {
		return filters;
	}

	public void setFilters(Set<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public String getFilename() {
		return "publications";
	}

	public ListCommand<Post<BibTex>> getPublications() {
		return publications;
	}

	public Group getOrganization() {
		return organization;
	}

	public void setOrganization(Group organization) {
		this.organization = organization;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
