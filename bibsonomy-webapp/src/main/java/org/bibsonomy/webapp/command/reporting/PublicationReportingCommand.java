package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * @author pda
 */
public class PublicationReportingCommand extends ReportingCommand {
	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);
	private SortKey order;

	/**
	 * @return the order
	 */
	public SortKey getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(SortKey order) {
		this.order = order;
	}

	/**
	 * @return the publications
	 */
	public ListCommand<Post<GoldStandardPublication>> getPublications() {
		return publications;
	}

	@Override
	public String getFilename() {
		return "publications";
	}
}
