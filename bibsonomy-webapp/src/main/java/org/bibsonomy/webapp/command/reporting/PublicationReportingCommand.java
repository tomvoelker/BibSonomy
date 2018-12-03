package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class PublicationReportingCommand extends ReportingCommand {
	private List<Post<BibTex>> publications;
	private Date startDate, endDate;
	private Order order;
	private String groupingName;
	private Set<Filter> filters;

	public List<Post<BibTex>> getPublications() {
		return publications;
	}

	public void setPublications(List<Post<BibTex>> publications) {
		this.publications = publications;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

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
}
