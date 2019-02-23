package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * @author pda
 */
public class PublicationReportingCommand extends ReportingCommand {
	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);
	private Order order;
	private String groupingName;
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

	@Override
	public String getFilename() {
		return "publications";
	}

	public ListCommand<Post<GoldStandardPublication>> getPublications() {
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
