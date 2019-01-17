/**
 * BibSonomy-Model - Java- and JAXB-Model.
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;

import java.util.Date;

/**
 * the project query to retrieve projects from the logic
 *
 * @author dzo
 */
public class ProjectQuery extends BasicQuery {

	private final Prefix prefix;
	/**
	 * the order of the projects
	 */
	private final ProjectOrder order;
	/**
	 * the sort order of the order
	 */
	private final SortOrder sortOrder;
	/**
	 * the project status
	 */
	private final ProjectStatus projectStatus;
	/**
	 * the type of the project
	 */
	private final String type;
	private final String sponsor;
	private final String internalId;
	private final Date startDate;
	private final Date endDate;
	private final Person person;
	private final Group organization;

	/**
	 * the constructor
	 *
	 * @param order
	 * @param sortOrder
	 * @param projectStatus
	 * @param type
	 * @param start
	 * @param end
	 * @param externalId
	 * @param startDate
	 * @param endDate
	 * @param person
	 * @param organization
	 */
	protected ProjectQuery(final String search, final Prefix prefix, final ProjectOrder order, SortOrder sortOrder,
												 ProjectStatus projectStatus, String type, String sponsor,
												 int start, int end, String externalId, Date startDate, Date endDate,
												 Person person, Group organization) {
		this.person = person;
		this.organization = organization;
		this.setSearch(search);
		this.setStart(start);
		this.setEnd(end);

		this.prefix = prefix;
		this.startDate = startDate;
		this.endDate = endDate;
		this.order = order;
		this.sortOrder = sortOrder;
		this.projectStatus = projectStatus;
		this.type = type;
		this.internalId = externalId;
		this.sponsor = sponsor;
	}

	/**
	 * @return creates a new builder
	 */
	public static ProjectQueryBuilder createBuilder() {
		return new ProjectQueryBuilder();
	}

	public String getSponsor() {
		return sponsor;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return the order
	 */
	public ProjectOrder getOrder() {
		return order;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * @return the projectStatus
	 */
	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @return the internalId
	 */
	public String getInternalId() {
		return internalId;
	}

	/**
	 * @return the organization
	 */
	public Group getOrganization() {
		return organization;
	}

	public static class ProjectQueryBuilder {
		/**
		 * the order of the projects, default {@link ProjectOrder#TITLE}
		 */
		private ProjectOrder order = ProjectOrder.TITLE;

		/**
		 * the sort order of the order
		 */
		private SortOrder sortOrder = SortOrder.ASC;

		private Prefix prefix;

		/**
		 * the project status
		 */
		private ProjectStatus projectStatus;

		private String type;
		private String sponsor;

		/**
		 * the start
		 */
		private int start = 0;

		/**
		 * the end
		 */
		private int end = 10;

		/**
		 * the internalId
		 */
		private String internalId;

		/**
		 * the search
		 **/
		private String search;

		private Date startDate;

		private Date endDate;

		private Person person;
		private Group organization;

		/**
		 *
		 * @param person
		 * @return
		 */
		public ProjectQueryBuilder person(final Person person) {
			this.person = person;
			return this;
		}

		/**
		 *
		 * @param organization
		 * @return
		 */
		public ProjectQueryBuilder organization(final Group organization) {
			this.organization = organization;
			return this;
		}

		/**
		 * @param prefix the prefix to query
		 * @return the builder
		 */
		public ProjectQueryBuilder prefix(final Prefix prefix) {
			this.prefix = prefix;
			return this;
		}

		/**
		 * @param startDate
		 * @return
		 */
		public ProjectQueryBuilder startDate(final Date startDate) {
			this.startDate = startDate;
			return this;
		}

		/**
		 * @param endDate
		 * @return
		 */
		public ProjectQueryBuilder endDate(final Date endDate) {
			this.endDate = endDate;
			return this;
		}

		/**
		 * sets the internalId
		 *
		 * @param internalId
		 * @return
		 */
		public ProjectQueryBuilder internalId(final String internalId) {
			this.internalId = internalId;
			return this;
		}

		/**
		 * sets the start
		 *
		 * @param start
		 * @return
		 */
		public ProjectQueryBuilder start(final int start) {
			this.start = start;
			return this;
		}

		/**
		 * sets the end
		 *
		 * @param end
		 * @return
		 */
		public ProjectQueryBuilder end(final int end) {
			this.end = end;
			return this;
		}

		/**
		 * sets the order
		 *
		 * @param order
		 * @return
		 */
		public ProjectQueryBuilder order(final ProjectOrder order) {
			this.order = order;
			return this;
		}

		/**
		 * sets the sort order
		 *
		 * @param sortOrder
		 * @return
		 */
		public ProjectQueryBuilder sortOrder(final SortOrder sortOrder) {
			this.sortOrder = sortOrder;
			return this;
		}

		/**
		 * sets the project status
		 *
		 * @param projectStatus
		 * @return
		 */
		public ProjectQueryBuilder projectStatus(final ProjectStatus projectStatus) {
			this.projectStatus = projectStatus;
			return this;
		}

		/**
		 * sets the type
		 *
		 * @param type
		 * @return
		 */
		public ProjectQueryBuilder type(final String type) {
			this.type = type;
			return this;
		}

		/**
		 * @param search
		 * @return
		 */
		public ProjectQueryBuilder search(final String search) {
			this.search = search;
			return this;
		}

		/**
		 * @param sponsor
		 * @return
		 */
		public ProjectQueryBuilder sponsor(String sponsor) {
			this.sponsor = sponsor;
			return this;
		}

		/**
		 * @return the project query
		 */
		public ProjectQuery build() {
			return new ProjectQuery(this.search, this.prefix, this.order, this.sortOrder, this.projectStatus,
							this.type, this.sponsor, this.start, this.end, this.internalId, startDate, endDate, person, organization);
		}
	}
}
