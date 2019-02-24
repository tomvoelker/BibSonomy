package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * @author pda
 */
public class PublicationReportingCommand extends ReportingCommand {
	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);
	private Order order;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public String getFilename() {
		return "publications";
	}

	public ListCommand<Post<GoldStandardPublication>> getPublications() {
		return publications;
	}
}
